package com.messmanager.app.feature.deposit

import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod

data class DepositFormState(
    val member: Member? = null,
    val amount: String = "",
    val method: PaymentMethod = PaymentMethod.CASH,
    val memberError: UiText? = null,
    val amountError: UiText? = null,
)

data class DepositUiState(
    val isLoading: Boolean = true,
    val items: List<Deposit> = emptyList(),
    val members: List<Member> = emptyList(),
    val form: DepositFormState? = null,
)
