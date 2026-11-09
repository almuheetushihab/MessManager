package com.messmanager.app.feature.bazar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.repository.MessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BazarViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val form = MutableStateFlow<BazarFormState?>(null)

    val uiState: StateFlow<BazarUiState> = combine(repository.observeBazar(), form) { items, form ->
        BazarUiState(isLoading = false, items = items, form = form)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), BazarUiState())

    fun onAddClick() = form.update { BazarFormState() }
    fun onDismissForm() = form.update { null }
    fun onAmountChange(value: String) = form.update { it?.copy(amount = value, amountError = null) }
    fun onNoteChange(value: String) = form.update { it?.copy(note = value) }
    fun onCategoryChange(value: BazarCategory) = form.update { it?.copy(category = value) }

    fun onSave() {
        val current = form.value ?: return
        val amount = current.amount.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            form.update { it?.copy(amountError = UiText.Resource(R.string.error_invalid_amount)) }
            return
        }
        viewModelScope.launch {
            repository.addBazar(
                Bazar(
                    epochDay = LocalDate.now().toEpochDay(),
                    amount = amount,
                    category = current.category,
                    note = current.note.trim(),
                ),
            )
            form.update { null }
        }
    }

    fun onDelete(bazar: Bazar) {
        viewModelScope.launch { repository.deleteBazar(bazar.id) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
