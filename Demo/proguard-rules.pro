-ignorewarnings
# for SecVerify
-keep class cn.fly.**{*;}
# for CTCC
-keep class cn.com.chinatelecom.account.**{*;}
# for CUCC
-dontwarn com.unicom.online.account.shield.**
-keep class com.unicom.online.account.shield.** {*;}
# for CMCC
-dontwarn com.cmic.gen.sdk.**
-keep class com.cmic.gen.sdk.**{*;}
# for CUCCXW
-keep class com.unicom.xiaowo.account.shield.**{*;}