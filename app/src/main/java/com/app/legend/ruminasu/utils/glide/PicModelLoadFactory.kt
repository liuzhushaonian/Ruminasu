package com.app.legend.ruminasu.utils.glide

import com.app.legend.ruminasu.beans.Picture
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import java.io.InputStream

class PicModelLoadFactory:ModelLoaderFactory<Picture,InputStream> {




    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Picture, InputStream> {
        return InputStreamDataModel()
    }

    override fun teardown() {

    }
}