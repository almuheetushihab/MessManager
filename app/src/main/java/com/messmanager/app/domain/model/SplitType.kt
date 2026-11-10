package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class SplitType(@StringRes val labelRes: Int) {
    EQUAL(R.string.shared_expense_split_equal),
    CUSTOM(R.string.shared_expense_split_custom),
}
