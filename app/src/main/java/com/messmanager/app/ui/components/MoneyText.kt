package com.messmanager.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R

@Composable
fun formatMoney(amount: Double): String = stringResource(R.string.amount_format, amount)
