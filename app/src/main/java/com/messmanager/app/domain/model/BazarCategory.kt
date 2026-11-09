package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class BazarCategory(@StringRes val labelRes: Int) {
    FISH_MEAT(R.string.category_fish_meat),
    VEGETABLE(R.string.category_vegetable),
    GROCERY(R.string.category_grocery),
    GAS(R.string.category_gas),
    OTHER(R.string.category_other),
}
