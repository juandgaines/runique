package com.juandgaines.auth.presentation.intro

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandgaines.auth.presentation.R
import com.juandgaines.core.presentation.designsystem.LogoIcon
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import com.juandgaines.core.presentation.designsystem.components.GradientBackground
import com.juandgaines.core.presentation.designsystem.components.RuniqueActionButton
import com.juandgaines.core.presentation.designsystem.components.RuniqueOutlinedActionButton

@Composable
fun IntroScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
){
    IntroScreen(
        onAction = {
            when(it){
                IntroAction.OnSignInClick -> onSignInClick()
                IntroAction.OnSignUpClick -> onSignUpClick()
            }
        }
    )
}
@Composable
fun IntroScreen (
    onAction: (IntroAction) -> Unit
){
    GradientBackground{
        Box (
            Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ){
            RuniqueVerticalLogo()
        }
        Column (
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp),
        ){
            Text(
                text = stringResource(id = R.string.welcome_to_runique),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.runique_description),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier =   Modifier.height(32.dp))
            RuniqueOutlinedActionButton(
                text = stringResource(id = R.string.sign_in),
                modifier = Modifier
                    .fillMaxWidth(),
                isLoading = false,
                onClick = {
                    onAction(IntroAction.OnSignInClick)
                }
            )
            Spacer(modifier =   Modifier.height(16.dp))
            RuniqueActionButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sign_up),
                isLoading = false,
                onClick = {
                    onAction(IntroAction.OnSignUpClick)
                }
            )

        }
    }
}

@Composable
fun RuniqueVerticalLogo(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = LogoIcon,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.runique),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview() {
    RuniqueTheme {
        IntroScreen(
            onAction = {}
        )
    }
}