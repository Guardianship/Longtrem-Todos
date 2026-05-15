# ProGuard rules for Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManagerHolder { *; }
-keepclassmembers @dagger.hilt.android.lifecycle.HiltViewModel class * {
    @javax.inject.Inject <fields>;
    @javax.inject.Inject <init>(...);
}
-keep class * extends android.app.Application { *; }
-keep class * extends android.app.Service { *; }
-keep class * extends android.content.BroadcastReceiver { *; }

# WorkManager
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(...);
}
-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(...);
}

# Glance Widget
-keepclassmembers class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver {
    public <init>(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# Keep data classes for serialization
-keepclassmembers class com.junelin.longtermtodos.data.local.entity.* {
    <init>(...);
    *;
}
-keepclassmembers class com.junelin.longtermtodos.export.* {
    <init>(...);
    *;
}
