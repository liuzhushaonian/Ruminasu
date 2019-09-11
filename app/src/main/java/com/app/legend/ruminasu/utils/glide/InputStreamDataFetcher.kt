package com.app.legend.ruminasu.utils.glide

import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.utils.ComicUtils
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.InputStream

class InputStreamDataFetcher(picture: Picture): DataFetcher<InputStream> {


    var picture:Picture?=null

    init {
        this.picture=picture
    }


    var inputStream:InputStream?=null


    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun cleanup() {
        if (inputStream!=null){
            inputStream!!.close()
        }
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

    override fun cancel() {

    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {

        inputStream=loadInputStream()

        callback.onDataReady(inputStream)

    }

    fun loadInputStream():InputStream?{

        var i:InputStream?=null

        if (picture!=null){

            i=ComicUtils.getComicInputStream(picture!!)
        }

        return i
    }
}