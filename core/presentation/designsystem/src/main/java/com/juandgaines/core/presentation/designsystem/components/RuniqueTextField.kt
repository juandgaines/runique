@file:OptIn(ExperimentalFoundationApi::class)

package com.juandgaines.core.presentation.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juandgaines.core.presentation.designsystem.CheckIcon
import com.juandgaines.core.presentation.designsystem.EmailIcon
import com.juandgaines.core.presentation.designsystem.RuniqueTheme

@Composable
fun RuniqueTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState(),
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    hint: String,
    title: String?,
    error: String? = null,
    keyBoardType: KeyboardType = KeyboardType.Text,
    additionalInfo: String? = null,
) {
    var isFocus by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        Row (
            modifier= Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ){
            if(title != null){
               Text(
                   text =  title,
                   color = MaterialTheme.colorScheme.onSurfaceVariant,
               )
            }
            if(error != null){
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }else if(additionalInfo != null){
                Text(
                    text = additionalInfo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        BasicTextField2(
            state = state,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyBoardType
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isFocus) MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.05f
                    ) else MaterialTheme.colorScheme.surface
                )
                .border(
                    width = 1.dp,
                    color = if (isFocus) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .onFocusChanged {
                    isFocus = it.isFocused
                },
            decorator = { innerBox ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically
                ) {
                    if (startIcon != null) {
                        Icon(
                            imageVector = startIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ){
                        if (state.text.isEmpty()) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.4f
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }
                    if(endIcon != null){
                        Spacer(modifier = Modifier.padding(16.dp))
                        Icon(
                            imageVector = endIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

            }

        )
    }
}

@Preview
@Composable
fun RuniqueTextFieldPreview() {
    RuniqueTheme {
        RuniqueTextField(
            state = rememberTextFieldState(),
            startIcon = EmailIcon,
            title = "Email",
            hint = "example@email.com",
            endIcon = CheckIcon,
            additionalInfo = "Must be a valid email address",
            modifier = Modifier.fillMaxWidth()
        )
    }
}