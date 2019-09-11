package com.app.legend.ruminasu.utils.glide

import com.app.legend.ruminasu.beans.Picture
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class ComicKey(picture: Picture):Key {


    var picture:Picture?=null

    init {

        this.picture=picture
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {



        val s=StringBuilder().append(picture!!.content).append("/").append(picture!!.comic_path).append("/").append(picture!!.chapter_name)

        val bs:Byte=s.toString().toByte()

        messageDigest.update(bs)


    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}