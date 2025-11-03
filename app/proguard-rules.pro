# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

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
-keepattributes LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Keep Room database classes
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }

# Keep Glide classes
-keep class com.bumptech.glide.** { *; }
-keep public class * extends com.bumptech.glide.module.AppGlideModule { void <init>(); }
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.qaassist.inspection.models.** { *; }

# Keep iText PDF classes
-keep class com.itextpdf.** { *; }
-keep class org.bouncycastle.** { *; }
-dontwarn com.itextpdf.**
-dontwarn org.bouncycastle.**
-dontwarn org.bouncycastle.asn1.**
-dontwarn org.bouncycastle.jce.**
-dontwarn org.bouncycastle.jce.provider.**
-dontwarn org.bouncycastle.pqc.**
-dontwarn org.bouncycastle.pqc.jcajce.provider.**
-dontwarn org.bouncycastle.x509.**

# Keep Google Play Services Location
-keep class com.google.android.gms.location.** { *; }
-keep class com.google.android.gms.internal.location.** { *; }

# Keep Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.internal.MainDispatcherFactory {
    void <init>();
}

# Keep ViewBinding classes
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(...);
}

# Keep Parcelize
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Suppress warnings for common libraries
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.android.gms.internal.vision.**
-dontwarn com.google.android.gms.internal.firebase_ml.**
-dontwarn com.google.firebase.ml.vision.barcode.internal.**
-dontwarn com.google.firebase.ml.vision.common.**
-dontwarn com.google.firebase.ml.vision.face.**
-dontwarn com.google.firebase.ml.vision.text.**
-dontwarn com.google.firebase.ml.vision.automl.**
-dontwarn com.google.firebase.ml.vision.label.**
-dontwarn com.google.firebase.ml.vision.object.**
-dontwarn com.google.firebase.ml.vision.internal.**
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn kotlin.jvm.internal.**

# Ignore all R8 warnings
-ignorewarnings
