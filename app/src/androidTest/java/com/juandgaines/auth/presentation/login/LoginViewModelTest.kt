@file:OptIn(ExperimentalFoundationApi::class)

package com.juandgaines.auth.presentation.login

import androidx.compose.foundation.ExperimentalFoundationApi
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.juandgaines.auth.data.AuthRepositoryImpl
import com.juandgaines.auth.data.EmailPatternValidator
import com.juandgaines.auth.data.LoginRequest
import com.juandgaines.auth.domain.UserDataValidator
import com.juandgaines.core.android_test.SessionStorageFake
import com.juandgaines.core.android_test.TestMockEngine
import com.juandgaines.core.android_test.loginResponseStub
import com.juandgaines.core.data.networking.HttpClientFactory
import com.juandgaines.test.MainCoroutineExtension
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LoginViewModelTest{
    private lateinit var viewModel: LoginViewModel
    private lateinit var authRepository: AuthRepositoryImpl

    private var sessionStorageFake = SessionStorageFake()
    private lateinit var mockEngine : TestMockEngine

    companion object{
        @JvmField
        @RegisterExtension
        val mainCoroutineDispatcher = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp(){
        sessionStorageFake = SessionStorageFake()
        val mockEngineConfig = MockEngineConfig().apply {
            requestHandlers.add{ request ->
                val relativeUrl = request.url.encodedPath
                if(relativeUrl == "/login"){
                    respond(
                        content = ByteReadChannel(
                            text = Json.encodeToString(
                                loginResponseStub
                            )
                        ),
                        headers = headers {
                            set("Content-Type", "application/json")
                        }
                    )
                }
                else{
                    respond(
                        content = byteArrayOf(),
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
        }
        mockEngine = TestMockEngine(
            dispatcher = mainCoroutineDispatcher.testDispatcher,
            mockEngineConfig = mockEngineConfig

        )
        val httpClient = HttpClientFactory(sessionStorageFake)
            .build(mockEngine)

        authRepository = AuthRepositoryImpl(
            httpClient = httpClient,
            sessionStorage = sessionStorageFake
        )
        viewModel = LoginViewModel(
            authRepository = authRepository,
            userDataValidator = UserDataValidator(
                patternValidator = EmailPatternValidator
            )
        )
    }

    @Test
    fun testLogin() = runTest {
        assertThat(viewModel.state.canLogin).isFalse()
        viewModel.state.email.edit {
            append("test@test.com")
        }

        viewModel.state.password.edit {
            append("Test12345")
        }

        viewModel.onAction(LoginAction.OnLoginClick)

        assertThat(viewModel.state.isLoggingIn).isFalse()
        assertThat(viewModel.state.email.text.toString()).isEqualTo("test@test.com")
        assertThat(viewModel.state.password.text.toString()).isEqualTo("Test12345")

        val loginRequest = mockEngine.mockEngine.requestHistory.find {
            it.url.encodedPath == "/login"
        }
        assertThat(loginRequest).isNotNull()
        assertThat(loginRequest!!.headers.contains("x-api-key")).isTrue()

        val loginBody = Json.decodeFromString<LoginRequest>(
            loginRequest.body.toByteArray().decodeToString()
        )

        assertThat(loginBody.email).isEqualTo("test@test.com")
        assertThat(loginBody.password).isEqualTo("Test12345")

        val session = sessionStorageFake.get()

        assertThat(session?.userId).isEqualTo(loginResponseStub.userId)
        assertThat(session?.accessToken).isEqualTo(loginResponseStub.accessToken)
        assertThat(session?.refreshToken).isEqualTo(loginResponseStub.refreshToken)
    }
}