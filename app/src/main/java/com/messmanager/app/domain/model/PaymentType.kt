package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class PaymentType(@StringRes val labelRes: Int) {
    CASH(R.string.payment_type_cash),
    CREDIT(R.string.payment_type_baki),
}
