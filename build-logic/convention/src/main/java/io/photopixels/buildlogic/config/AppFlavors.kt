package io.photopixels.buildlogic.config

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import io.photopixels.buildlogic.config.FlavorDimension.BRAND
import io.photopixels.buildlogic.config.FlavorDimension.ENVIRONMENT
import io.photopixels.buildlogic.extensions.toCamelCase

/** Flavor dimensions are defined here. Order DOES matter. Customize as required. */
enum class FlavorDimension {
    BRAND,
    ENVIRONMENT,
}

enum class AppFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    DEV(dimension = ENVIRONMENT, applicationIdSuffix = ".dev"),
    STAGING(dimension = ENVIRONMENT, applicationIdSuffix = ".staging"),
    PROD(dimension = ENVIRONMENT),

    CUSTOMER_BRAND_1(dimension = BRAND, applicationIdSuffix = ".brand1"),
    CUSTOMER_BRAND_2(dimension = BRAND, applicationIdSuffix = ".brand2"),
}

fun manageFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach {
            flavorDimensions += it.name
        }

        productFlavors {
            AppFlavor.values().forEach {
                create(it.name.toCamelCase()) {
                    dimension = it.dimension.name

                    flavorConfigurationBlock(this, it)

                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
