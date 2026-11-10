package com.messmanager.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.messmanager.app.data.local.dao.BazarDao
import com.messmanager.app.data.local.dao.ClosedMonthDao
import com.messmanager.app.data.local.dao.DepositDao
import com.messmanager.app.data.local.dao.MealDao
import com.messmanager.app.data.local.dao.MemberDao
import com.messmanager.app.data.local.dao.SharedExpenseDao
import com.messmanager.app.data.local.entity.BazarEntity
import com.messmanager.app.data.local.entity.ClosedMonthEntity
import com.messmanager.app.data.local.entity.DepositEntity
import com.messmanager.app.data.local.entity.MealEntity
import com.messmanager.app.data.local.entity.MemberEntity
import com.messmanager.app.data.local.entity.SharedExpenseEntity
import com.messmanager.app.data.local.entity.SharedExpenseShareEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `bazar_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `epochDay` INTEGER NOT NULL,
                `amount` REAL NOT NULL,
                `category` TEXT NOT NULL,
                `note` TEXT NOT NULL,
                `quantity` REAL,
                `unit` TEXT,
                `paymentType` TEXT NOT NULL DEFAULT 'CASH',
                `creditShopName` TEXT NOT NULL DEFAULT '',
                `isCreditSettled` INTEGER NOT NULL DEFAULT 0,
                `paidByMemberId` INTEGER,
                `photoUri` TEXT,
                FOREIGN KEY(`paidByMemberId`) REFERENCES `members`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO `bazar_new` (`id`, `epochDay`, `amount`, `category`, `note`, `paymentType`, `creditShopName`, `isCreditSettled`)
            SELECT `id`, `epochDay`, `amount`, `category`, `note`, 'CASH', '', 0 FROM `bazar`
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE `bazar`")
        db.execSQL("ALTER TABLE `bazar_new` RENAME TO `bazar`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_bazar_paidByMemberId` ON `bazar` (`paidByMemberId`)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `closed_months` (
                `month` TEXT NOT NULL,
                `closedAtEpochDay` INTEGER NOT NULL,
                PRIMARY KEY(`month`)
            )
            """.trimIndent(),
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `shared_expenses` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `epochDay` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `type` TEXT NOT NULL,
                `amount` REAL NOT NULL,
                `splitType` TEXT NOT NULL,
                `paidByMemberId` INTEGER,
                FOREIGN KEY(`paidByMemberId`) REFERENCES `members`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_shared_expenses_paidByMemberId` ON `shared_expenses` (`paidByMemberId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `shared_expense_shares` (
                `expenseId` INTEGER NOT NULL,
                `memberId` INTEGER NOT NULL,
                `shareAmount` REAL NOT NULL,
                PRIMARY KEY(`expenseId`, `memberId`),
                FOREIGN KEY(`expenseId`) REFERENCES `shared_expenses`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`memberId`) REFERENCES `members`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_shared_expense_shares_expenseId` ON `shared_expense_shares` (`expenseId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_shared_expense_shares_memberId` ON `shared_expense_shares` (`memberId`)")
    }
}

@Database(
    entities = [
        MemberEntity::class,
        MealEntity::class,
        BazarEntity::class,
        DepositEntity::class,
        ClosedMonthEntity::class,
        SharedExpenseEntity::class,
        SharedExpenseShareEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class MessDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun mealDao(): MealDao
    abstract fun bazarDao(): BazarDao
    abstract fun depositDao(): DepositDao
    abstract fun closedMonthDao(): ClosedMonthDao
    abstract fun sharedExpenseDao(): SharedExpenseDao

    companion object {
        const val NAME = "mess_manager.db"
    }
}
