# Room rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt & Dagger rules
-keep class dagger.hilt.** { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

# Kotlin Coroutines & Flow
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Database Entities & Models
-keep class com.messmanager.app.data.local.entity.** { *; }
-keep class com.messmanager.app.domain.model.** { *; }
-keep class com.messmanager.app.data.repository.UserSettings { *; }

# DataStore
-keep class androidx.datastore.** { *; }
