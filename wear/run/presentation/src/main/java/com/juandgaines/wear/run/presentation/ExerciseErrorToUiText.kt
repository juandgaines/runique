package com.juandgaines.wear.run.presentation

import com.juandgaines.core.presentation.ui.UiText
import com.juandgaines.wear.run.domain.ExerciseError

fun ExerciseError.toUiText():UiText?{
    return when(this){
        ExerciseError.ONGOING_OWN_EXERCISE,
        ExerciseError.ONGOING_OTHER_EXERCISE -> UiText.StringResource(R.string.error_ongoing_exercise)
        ExerciseError.EXERCISE_ALREADY_ENDED -> UiText.StringResource(R.string.error_exercise_already_ended)
        ExerciseError.UNKNOWN -> UiText.StringResource(com.juandgaines.core.presentation.ui.R.string.error_unknown)
        ExerciseError.TRACKING_NOT_SUPPORTED -> null
    }
}