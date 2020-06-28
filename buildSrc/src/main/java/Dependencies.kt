/**
 * @author lanxiaobin
 * @date 2020/6/28
 */

object Version {
    const val compileSdkVersion = 29
    const val minSdkVersion = 19
    const val targetSdkVersion = 29

    const val versionCode = 1
    const val versionName = "v1.0.0"

    const val gradleVersion = "3.5.0"

    const val androidSupportSdkVersion = "28.0.0"
    const val retrofit = "2.3.0"
    const val rxjava = "2.1.9"

    const val constraintlayout = "1.1.3"

    const val kotlin_version = "1.3.41"

    const val core_ktx = "1.2.0"
    const val kotlinx_coroutines_android = "1.3.0"
    const val lifecycle_viewmodel_ktx = "2.2.0-alpha04"
    const val lifecycle_extensions = "2.2.0-alpha04"
    const val recyclerview = "1.1.0-beta04"
    const val BaseRecyclerViewAdapterHelper = "2.9.50"
    const val smart_refresh_layout = "2.0.0"

    const val doraemonkit = "3.1.5"

    const val rxhttp = "2.2.0"
}

object Deps {
    val support_v4 = "androidx.legacy:legacy-support-v4:${Version.androidSupportSdkVersion}"
    val support_annotations = "com.android.support:support-annotations:${Version.androidSupportSdkVersion}"
    val appcompat = "androidx.appcompat:appcompat:1.0.0"
    val constraintlayout = "androidx.constraintlayout:constraintlayout:${Version.constraintlayout}"
    val core_ktx = "androidx.core:core-ktx:${Version.core_ktx}"
    val kotlin_stdlib_jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlin_version}"
    val kotlinx_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.kotlinx_coroutines_android}"
    val lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle_viewmodel_ktx}"
    val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:${Version.lifecycle_extensions}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Version.recyclerview}"
    val BaseRecyclerViewAdapterHelper = "com.github.CymChad:BaseRecyclerViewAdapterHelper:${Version.BaseRecyclerViewAdapterHelper}"
    val retrofit2 = "com.squareup.retrofit2:retrofit:2.6.0"
    val retrofit2_converter_gson = "com.squareup.retrofit2:converter-gson:2.6.0"
    val okhttp3_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:3.12.1"
    val autosize = "me.jessyan:autosize:1.2.1"
    val eventbus = "org.greenrobot:eventbus:3.2.0"
    val cardview = "androidx.cardview:cardview:1.0.0"
    val glide = "com.github.bumptech.glide:glide:4.11.0"

    val smart_refresh_layout_kernel = "com.scwang.smart:refresh-layout-kernel:${Version.smart_refresh_layout}"
    val smart_refresh_layout_header_material = "com.scwang.smart:refresh-header-material:${Version.smart_refresh_layout}"
    val smart_refresh_layout_footer_classics = "com.scwang.smart:refresh-footer-classics:${Version.smart_refresh_layout}"

    val doraemonkit_debug = "com.didichuxing.doraemonkit:doraemonkit:${Version.doraemonkit}"
    val doraemonkit_release = "com.didichuxing.doraemonkit:doraemonkit-no-op:${Version.doraemonkit}"
    val doraemonkit_leakcanary = "com.didichuxing.doraemonkit:doraemonkit-leakcanary:${Version.doraemonkit}"

    val mmkv = "com.tencent:mmkv-static:1.1.1"

    val multidex = "com.android.support:multidex:1.0.3"

    val flexbox = "com.google.android:flexbox:2.0.1"

    val lottie = "com.airbnb.android:lottie:3.4.0"

    val rxhttp = "com.ljx.rxhttp:rxhttp:${Version.rxhttp}"
    val rxhttp_compiler = "com.ljx.rxhttp:rxhttp-compiler:${Version.rxhttp}"

    val xcrash = "com.iqiyi.xcrash:xcrash-android-lib:2.4.9"

    //BlurLayout 高斯模糊
    val blurkit = "io.alterac.blurkit:blurkit:1.1.1"

    //Bugly
    val crashreport = "com.tencent.bugly:crashreport:3.2.3"
    val nativecrashreport = "com.tencent.bugly:nativecrashreport:3.7.3"
}

