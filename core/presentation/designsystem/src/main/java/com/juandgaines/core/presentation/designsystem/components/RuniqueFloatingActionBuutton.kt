package com.juandgaines.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juandgaines.core.presentation.designsystem.RunIcon
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import com.plcoding.core.presentation.designsystem.R

@Composable
fun RuniqueFloatingActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = null,
    iconSize: Dp = 25.dp
) {
    Box(
        modifier = modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .clickable {
                onClick()
            },
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Box (
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primary
                )
                .padding(12.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ){
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(iconSize)
            )

        }
    }
}

@Preview
@Composable
fun RuniqueFloatingActionButtonPreview() {
    RuniqueTheme {
        RuniqueFloatingActionButton(
            icon = RunIcon,
            onClick = {}
        )
    }
}