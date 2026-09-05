# Keep Room's generated database implementation and schema metadata available to R8.
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class **.Database_Impl { *; }
-keep @androidx.room.Entity class * { *; }

# Keep Kotlinx Serialization generated serializers used by backup payloads.
-keepclassmembers class com.example.vehiclemanager.** {
    *** Companion;
}
-keepclassmembers class **$$serializer { *; }
-keep class kotlinx.serialization.** { *; }

# Hilt-generated entry points and ViewModels are discovered by generated code.
-keep class dagger.hilt.** { *; }
