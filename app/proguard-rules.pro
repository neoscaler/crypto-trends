#-dontwarn com.facebook.**
#-dontwarn org.checkerframework.**
#-dontwarn afu.org.checkerframework.**
#-dontwarn kotlin.**
#-dontwarn sun.**
#-dontwarn java.**
#-dontwarn COM.**
#-dontwarn jrockit.**
#-dontwarn org.modelmapper.**
#-dontwarn dalvik.**
#-dontwarn org.apache.**
#-dontwarn javax.**
#
#
#-keep class com.neoscaler.cryptotrends.model.** { *; }
#
##
## EventBus
##
#-keepattributes *Annotation*
#-keepclassmembers class ** {
#    @org.greenrobot.eventbus.Subscribe <methods>;
#}
#-keep enum org.greenrobot.eventbus.ThreadMode { *; }
## Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}
#
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
#-keep class com.marshalchen.** { *; }
#-keep interface com.marshalchen.** { *; }
#-keep class org.modelmapper.** { *; }
#-keep interface org.modelmapper.** { *; }
#-keep class com.squareup.retrofit2.** { *; }
#-keep interface com.squareup.retrofit2.** { *; }
