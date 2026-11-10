package com.messmanager.app.data.mapper

import com.messmanager.app.data.local.entity.BazarEntity
import com.messmanager.app.data.local.entity.DepositEntity
import com.messmanager.app.data.local.entity.MealEntity
import com.messmanager.app.data.local.entity.MemberEntity
import com.messmanager.app.data.local.entity.SharedExpenseEntity
import com.messmanager.app.data.local.entity.SharedExpenseShareEntity
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseShare
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType

fun MemberEntity.toDomain() = Member(id = id, name = name, phone = phone)
fun Member.toEntity() = MemberEntity(id = id, name = name, phone = phone)

fun MealEntity.toDomain() = MealEntry(
    memberId = memberId,
    epochDay = epochDay,
    breakfast = breakfast,
    lunch = lunch,
    dinner = dinner,
    guests = guests,
)

fun MealEntry.toEntity() = MealEntity(
    memberId = memberId,
    epochDay = epochDay,
    breakfast = breakfast,
    lunch = lunch,
    dinner = dinner,
    guests = guests,
)

fun BazarEntity.toDomain(paidByMemberName: String? = null) = Bazar(
    id = id,
    epochDay = epochDay,
    amount = amount,
    category = runCatching { BazarCategory.valueOf(category) }.getOrDefault(BazarCategory.OTHER),
    note = note,
    quantity = quantity,
    unit = unit?.let { u -> runCatching { BazarUnit.valueOf(u) }.getOrNull() },
    paymentType = runCatching { PaymentType.valueOf(paymentType) }.getOrDefault(PaymentType.CASH),
    creditShopName = creditShopName,
    isCreditSettled = isCreditSettled,
    paidByMemberId = paidByMemberId,
    paidByMemberName = paidByMemberName,
    photoUri = photoUri,
)

fun Bazar.toEntity() = BazarEntity(
    id = id,
    epochDay = epochDay,
    amount = amount,
    category = category.name,
    note = note,
    quantity = quantity,
    unit = unit?.name,
    paymentType = paymentType.name,
    creditShopName = creditShopName,
    isCreditSettled = isCreditSettled,
    paidByMemberId = paidByMemberId,
    photoUri = photoUri,
)

fun DepositEntity.toDomain(memberName: String) = Deposit(
    id = id,
    memberId = memberId,
    memberName = memberName,
    epochDay = epochDay,
    amount = amount,
    method = runCatching { PaymentMethod.valueOf(method) }.getOrDefault(PaymentMethod.CASH),
)

fun Deposit.toEntity() = DepositEntity(
    id = id,
    memberId = memberId,
    epochDay = epochDay,
    amount = amount,
    method = method.name,
)

fun SharedExpenseEntity.toDomain(
    paidByMemberName: String? = null,
    shares: List<SharedExpenseShare> = emptyList(),
) = SharedExpense(
    id = id,
    epochDay = epochDay,
    title = title,
    type = runCatching { SharedExpenseType.valueOf(type) }.getOrDefault(SharedExpenseType.OTHER),
    amount = amount,
    splitType = runCatching { SplitType.valueOf(splitType) }.getOrDefault(SplitType.EQUAL),
    paidByMemberId = paidByMemberId,
    paidByMemberName = paidByMemberName,
    shares = shares,
)

fun SharedExpense.toEntity() = SharedExpenseEntity(
    id = id,
    epochDay = epochDay,
    title = title,
    type = type.name,
    amount = amount,
    splitType = splitType.name,
    paidByMemberId = paidByMemberId,
)

fun SharedExpenseShareEntity.toDomain(memberName: String? = null) = SharedExpenseShare(
    expenseId = expenseId,
    memberId = memberId,
    shareAmount = shareAmount,
    memberName = memberName,
)

fun SharedExpenseShare.toEntity(expenseId: Long = this.expenseId) = SharedExpenseShareEntity(
    expenseId = expenseId,
    memberId = memberId,
    shareAmount = shareAmount,
)
