# Flinger-Bit keep rules
-keepattributes SourceFile,LineNumberTable

# Keep app classes if minify gets enabled later.
-keep class com.flingerbit.** { *; }

# Keep view binding classes.
-keep class **Binding { *; }

# WebView / Android internals
-dontwarn android.webkit.**
