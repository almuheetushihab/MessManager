package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class SharedExpenseType(@StringRes val labelRes: Int) {
    GAS(R.string.shared_expense_type_gas),
    WIFI(R.string.shared_expense_type_wifi),
    ELECTRICITY(R.string.shared_expense_type_electricity),
    MAID(R.string.shared_expense_type_maid),
    RENT(R.string.shared_expense_type_rent),
    OTHER(R.string.shared_expense_type_other),
}
