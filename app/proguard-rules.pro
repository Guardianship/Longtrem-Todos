# ProGuard rules
-keepclassmembers class * extends androidx.work.Worker {
    public <init>(...);
}
-keepclassmembers class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver {
    public <init>(...);
}
