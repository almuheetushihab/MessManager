package com.messmanager.app.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.messmanager.app.data.local.MessDatabase
import com.messmanager.app.data.local.entity.BazarEntity
import com.messmanager.app.data.local.entity.ClosedMonthEntity
import com.messmanager.app.data.local.entity.DepositEntity
import com.messmanager.app.data.local.entity.MealEntity
import com.messmanager.app.data.local.entity.MemberEntity
import com.messmanager.app.data.local.entity.SharedExpenseEntity
import com.messmanager.app.data.local.entity.SharedExpenseShareEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseBackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: MessDatabase,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 4
    }

    suspend fun exportDatabaseToJsonUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val members = database.memberDao().getAll()
            val meals = database.mealDao().getAll()
            val bazars = database.bazarDao().getAll()
            val deposits = database.depositDao().getAll()
            val sharedExpenses = database.sharedExpenseDao().getAllExpenses()
            val sharedExpenseShares = database.sharedExpenseDao().getAllShares()
            val closedMonths = database.closedMonthDao().getAll()

            val root = JSONObject().apply {
                put("schemaVersion", CURRENT_SCHEMA_VERSION)
                put("exportedAtEpochMs", System.currentTimeMillis())
                put("appVersion", "1.0.0")

                val membersArray = JSONArray()
                members.forEach { m ->
                    membersArray.put(
                        JSONObject().apply {
                            put("id", m.id)
                            put("name", m.name)
                            put("phone", m.phone)
                        },
                    )
                }
                put("members", membersArray)

                val mealsArray = JSONArray()
                meals.forEach { meal ->
                    mealsArray.put(
                        JSONObject().apply {
                            put("id", meal.id)
                            put("memberId", meal.memberId)
                            put("epochDay", meal.epochDay)
                            put("breakfast", meal.breakfast)
                            put("lunch", meal.lunch)
                            put("dinner", meal.dinner)
                            put("guests", meal.guests)
                        },
                    )
                }
                put("meals", mealsArray)

                val bazarsArray = JSONArray()
                bazars.forEach { b ->
                    bazarsArray.put(
                        JSONObject().apply {
                            put("id", b.id)
                            put("epochDay", b.epochDay)
                            put("amount", b.amount)
                            put("category", b.category)
                            put("note", b.note)
                            if (b.quantity != null) put("quantity", b.quantity)
                            if (b.unit != null) put("unit", b.unit)
                            put("paymentType", b.paymentType)
                            put("creditShopName", b.creditShopName)
                            put("isCreditSettled", b.isCreditSettled)
                            if (b.paidByMemberId != null) put("paidByMemberId", b.paidByMemberId)
                            if (b.photoUri != null) put("photoUri", b.photoUri)
                        },
                    )
                }
                put("bazars", bazarsArray)

                val depositsArray = JSONArray()
                deposits.forEach { d ->
                    depositsArray.put(
                        JSONObject().apply {
                            put("id", d.id)
                            put("memberId", d.memberId)
                            put("epochDay", d.epochDay)
                            put("amount", d.amount)
                            put("method", d.method)
                        },
                    )
                }
                put("deposits", depositsArray)

                val sharedExpensesArray = JSONArray()
                sharedExpenses.forEach { se ->
                    sharedExpensesArray.put(
                        JSONObject().apply {
                            put("id", se.id)
                            put("epochDay", se.epochDay)
                            put("title", se.title)
                            put("type", se.type)
                            put("amount", se.amount)
                            put("splitType", se.splitType)
                            if (se.paidByMemberId != null) put("paidByMemberId", se.paidByMemberId)
                        },
                    )
                }
                put("sharedExpenses", sharedExpensesArray)

                val sharedSharesArray = JSONArray()
                sharedExpenseShares.forEach { ses ->
                    sharedSharesArray.put(
                        JSONObject().apply {
                            put("expenseId", ses.expenseId)
                            put("memberId", ses.memberId)
                            put("shareAmount", ses.shareAmount)
                        },
                    )
                }
                put("sharedExpenseShares", sharedSharesArray)

                val closedMonthsArray = JSONArray()
                closedMonths.forEach { cm ->
                    closedMonthsArray.put(
                        JSONObject().apply {
                            put("month", cm.month)
                            put("closedAtEpochDay", cm.closedAtEpochDay)
                        },
                    )
                }
                put("closedMonths", closedMonthsArray)
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(root.toString(2).toByteArray(Charsets.UTF_8))
            } ?: throw IllegalStateException("Could not open output stream for export")
        }
    }

    suspend fun importDatabaseFromJsonUri(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader(Charsets.UTF_8).readText()
            } ?: throw IllegalStateException("Could not open input stream for import")

            val root = JSONObject(jsonString)
            val schemaVersion = root.optInt("schemaVersion", -1)
            if (schemaVersion <= 0 || schemaVersion > CURRENT_SCHEMA_VERSION) {
                throw IllegalArgumentException("Incompatible schema version: $schemaVersion (current max is $CURRENT_SCHEMA_VERSION)")
            }

            val membersList = mutableListOf<MemberEntity>()
            val membersArray = root.optJSONArray("members") ?: JSONArray()
            for (i in 0 until membersArray.length()) {
                val obj = membersArray.getJSONObject(i)
                membersList.add(
                    MemberEntity(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        phone = obj.optString("phone", ""),
                    ),
                )
            }

            val mealsList = mutableListOf<MealEntity>()
            val mealsArray = root.optJSONArray("meals") ?: JSONArray()
            for (i in 0 until mealsArray.length()) {
                val obj = mealsArray.getJSONObject(i)
                mealsList.add(
                    MealEntity(
                        id = obj.getLong("id"),
                        memberId = obj.getLong("memberId"),
                        epochDay = obj.getLong("epochDay"),
                        breakfast = obj.getBoolean("breakfast"),
                        lunch = obj.getBoolean("lunch"),
                        dinner = obj.getBoolean("dinner"),
                        guests = obj.optInt("guests", 0),
                    ),
                )
            }

            val bazarsList = mutableListOf<BazarEntity>()
            val bazarsArray = root.optJSONArray("bazars") ?: JSONArray()
            for (i in 0 until bazarsArray.length()) {
                val obj = bazarsArray.getJSONObject(i)
                bazarsList.add(
                    BazarEntity(
                        id = obj.getLong("id"),
                        epochDay = obj.getLong("epochDay"),
                        amount = obj.getDouble("amount"),
                        category = obj.getString("category"),
                        note = obj.optString("note", ""),
                        quantity = if (obj.has("quantity") && !obj.isNull("quantity")) obj.getDouble("quantity") else null,
                        unit = if (obj.has("unit") && !obj.isNull("unit")) obj.getString("unit") else null,
                        paymentType = obj.optString("paymentType", "CASH"),
                        creditShopName = obj.optString("creditShopName", ""),
                        isCreditSettled = obj.optBoolean("isCreditSettled", false),
                        paidByMemberId = if (obj.has("paidByMemberId") && !obj.isNull("paidByMemberId")) obj.getLong("paidByMemberId") else null,
                        photoUri = if (obj.has("photoUri") && !obj.isNull("photoUri")) obj.getString("photoUri") else null,
                    ),
                )
            }

            val depositsList = mutableListOf<DepositEntity>()
            val depositsArray = root.optJSONArray("deposits") ?: JSONArray()
            for (i in 0 until depositsArray.length()) {
                val obj = depositsArray.getJSONObject(i)
                depositsList.add(
                    DepositEntity(
                        id = obj.getLong("id"),
                        memberId = obj.getLong("memberId"),
                        epochDay = obj.getLong("epochDay"),
                        amount = obj.getDouble("amount"),
                        method = obj.optString("method", "CASH"),
                    ),
                )
            }

            val sharedExpensesList = mutableListOf<SharedExpenseEntity>()
            val sharedExpensesArray = root.optJSONArray("sharedExpenses") ?: JSONArray()
            for (i in 0 until sharedExpensesArray.length()) {
                val obj = sharedExpensesArray.getJSONObject(i)
                sharedExpensesList.add(
                    SharedExpenseEntity(
                        id = obj.getLong("id"),
                        epochDay = obj.getLong("epochDay"),
                        title = obj.getString("title"),
                        type = obj.getString("type"),
                        amount = obj.getDouble("amount"),
                        splitType = obj.getString("splitType"),
                        paidByMemberId = if (obj.has("paidByMemberId") && !obj.isNull("paidByMemberId")) obj.getLong("paidByMemberId") else null,
                    ),
                )
            }

            val sharedSharesList = mutableListOf<SharedExpenseShareEntity>()
            val sharedSharesArray = root.optJSONArray("sharedExpenseShares") ?: JSONArray()
            for (i in 0 until sharedSharesArray.length()) {
                val obj = sharedSharesArray.getJSONObject(i)
                sharedSharesList.add(
                    SharedExpenseShareEntity(
                        expenseId = obj.getLong("expenseId"),
                        memberId = obj.getLong("memberId"),
                        shareAmount = obj.getDouble("shareAmount"),
                    ),
                )
            }

            val closedMonthsList = mutableListOf<ClosedMonthEntity>()
            val closedMonthsArray = root.optJSONArray("closedMonths") ?: JSONArray()
            for (i in 0 until closedMonthsArray.length()) {
                val obj = closedMonthsArray.getJSONObject(i)
                closedMonthsList.add(
                    ClosedMonthEntity(
                        month = obj.getString("month"),
                        closedAtEpochDay = obj.getLong("closedAtEpochDay"),
                    ),
                )
            }

            database.withTransaction {
                database.sharedExpenseDao().deleteAllShares()
                database.sharedExpenseDao().deleteAllExpenses()
                database.bazarDao().deleteAll()
                database.depositDao().deleteAll()
                database.mealDao().deleteAll()
                database.closedMonthDao().deleteAll()
                database.memberDao().deleteAll()

                database.memberDao().insertAll(membersList)
                database.mealDao().insertAll(mealsList)
                database.bazarDao().insertAll(bazarsList)
                database.depositDao().insertAll(depositsList)
                database.sharedExpenseDao().insertAllExpenses(sharedExpensesList)
                database.sharedExpenseDao().insertShares(sharedSharesList)
                database.closedMonthDao().insertAll(closedMonthsList)
            }
        }
    }
}
