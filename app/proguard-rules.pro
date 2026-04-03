# ========================================
#  PROGUARD / R8 rules for Flinger-Bit
#  Licensed under MIT License
# ========================================

# Mantener los atributos de línea y archivo para facilitar debugging si ocurre crash
-keepattributes SourceFile,LineNumberTable

# Mantener las clases principales de Android
-keep class android.app.Activity { *; }
-keep class android.app.Application { *; }
-keep class android.content.Context { *; }
-keep class android.view.** { *; }
-keep class android.webkit.WebView { *; }

# Mantener cualquier clase anotada con @Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# WebView interfaces / callback usados
-keepclassmembers class * {
    public void onPageFinished(android.webkit.WebView, java.lang.String);
    public void onReceivedError(android.webkit.WebView, android.webkit.WebResourceRequest, android.webkit.WebResourceError);
}

# Mantener clases de binding si usas viewBinding
-keep class * extends androidx.viewbinding.ViewBinding { *; }
-keep class com.flingerbit.databinding.** { *; }

# Evitar warnings innecesarios
-dontwarn android.support.**
-dontwarn androidx.**
-dontwarn okhttp3.**
-dontwarn okio.**

# No eliminar métodos que podrían ser llamados por reflexión
-keepclassmembers class * {
    public <init>(...);
    public void *(...);
}

# ================================
# Opcional: si agregas librerías adicionales
# podrías necesitar reglas para ellas,
# cambia según lo que uses en tu app
# ================================

# Mantener clases de soporte y utilidades
-keep class com.google.android.material.** { *; }
