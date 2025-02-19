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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

### Kotlin Coroutine
# https://github.com/Kotlin/kotlinx.coroutines/blob/master/README.md
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

### Kotlin
#https://stackoverflow.com/questions/33547643/how-to-use-kotlin-with-proguard
#https://medium.com/@AthorNZ/kotlin-metadata-jackson-and-proguard-f64f51e5ed32
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

###AndroidX
-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.* { *; }

-keepattributes Signaturec
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn okio.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
  public static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
  public static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
  public static void checkNotNull(java.lang.Object);
  public static void checkNotNull(java.lang.Object, java.lang.String);
  public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
  public static void checkNotNullParameter(java.lang.Object, java.lang.String);
  public static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
  public static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
  public static void throwUninitializedPropertyAccessException(java.lang.String);
}

-keepattributes *Annotation*, Signature, Exception

### remove logging
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-keepnames class * extends android.os.Parcelable
-keepnames class * extends kotlinx.parcelize.Parcelize
-keepnames class * extends java.io.Serializable
-keep enum com.hooyu.android.sdk.model.document.DocumentType
-keepnames class com.hooyu.android.sdk.model.document.DocumentType { *; }
-keep public class com.hooyu.android.sdk.model.*, com.hooyu.android.wai.model.*, com.hooyu.android.wai.model.*.*, com.hooyu.android.sdk.model.*.* { public protected private *; }
-keep public class com.hooyu.android.sdk.liveness.*, com.hooyu.android.sdk.service.*, com.hooyu.android.sdk.nfc.*, com.hooyu.android.sdk.ocr.* { public protected *; }

-keepclasseswithmembers class com.miteksystems.misnap.** {*;}
-keepclasseswithmembers class com.lelestacia.network.model** { *; }

-keepclassmembers,allowobfuscation class * {
  @kotlinx.parcelize.Parcelize <fields>;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn com.android.org.conscrypt.SSLParametersImpl
-dontwarn com.google.mlkit.common.sdkinternal.LibraryVersion
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$ClassificationRoutine
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result$Failure$ImageFormat
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result$Failure$LibraryLoad
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result$Failure$License
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result$Failure
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result$Processed
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier$Result
-dontwarn com.miteksystems.misnap.classifier.MiSnapDocumentClassifier
-dontwarn com.miteksystems.misnap.classifier.util.ClassificationUtil
-dontwarn org.apache.harmony.xnet.provider.jsse.SSLParametersImpl

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response