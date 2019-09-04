package com.app.legend.ruminasu.utils

/**
 * 解析章节名，获取分类
 */
class ChaptersNameUtils {

    companion object{

        public fun isImage(string: String):Boolean{

            return string.endsWith(".jpg")||string.endsWith(".png")

        }

    }

}