package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class BazarCategory(
    @StringRes val labelRes: Int,
    val allowedUnits: List<BazarUnit>,
    val defaultUnit: BazarUnit,
) {
    FISH_MEAT(
        labelRes = R.string.category_fish_meat,
        allowedUnits = listOf(BazarUnit.KG, BazarUnit.GRAM),
        defaultUnit = BazarUnit.KG,
    ),
    VEGETABLE(
        labelRes = R.string.category_vegetable,
        allowedUnits = listOf(BazarUnit.KG, BazarUnit.GRAM, BazarUnit.PIECE),
        defaultUnit = BazarUnit.KG,
    ),
    GROCERY(
        labelRes = R.string.category_grocery,
        allowedUnits = listOf(BazarUnit.KG, BazarUnit.GRAM, BazarUnit.LITER, BazarUnit.ML, BazarUnit.PACKET, BazarUnit.PIECE),
        defaultUnit = BazarUnit.KG,
    ),
    GAS(
        labelRes = R.string.category_gas,
        allowedUnits = listOf(BazarUnit.PIECE),
        defaultUnit = BazarUnit.PIECE,
    ),
    OTHER(
        labelRes = R.string.category_other,
        allowedUnits = BazarUnit.entries,
        defaultUnit = BazarUnit.PIECE,
    ),
}
