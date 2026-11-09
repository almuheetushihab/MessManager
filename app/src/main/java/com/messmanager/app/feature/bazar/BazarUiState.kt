package com.messmanager.app.feature.bazar

import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory

data class BazarFormState(
    val amount: String = "",
    val note: String = "",
    val category: BazarCategory = BazarCategory.GROCERY,
    val amountError: UiText? = null,
)

data class BazarUiState(
    val isLoading: Boolean = true,
    val items: List<Bazar> = emptyList(),
    val form: BazarFormState? = null,
)
