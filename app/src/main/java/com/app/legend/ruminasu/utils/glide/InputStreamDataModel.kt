package com.app.legend.ruminasu.utils.glide


import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.utils.ChaptersNameUtils
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

class InputStreamDataModel:ModelLoader<Picture,InputStream>{

    override fun buildLoadData(
        model: Picture,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

        return ModelLoader.LoadData<InputStream>(ObjectKey(model),
            InputStreamDataFetcher(model)
        )

    }

    override fun handles(model: Picture): Boolean {
        return ChaptersNameUtils.isImage(model.content)
    }
}