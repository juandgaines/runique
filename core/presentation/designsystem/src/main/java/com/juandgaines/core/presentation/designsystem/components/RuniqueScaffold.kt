package com.juandgaines.core.presentation.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RuniqueScaffold(
    modifier: Modifier = Modifier,
    withGradient: Boolean = false,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content : @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
    ) { paddingValues ->
        if(withGradient) {
            GradientBackground{
                content(paddingValues)
            }
        } else {
            content(paddingValues)
        }

    }
}