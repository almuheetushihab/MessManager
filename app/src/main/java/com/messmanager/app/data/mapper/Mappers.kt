package com.messmanager.app.data.mapper

import com.messmanager.app.data.local.entity.BazarEntity
import com.messmanager.app.data.local.entity.DepositEntity
import com.messmanager.app.data.local.entity.MealEntity
import com.messmanager.app.data.local.entity.MemberEntity
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod

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

fun BazarEntity.toDomain() = Bazar(
    id = id,
    epochDay = epochDay,
    amount = amount,
    category = runCatching { BazarCategory.valueOf(category) }.getOrDefault(BazarCategory.OTHER),
    note = note,
)

fun Bazar.toEntity() = BazarEntity(
    id = id,
    epochDay = epochDay,
    amount = amount,
    category = category.name,
    note = note,
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
