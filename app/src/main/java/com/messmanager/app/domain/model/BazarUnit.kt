package com.messmanager.app.domain.model

import androidx.annotation.StringRes
import com.messmanager.app.R

enum class BazarUnit(
    @StringRes val labelRes: Int,
    @StringRes val symbolRes: Int,
) {
    GRAM(R.string.unit_gram, R.string.unit_gram_symbol),
    KG(R.string.unit_kg, R.string.unit_kg_symbol),
    LITER(R.string.unit_liter, R.string.unit_liter_symbol),
    ML(R.string.unit_ml, R.string.unit_ml_symbol),
    PIECE(R.string.unit_piece, R.string.unit_piece_symbol),
    DOZEN(R.string.unit_dozen, R.string.unit_dozen_symbol),
    PACKET(R.string.unit_packet, R.string.unit_packet_symbol),
}
