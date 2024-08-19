package com.juandgaines.runique

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.juandgaines.auth.presentation.intro.IntroScreenRoot
import com.juandgaines.auth.presentation.login.LoginScreenRoot
import com.juandgaines.auth.presentation.register.RegisterScreenRoot
import com.juandgaines.run.presentation.active_run.ActiveRunScreenRoot
import com.juandgaines.run.presentation.active_run.ActiveRunService
import com.juandgaines.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController =  navController,
        startDestination = if(isLoggedIn) "run" else "auth"
    ) {
        runGraph(navController)
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
                    navController.navigate("run_overview"){
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

private fun NavGraphBuilder.runGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = "run_overview",
        route = "run"
    ){
        composable(route = "run_overview"){
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate("active_run")
                },
                onLogOutClick = {
                    navController.navigate("auth"){
                        popUpTo("run"){
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "active_run",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "runique://active_run"
                }
            )
        ){
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onFinished = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if(shouldServiceRun){
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context,
                                MainActivity::class.java
                            )
                        )
                    }
                    else{
                        context.stopService(
                            ActiveRunService.createStopIntent(context)
                        )
                    }
                }
            )
        }
    }
}