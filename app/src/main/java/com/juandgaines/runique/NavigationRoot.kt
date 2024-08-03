package com.juandgaines.runique

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.juandgaines.auth.presentation.intro.IntroScreenRoot
import com.juandgaines.auth.presentation.login.LoginScreenRoot
import com.juandgaines.auth.presentation.register.RegisterScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController =  navController,
        startDestination = "auth"
    ) {
        authGraph(navController)

    }

}
private fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ){
        composable(route = "intro"){
          IntroScreenRoot(
              onSignInClick = {
                  navController.navigate("login")
              },
              onSignUpClick = {
                  navController.navigate("register")
              }
          )
        }
        composable(route = "register"){
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login"){
                        popUpTo("register"){
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("login")
                }
            )
        }
        composable(route = "login"){
            LoginScreenRoot(
                onLoginSuccess = {
                    navController.navigate("run"){
                        popUpTo("auth"){
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("register"){
                        popUpTo("login"){
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}