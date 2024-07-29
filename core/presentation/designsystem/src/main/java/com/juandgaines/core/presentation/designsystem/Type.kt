package com.juandgaines.core.presentation.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.plcoding.core.presentation.designsystem.R

val Poppins = FontFamily(
     Font(
         resId = R.font.poppins_light,
         weight = FontWeight.Light,
     ),
    Font(
        resId = R.font.poppins_regular,
        weight = FontWeight.Normal,
    ),
    Font(
        resId = R.font.poppins_medium,
        weight = FontWeight.Medium,
    ),
    Font(
        resId = R.font.poppins_semibold,
        weight = FontWeight.SemiBold,
    ),
    Font(
        resId = R.font.poppins_bold,
        weight = FontWeight.Bold,
    ),
)

val Typography= Typography(
    bodySmall = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 20.sp,
        color = RuniqueGray
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = RuniqueGray
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 20.sp,
        color = RuniqueWhite
    ),


)