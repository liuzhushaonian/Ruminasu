package com.app.legend.ruminasu.utils.glide

import android.content.Context
import com.app.legend.ruminasu.beans.Picture
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class InputStreamAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)

        registry.prepend(Picture::class.java,InputStream::class.java,PicModelLoadFactory())

    }
}