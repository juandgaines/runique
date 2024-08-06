@file:OptIn(ExperimentalMaterial3Api::class)

package com.juandgaines.run.presentation.run_overview

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juandgaines.core.presentation.designsystem.AnalyticsIcon
import com.juandgaines.core.presentation.designsystem.LogoIcon
import com.juandgaines.core.presentation.designsystem.LogoutIcon
import com.juandgaines.core.presentation.designsystem.RunIcon
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import com.juandgaines.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.juandgaines.core.presentation.designsystem.components.RuniqueScaffold
import com.juandgaines.core.presentation.designsystem.components.RuniqueToolbar
import com.juandgaines.core.presentation.designsystem.components.util.DropDownItem
import com.juandgaines.run.presentation.R
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnAnalyticsClick
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnLogoutClick
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnStartClick
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit = {},
    viewModel: RunOverviewViewModel = koinViewModel(),
) {
    RunOverviewScreen(
        onAction = {
            when(it){
                OnStartClick -> onStartRunClick()
                else-> Unit
            }
            viewModel.onAction(it)
        }
    )
}
@Composable
fun RunOverviewScreen(
    onAction : (RunOverviewAction) -> Unit = {},
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state = topAppBarState)
    RuniqueScaffold (
        topAppBar = {
            RuniqueToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.runique),
                scrollBehavior = scrollBehavior,
                menuItems = listOf(
                    DropDownItem(
                        icon =  AnalyticsIcon,
                        title = stringResource(id = R.string.analytics)
                    ),
                    DropDownItem(
                        icon =  LogoutIcon,
                        title = stringResource(id = R.string.logout)
                    )
                ),
                onMenuItemClick = { index ->
                    when(index) {
                        0 -> onAction(OnAnalyticsClick)
                        1 -> onAction(OnLogoutClick)
                    }
                },
                startContent = {
                    Icon(
                        imageVector = LogoIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            )
        }, floatingActionButton ={
            RuniqueFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(OnStartClick)
                },
            )
    }
    ) { paddingValues ->

    }
}

@Preview
@Composable
fun RunOverviewScreenRootPreview() {
    RuniqueTheme {
        RunOverviewScreen()
    }
}