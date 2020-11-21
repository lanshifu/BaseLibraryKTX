package com.lanshifu.plugin.transfrom

import com.android.build.gradle.api.BaseVariant

interface VariantProcessor {

    fun process(variant: BaseVariant)

}
