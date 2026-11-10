package com.messmanager.app.core.preview

import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.*
import com.messmanager.app.feature.bazar.BazarFormState
import com.messmanager.app.feature.bazar.BazarUiState
import com.messmanager.app.feature.deposit.DepositFormState
import com.messmanager.app.feature.deposit.DepositUiState
import com.messmanager.app.feature.home.HomeUiState
import com.messmanager.app.feature.meal.MealUiState
import com.messmanager.app.feature.meal.MemberMealUi
import com.messmanager.app.feature.members.MemberFormState
import com.messmanager.app.feature.members.MembersUiState
import com.messmanager.app.feature.report.ReportUiState
import com.messmanager.app.feature.shared_expense.SharedExpenseFormState
import com.messmanager.app.feature.shared_expense.SharedExpenseUiState
import java.time.LocalDate
import java.time.YearMonth

object PreviewData {
    val members = listOf(
        Member(id = 1, name = "Rahim Uddin", phone = "01711223344"),
        Member(id = 2, name = "Karim Ahmed", phone = "01822334455"),
        Member(id = 3, name = "Tanvir Hasan", phone = "01933445566"),
        Member(id = 4, name = "Sakib Al Hasan", phone = "01644556677"),
        Member(id = 5, name = "Mahmudullah Riyad", phone = "01555667788"),
    )

    val todayEpoch = LocalDate.now().toEpochDay()

    val mealEntries = listOf(
        MealEntry(memberId = 1, epochDay = todayEpoch, breakfast = true, lunch = true, dinner = true, guests = 1),
        MealEntry(memberId = 2, epochDay = todayEpoch, breakfast = true, lunch = true, dinner = false, guests = 0),
        MealEntry(memberId = 3, epochDay = todayEpoch, breakfast = false, lunch = true, dinner = true, guests = 2),
        MealEntry(memberId = 4, epochDay = todayEpoch, breakfast = true, lunch = false, dinner = true, guests = 0),
        MealEntry(memberId = 5, epochDay = todayEpoch, breakfast = false, lunch = false, dinner = false, guests = 0),
    )

    val memberMealUis = members.map { member ->
        MemberMealUi(
            member = member,
            entry = mealEntries.find { it.memberId == member.id } ?: MealEntry(memberId = member.id, epochDay = todayEpoch),
        )
    }

    val bazarItems = listOf(
        Bazar(
            id = 1,
            epochDay = todayEpoch,
            amount = 650.0,
            category = BazarCategory.FISH_MEAT,
            quantity = 1.5,
            unit = BazarUnit.KG,
            paymentType = PaymentType.CASH,
            paidByMemberId = 1L,
            paidByMemberName = "Rahim Uddin",
            note = "Hilsa fish",
        ),
        Bazar(
            id = 2,
            epochDay = todayEpoch - 1,
            amount = 250.0,
            category = BazarCategory.VEGETABLE,
            quantity = 3.0,
            unit = BazarUnit.KG,
            paymentType = PaymentType.CREDIT,
            creditShopName = "Desh General Store",
            isCreditSettled = false,
            note = "Potatoes & Onions",
        ),
        Bazar(
            id = 3,
            epochDay = todayEpoch - 2,
            amount = 400.0,
            category = BazarCategory.GROCERY,
            quantity = 5.0,
            unit = BazarUnit.KG,
            paymentType = PaymentType.CASH,
            paidByMemberId = 2L,
            paidByMemberName = "Karim Ahmed",
            note = "Rice and oil",
        ),
        Bazar(
            id = 4,
            epochDay = todayEpoch - 3,
            amount = 1200.0,
            category = BazarCategory.GAS,
            quantity = 1.0,
            unit = BazarUnit.PIECE,
            paymentType = PaymentType.CREDIT,
            creditShopName = "Bhai Bhai Gas Store",
            isCreditSettled = true,
            note = "LPG Cylinder",
        ),
        Bazar(
            id = 5,
            epochDay = todayEpoch - 4,
            amount = 150.0,
            category = BazarCategory.OTHER,
            quantity = 2.0,
            unit = BazarUnit.PACKET,
            paymentType = PaymentType.CASH,
            note = "Soap & detergent",
        ),
        Bazar(
            id = 6,
            epochDay = todayEpoch - 5,
            amount = 500.0,
            category = BazarCategory.FISH_MEAT,
            quantity = 2.0,
            unit = BazarUnit.KG,
            paymentType = PaymentType.CASH,
            paidByMemberId = 1L,
            paidByMemberName = "Rahim Uddin",
            note = "Chicken",
        ),
    )

    val depositItems = listOf(
        Deposit(id = 1, memberId = 1, memberName = "Rahim Uddin", epochDay = todayEpoch, amount = 3000.0, method = PaymentMethod.CASH),
        Deposit(id = 2, memberId = 2, memberName = "Karim Ahmed", epochDay = todayEpoch - 1, amount = 2500.0, method = PaymentMethod.BKASH),
        Deposit(id = 3, memberId = 3, memberName = "Tanvir Hasan", epochDay = todayEpoch - 2, amount = 2000.0, method = PaymentMethod.NAGAD),
        Deposit(id = 4, memberId = 4, memberName = "Sakib Al Hasan", epochDay = todayEpoch - 3, amount = 1500.0, method = PaymentMethod.BANK),
        Deposit(id = 5, memberId = 5, memberName = "Mahmudullah Riyad", epochDay = todayEpoch - 4, amount = 100.0, method = PaymentMethod.CASH),
        Deposit(id = 6, memberId = 1, memberName = "Rahim Uddin", epochDay = todayEpoch - 5, amount = 500.0, method = PaymentMethod.BKASH),
    )

