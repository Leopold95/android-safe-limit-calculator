# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Koin
-keepnames class org.koin.** { *; }
-keepnames class * extends org.koin.core.module.Module
-keepclassmembers class * {
    public <init>(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.alexandr.safelimitcalculator.**$$serializer { *; }
-keepclassmembers class com.alexandr.safelimitcalculator.** {
    *** Companion;
}
-keepclasseswithmembers class com.alexandr.safelimitcalculator.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Keep data models
-keep class com.alexandr.safelimitcalculator.data.model.** { *; }

# R8 Missing Classes - Suppress warnings
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# Additional R8 rules for common libraries
-dontwarn sun.misc.Unsafe
-dontwarn java.nio.channels.FileChannel
-dontwarn javax.naming.**
-dontwarn com.sun.activation.**

# Keep Compose UI state
-keep class androidx.compose.runtime.** { *; }



