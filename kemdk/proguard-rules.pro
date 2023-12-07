# General (make debugging easier etc)
-dontobfuscate
-optimizations code/simplification/arithmetic,code/simplification/cast,field/*,class/merging/*
-optimizationpasses 5
-keepattributes SourceFile,LineNumberTable
-dontwarn java.lang.invoke.StringConcatFactory

# To support R8 we have some classes we exclude from
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class dk.gls.kemdk.** {
    *** Companion;
}

-keep class dk.gls.kemdk.**$DefaultImpls {
    *;
}

-keep class dk.gls.kemdk.** {
     *;
}

-keep class dk.gls.kemdk.DeviceSerialUtil.** {
     *;
}

-keep class dk.gls.kemdk.EMDKResult.** {
     *;
}

-keep class dk.gls.kemdk.emdk.EMDKThrowable.** {
     *;
}