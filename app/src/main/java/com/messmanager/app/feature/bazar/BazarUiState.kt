package com.messmanager.app.feature.bazar

import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentType
import java.time.LocalDate

data class BazarFormState(
    val id: Long = 0,
    val amount: String = "",
    val quantity: String = "",
    val category: BazarCategory = BazarCategory.GROCERY,
    val unit: BazarUnit = BazarCategory.GROCERY.defaultUnit,
    val allowedUnits: List<BazarUnit> = BazarCategory.GROCERY.allowedUnits,
    val paymentType: PaymentType = PaymentType.CASH,
    val creditShopName: String = "",
    val paidByMemberId: Long? = null,
    val dateEpochDay: Long = LocalDate.now().toEpochDay(),
    val note: String = "",
    val amountError: UiText? = null,
    val shopNameError: UiText? = null,
) {
    val isEditMode: Boolean get() = id > 0
}

data class BazarUiState(
    val isLoading: Boolean = true,
    val items: List<Bazar> = emptyList(),
    val members: List<Member> = emptyList(),
    val form: BazarFormState? = null,
    val recentlyDeletedBazar: Bazar? = null,
)
