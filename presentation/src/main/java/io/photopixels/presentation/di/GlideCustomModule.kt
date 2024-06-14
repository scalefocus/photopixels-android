package io.photopixels.presentation.di

import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

/** Ensures that Glide's generated API is created for the Gallery sample.  */
@GlideModule
class GlideCustomModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setLogLevel(Log.VERBOSE)
        builder.setIsActiveResourceRetentionAllowed(true)
    }
}
