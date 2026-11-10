package com.messmanager.app.di

import android.content.Context
import androidx.room.Room
import com.messmanager.app.data.local.MIGRATION_1_2
import com.messmanager.app.data.local.MIGRATION_2_3
import com.messmanager.app.data.local.MIGRATION_3_4
import com.messmanager.app.data.local.MessDatabase
import com.messmanager.app.data.local.dao.BazarDao
import com.messmanager.app.data.local.dao.ClosedMonthDao
import com.messmanager.app.data.local.dao.DepositDao
import com.messmanager.app.data.local.dao.MealDao
import com.messmanager.app.data.local.dao.MemberDao
import com.messmanager.app.data.local.dao.SharedExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MessDatabase =
        Room.databaseBuilder(context, MessDatabase::class.java, MessDatabase.NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()

    @Provides fun provideMemberDao(db: MessDatabase): MemberDao = db.memberDao()
    @Provides fun provideMealDao(db: MessDatabase): MealDao = db.mealDao()
    @Provides fun provideBazarDao(db: MessDatabase): BazarDao = db.bazarDao()
    @Provides fun provideDepositDao(db: MessDatabase): DepositDao = db.depositDao()
    @Provides fun provideClosedMonthDao(db: MessDatabase): ClosedMonthDao = db.closedMonthDao()
    @Provides fun provideSharedExpenseDao(db: MessDatabase): SharedExpenseDao = db.sharedExpenseDao()
}