    val memberSummaries = listOf(
        MemberSummary(member = members[0], meals = 10, mealCost = 900.0, deposit = 3500.0, paidByTotal = 1150.0, sharedExpenseShare = 300.0), // balance: 3500 + 1150 - 900 - 300 = +3450
        MemberSummary(member = members[1], meals = 8, mealCost = 720.0, deposit = 2500.0, paidByTotal = 400.0, sharedExpenseShare = 300.0),  // balance: 2500 + 400 - 720 - 300 = +1880
        MemberSummary(member = members[2], meals = 9, mealCost = 810.0, deposit = 2000.0, sharedExpenseShare = 300.0),  // balance: +890
        MemberSummary(member = members[3], meals = 6, mealCost = 540.0, deposit = 1500.0, sharedExpenseShare = 300.0),  // balance: +660
        MemberSummary(member = members[4], meals = 2, mealCost = 180.0, deposit = 100.0, sharedExpenseShare = 300.0),   // balance: -380
    )

    val messSummary = MessSummary(
        month = YearMonth.now(),
        totalBazar = 3150.0,
        totalSharedExpenses = 1500.0,
        totalMeals = 35,
        mealRate = 90.0,
        totalOutstandingBaki = 250.0,
        members = memberSummaries,
    )

    // Home UiStates
    val homeNormal = HomeUiState(isLoading = false, summary = messSummary, todayMeals = 7)
    val homeLoading = HomeUiState(isLoading = true)

    // Meal UiStates
    val mealNormal = MealUiState(isLoading = false, date = LocalDate.now(), items = memberMealUis)
    val mealEmpty = MealUiState(isLoading = false, date = LocalDate.now(), items = emptyList())
    val mealLoading = MealUiState(isLoading = true)

    // Bazar UiStates
    val bazarNormal = BazarUiState(isLoading = false, items = bazarItems, members = members, form = null)
    val bazarEmpty = BazarUiState(isLoading = false, items = emptyList(), members = members, form = null)
    val bazarLoading = BazarUiState(isLoading = true)
    val bazarForm = BazarUiState(
        isLoading = false,
        items = bazarItems,
        members = members,
        form = BazarFormState(
            amount = "500",
            quantity = "2.5",
            category = BazarCategory.VEGETABLE,
            unit = BazarUnit.KG,
            note = "Fresh vegetables",
        ),
    )
    val bazarError = BazarUiState(
        isLoading = false,
        items = bazarItems,
        members = members,
        form = BazarFormState(
            amount = "-10",
            amountError = UiText.Resource(R.string.error_invalid_amount),
        ),
    )

    // Deposit UiStates
    val depositNormal = DepositUiState(isLoading = false, items = depositItems, members = members, form = null)
    val depositEmpty = DepositUiState(isLoading = false, items = emptyList(), members = members, form = null)
    val depositLoading = DepositUiState(isLoading = true)
    val depositForm = DepositUiState(isLoading = false, items = depositItems, members = members, form = DepositFormState(member = members[0], amount = "1000", method = PaymentMethod.BKASH))
    val depositError = DepositUiState(isLoading = false, items = depositItems, members = members, form = DepositFormState(amountError = UiText.Resource(R.string.error_invalid_amount)))

    // Members UiStates
    val membersNormal = MembersUiState(isLoading = false, members = members, form = null, memberToDelete = null)
    val membersEmpty = MembersUiState(isLoading = false, members = emptyList(), form = null, memberToDelete = null)
    val membersLoading = MembersUiState(isLoading = true)
    val membersForm = MembersUiState(isLoading = false, members = members, form = MemberFormState(name = "Mushfiqur Rahim", phone = "01700000000"), memberToDelete = null)
    val membersError = MembersUiState(isLoading = false, members = members, form = MemberFormState(nameError = UiText.Resource(R.string.error_name_required)), memberToDelete = null)
    val membersDelete = MembersUiState(isLoading = false, members = members, form = null, memberToDelete = members[0])

    // Report UiStates
    val reportNormal = ReportUiState(isLoading = false, summary = messSummary)
    val reportEmpty = ReportUiState(isLoading = false, summary = MessSummary(members = emptyList()))
    val reportLoading = ReportUiState(isLoading = true)

    // Shared Expense
    val sharedExpenses = listOf(
        SharedExpense(
            id = 1,
            title = "Gas Bill",
            type = SharedExpenseType.GAS,
            amount = 1500.0,
            splitType = SplitType.EQUAL,
            epochDay = todayEpoch,
            paidByMemberId = 1L,
            paidByMemberName = "Rahim Uddin",
        ),
        SharedExpense(
            id = 2,
            title = "WiFi Bill",
            type = SharedExpenseType.WIFI,
            amount = 800.0,
            splitType = SplitType.EQUAL,
            epochDay = todayEpoch - 1,
            paidByMemberId = null,
            paidByMemberName = null,
        ),
    )

    val sharedExpenseNormal = SharedExpenseUiState(isLoading = false, items = sharedExpenses, members = members, form = null)
    val sharedExpenseEmpty = SharedExpenseUiState(isLoading = false, items = emptyList(), members = members, form = null)
    val sharedExpenseLoading = SharedExpenseUiState(isLoading = true)
    val sharedExpenseForm = SharedExpenseUiState(
        isLoading = false,
        items = sharedExpenses,
        members = members,
        form = SharedExpenseFormState(
            title = "Internet Bill",
            type = SharedExpenseType.WIFI,
            amountText = "1200",
            splitType = SplitType.EQUAL,
        ),
    )
}
