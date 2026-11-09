package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class PaymentMethod(@StringRes val labelRes: Int) {
    CASH(R.string.method_cash),
    BKASH(R.string.method_bkash),
    NAGAD(R.string.method_nagad),
    BANK(R.string.method_bank),
}
