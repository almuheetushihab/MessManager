package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class MealType(@StringRes val labelRes: Int) {
    BREAKFAST(R.string.meal_breakfast),
    LUNCH(R.string.meal_lunch),
    DINNER(R.string.meal_dinner),
}
