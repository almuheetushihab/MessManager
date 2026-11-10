package com.messmanager.app.feature.shared_expense

import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
import java.time.LocalDate

data class CustomShareInput(
    val memberId: Long,
    val memberName: String,
    val amountText: String,
)

data class SharedExpenseFormState(
    val id: Long = 0,
    val epochDay: Long = LocalDate.now().toEpochDay(),
    val title: String = "",
    val type: SharedExpenseType = SharedExpenseType.WIFI,
    val amountText: String = "",
    val splitType: SplitType = SplitType.EQUAL,
    val paidByMemberId: Long? = null,
    val customShares: List<CustomShareInput> = emptyList(),
    val titleError: String? = null,
    val amountError: String? = null,
    val sharesError: String? = null,
) {
    val isEdit: Boolean get() = id != 0L
}

data class SharedExpenseUiState(
    val items: List<SharedExpense> = emptyList(),
    val isLoading: Boolean = false,
    val isMonthClosed: Boolean = false,
    val members: List<Member> = emptyList(),
    val form: SharedExpenseFormState? = null,
    val recentlyDeletedExpense: SharedExpense? = null,
    val showSavedMessage: Boolean = false,
)
