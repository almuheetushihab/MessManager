package com.messmanager.app.domain.model

data class MealEntry(
    val memberId: Long,
    val epochDay: Long,
    val breakfast: Boolean = false,
    val lunch: Boolean = false,
    val dinner: Boolean = false,
    val guests: Int = 0,
) {
    val totalCount: Int
        get() = listOf(breakfast, lunch, dinner).count { it } + guests

    fun isOn(type: MealType): Boolean = when (type) {
        MealType.BREAKFAST -> breakfast
        MealType.LUNCH -> lunch
        MealType.DINNER -> dinner
    }

    fun toggle(type: MealType): MealEntry = when (type) {
        MealType.BREAKFAST -> copy(breakfast = !breakfast)
        MealType.LUNCH -> copy(lunch = !lunch)
        MealType.DINNER -> copy(dinner = !dinner)
    }

    fun changeGuests(delta: Int): MealEntry = copy(guests = (guests + delta).coerceAtLeast(0))
}
