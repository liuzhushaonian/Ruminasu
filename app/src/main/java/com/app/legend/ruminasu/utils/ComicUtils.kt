package com.app.legend.ruminasu.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.beans.Type
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.FileHeader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 漫画工具
 * 获取zip下的各种资源，文件夹、zip或是图片
 * 获取文件夹下的各种资源，文件夹、zip或是图片
 */
class ComicUtils {

    companion object {

        /**
         * 根据type生成chapter对象，并设置好id，orderName等属性，然后保存到数据库内
         * 内部可能是zip、文件夹或是直接是图片
         * wdmnd 难写的一批
         */
        public fun getChapterByType(comic: Comic, type: Type): MutableList<Chapter> {

            val chapters: MutableList<Chapter> = ArrayList()

            if (comic.zip > 0) {//是zip文件

                val z = ZipFile(comic.path)
                val header = z.fileHeaders


                /**
                 * 获取内部资源，判断是否是文件夹，或者图片，或者zip
                 */
                if (header.size > 0) {

                    //在这里挑选出文件夹来吧
                    for (f in header) {

                        val n = f.fileName

                        if (f.isDirectory && n.contains(type.content)) {//是文件夹

                            val c = Chapter(f.fileName, -1, type.id, -1, "", 0, "", comic.id)
                            chapters.add(c)

                        } else if (n.endsWith(".zip") && n.contains(type.content)) {//是zip文件，生成章节并加入集合

                            val c = Chapter(f.fileName, -1, type.id, -1, "", 0, "", comic.id)
                            chapters.add(c)

                        } else if (!n.contains("/") && ChaptersNameUtils.isImage(n)) {//文件名不包含"/"且是图片

                            val c = Chapter(comic.title, -1, type.id, -1, "", 0, "", comic.id)
                            chapters.add(c)
                            break

                        }

                    }


                }

            } else {//是文件夹

                val file = File(comic.path)

                if (file.isDirectory) {

                    val list: Array<File> = file.listFiles()!!

                    if (list.isNotEmpty()) {

                        val p = list[0]
                        if (ChaptersNameUtils.isImage(p.name)) {//判断第一个文件是不是图片，是就直接返回吧，这里面只是图片了

                            if (file.name.contains(type.content)) {

                                val c = Chapter(comic.title, 0, type.id, -1, "", 0, "", comic.id)

                                chapters.add(c)
                            }
                            return chapters //??? 这里为啥要返回？？
                        }

                        for (f in list) {

                            if (f.name.contains(type.content)) {

                                val c = Chapter(f.name, 0, type.id, -1, "", 0, "", comic.id)

                                chapters.add(c)
                            }
                        }
                    }
                }
            }

            return chapters
        }


        /**
         * 获取章节的封面
         *
         */
        fun getChapterBook(comic: Comic, chapter: Chapter): String? {

            var result = "";

            val cache = RuminasuApp.context.externalCacheDir!!.absolutePath + "/" + comic.title

            var name = chapter.name.replace(".zip", "")

            name = name.replace("/", "")//防止chapter是文件夹后面带有/后缀

            val bookPath =
                RuminasuApp.context.getExternalFilesDir(null)!!.absolutePath + "/comic_book/" + comic.title + "/" + name

            if (comic.zip > 0) {

                if (comic.path.isNotEmpty()) {

                    val zip = ZipFile(comic.path)
                    val h = zip.getFileHeader(chapter.name)

                    if (comic.title.equals(chapter.name) && h == null) {//表示这个章节就是这个漫画，名字相同，但内部没找到相同的header

                        val headers = zip.fileHeaders;

                        if (headers.isNotEmpty()) {

                            val q = headers.get(0)
                            if (ChaptersNameUtils.isImage(q.fileName)) {

                                val input = zip.getInputStream(q)

                                val bitmap = BitmapFactory.decodeStream(input);

                                saveBookInDisk(bitmap, bookPath)

                                result = bookPath

                            }

                        }

                    } else if (h != null && h.isDirectory) {//是文件夹

                        val header = zip.fileHeaders

                        val chapters: MutableList<FileHeader> = ArrayList()

                        for (h1 in header) {//遍历内部，找到相同的

                            if (h1.fileName.startsWith(chapter.name) && !h1.isDirectory) {

                                chapters.add(h1)

                            }
                        }

                        chapters.sortWith(object : Comparator<FileHeader> {

                            override fun compare(p0: FileHeader?, p1: FileHeader?): Int {

                                return order(p0!!.fileName) - order(p1!!.fileName)

                            }

                        })

                        if (chapters.isNotEmpty()) {

                            val f = chapters.get(0)

                            if (ChaptersNameUtils.isImage(f.fileName)) {

                                val input = zip.getInputStream(f)

                                val bitmap = BitmapFactory.decodeStream(input);

                                saveBookInDisk(bitmap, bookPath)

                                result = bookPath
                            }

                        }


                    } else if (h != null && h.fileName.endsWith(".zip")) {//是zip文件，解压到缓存，之后转移到路径下

                        zip.extractFile(h, cache)

                        val f = File(cache, h.fileName)

                        val z = ZipFile(f)

                        val hh = z.fileHeaders;

                        if (hh.isNotEmpty()) {

                            val i = hh.get(0)//获取第一张

                            if (ChaptersNameUtils.isImage(i.fileName)) {

                                val input = z.getInputStream(i)

                                val bitmap = BitmapFactory.decodeStream(input);

                                saveBookInDisk(bitmap, bookPath)

                                result = bookPath
                                f.delete()//删除缓存的文件

                            }
                        }
                    }

                }

            } else {//文件夹，内部是文件夹、zip或者直接图片

                if (comic.path.isNotBlank()) {

                    val c = File(comic.path)

                    if (c.isDirectory) {

                        val list = c.listFiles()

                        if (list != null && list.isNotEmpty()) {//非null且非空
                            val f = list.get(0)
                            if (ChaptersNameUtils.isImage(f.name)) {//第一张是图片

                                val bitmap = BitmapFactory.decodeFile(f.absolutePath)

                                saveBookInDisk(bitmap, bookPath)

                                result = bookPath

                                return result
                            }

                        }

                        //直接new file

                        val file = File(c, chapter.name)

                        if (file.exists()) {

                            if (file.isDirectory) {

                                val ll = file.listFiles()

                                if (ll != null && ll.isNotEmpty()) {

                                    val p = ll.get(0)
                                    if (ChaptersNameUtils.isImage(p.name)) {
                                        val bitmap = BitmapFactory.decodeFile(p.absolutePath)
                                        saveBookInDisk(bitmap, bookPath)
                                        result = bookPath
                                    }
                                }

                            } else if (file.name.endsWith(".zip")) {

                                val z = ZipFile(file)
                                val hs = z.fileHeaders
                                if (hs.isNotEmpty()) {

                                    val d = hs.get(0)

                                    if (ChaptersNameUtils.isImage(d.fileName)) {

                                        val input = z.getInputStream(d)

                                        val bitmap = BitmapFactory.decodeStream(input);

                                        saveBookInDisk(bitmap, bookPath)

                                        result = bookPath
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return result
        }

        private fun order(str: String): Int {

            var name = str

            if (!name.endsWith("/") && name.contains("/")) {

                name = name.substring(name.indexOf("/"), name.length)

            }

            name = name.replace("[^\\d]".toRegex(), "")

            if (name.isBlank()) {
                return 0
            } else {
                return name.toInt()
            }

        }

        //保存图片到本地
        private fun saveBookInDisk(bitmap: Bitmap, path: String) {

            val f = File(path)
            if (!f.parentFile!!.exists()) {

                f.parentFile!!.mkdirs()

            }

            val out = FileOutputStream(path)

            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)

        }

        /**
         * 获取漫画的封面
         */
        public fun getComicBook(comic: Comic): String {
            var result = ""
            val cache = RuminasuApp.context.externalCacheDir!!.absolutePath + "/" + comic.title
            val comicBookPath =
                RuminasuApp.context.getExternalFilesDir(null)!!.absolutePath + "/comic_book/" + comic.title + "/book"

            if (comic.zip > 0) {//是zip

                if (comic.path.isNotBlank()) {
                    val zip = ZipFile(comic.path)
                    val headers = zip.fileHeaders

                    val folders: MutableList<FileHeader> = ArrayList()
                    val zips: MutableList<FileHeader> = ArrayList()

                    for (h in headers) {

                        val n = h.fileName

                        if (h.isDirectory) {

                            folders.add(h)

                        } else if (n.endsWith(".zip")) {

                            zips.add(h)


                        } else if (!n.contains("/") && ChaptersNameUtils.isImage(n)) {//不包含/且是图片，表示这里面直接是图片，获取第一张就好

                            val inout = zip.getInputStream(h)

                            val bitmap = BitmapFactory.decodeStream(inout)

                            saveBookInDisk(bitmap, comicBookPath)
                            result = comicBookPath

                            return result

                        }
                    }

                    if (folders.isNotEmpty()) {

                        folders.sortWith(object : Comparator<FileHeader> {
                            override fun compare(p0: FileHeader?, p1: FileHeader?): Int {

                                return order(p0!!.fileName) - order(p1!!.fileName)

                            }
                        })

                        val folder = folders.get(0)//获取第一章
                        val images: MutableList<FileHeader> = ArrayList()

                        for (j in headers) {

                            val w = j.fileName

                            if (!j.isDirectory && w.startsWith(folder.fileName)) {

                                images.add(j)

                            }


                        }

                        if (images.isNotEmpty()) {

                            images.sortWith(object : Comparator<FileHeader> {
                                override fun compare(p0: FileHeader?, p1: FileHeader?): Int {

                                    return order(p0!!.fileName) - order(p1!!.fileName)

                                }

                            })


                            val p = images.get(0)

                            if (ChaptersNameUtils.isImage(p.fileName)) {

                                val inout = zip.getInputStream(p)

                                val bitmap = BitmapFactory.decodeStream(inout)

                                saveBookInDisk(bitmap, comicBookPath)

                                result = comicBookPath

                                return result
                            }
                        }

                    }


                    if (zips.isNotEmpty()) {

                        zips.sortWith(object : Comparator<FileHeader> {
                            override fun compare(p0: FileHeader?, p1: FileHeader?): Int {

                                return order(p0!!.fileName) - order(p1!!.fileName)
                            }

                        })

                        val z = zips.get(0)

                        zip.extractFile(z, cache)

                        val file = File(cache, z.fileName)

                        val files = file.listFiles()

                        if (files != null && files.isNotEmpty()) {

                            val i = files.get(0)
                            if (ChaptersNameUtils.isImage(i.name)) {

                                val bitmap = BitmapFactory.decodeFile(i.absolutePath)
                                saveBookInDisk(bitmap, comicBookPath)
                                result = comicBookPath
                                file.delete()
                            }
                        }
                    }
                }

            } else {//不是zip

                if (comic.path.isNotBlank()) {

                    val file = File(comic.path)
                    if (file.exists() && file.isDirectory) {

                        val list = file.listFiles()

                        if (list != null) {

                            list.sortWith(object : Comparator<File> {
                                override fun compare(p0: File?, p1: File?): Int {
                                    return order(p0!!.name) - order(p1!!.name)
                                }
                            })

                            val f = list.get(0)

                            if (ChaptersNameUtils.isImage(f.name)) {

                                val bitmap = BitmapFactory.decodeFile(f.absolutePath)
                                saveBookInDisk(bitmap, comicBookPath)
                                result = comicBookPath


                            } else if (f.isDirectory) {

                                val ss = f.listFiles()

                                if (ss != null && ss.isNotEmpty()) {

                                    ss.sortWith(object : Comparator<File> {
                                        override fun compare(p0: File?, p1: File?): Int {

                                            return order(p0!!.name) - order(p1!!.name)

                                        }

                                    })

                                    val s = ss.get(0)

                                    val bitmap = BitmapFactory.decodeFile(s.absolutePath)

                                    saveBookInDisk(bitmap, comicBookPath)

                                    result = comicBookPath

                                }

                            } else if (f.endsWith(".zip")) {

                                val z = ZipFile(f)

                                val list1 = z.fileHeaders

                                if (list1.isNotEmpty()) {

                                    val p = list1.get(0)
                                    if (ChaptersNameUtils.isImage(p.fileName)) {

                                        val input = z.getInputStream(p)
                                        val bitmap = BitmapFactory.decodeStream(input)

                                        saveBookInDisk(bitmap, comicBookPath)
                                        result = comicBookPath
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return result
        }


        /**
         * 获取输入流，如果是zip，直接可以获取
         * 如果是文件夹，则读取文件获取
         */
        fun getComicInputStream(picture: Picture): InputStream? {

            var inputStream: InputStream? = null

            if (picture.zip>0){

                val zipFile=ZipFile(picture.comic_path)

                val header=zipFile.getFileHeader(picture.chapter_name)

                if (header==null&&picture.chapter_name.endsWith(picture.comic_title)){//表示漫画本身就是章节

                    val h=zipFile.getFileHeader(picture.content)

                    if (h!=null&&ChaptersNameUtils.isImage(h.fileName)){

                        inputStream=zipFile.getInputStream(h)

                        return inputStream

                    }

                }

                if (header!=null){

                    if (header.fileName.endsWith(".zip")){

                        val p= getCachePath(picture.comic_title,picture.chapter_name)
                        val file=File(p,picture.chapter_name)//查找文件是否存在，存在就直接从文件内提取
                        if (file.exists()){

                            val z=ZipFile(file)

                            val pic=z.getFileHeader(picture.content)

                            inputStream=z.getInputStream(pic)

                        }else {

                            zipFile.extractFile(header, p)

                            val f=File(p,picture.chapter_name)

                            if (f.exists()){

                                val zz=ZipFile(f)

                                val h=zz.getFileHeader(picture.content)

                                inputStream=zz.getInputStream(h)

                            }
                        }


                    }else if (header.isDirectory){//是文件夹

                        val g=zipFile.getFileHeader(picture.content)
                        if (ChaptersNameUtils.isImage(g.fileName)){

                            inputStream=zipFile.getInputStream(g)
                        }
                    }
                }


            }else{

                val folder=File(picture.comic_path)
                if (folder.isDirectory){

                    val p=File(folder,picture.content)

                    if (p.exists()){//不在第一层内，继续找

                        inputStream=FileInputStream(p)

                    }

                    val pp=File(folder,picture.chapter_name)

                    if (pp.exists()){//存在，判断是zip还是文件夹

                        if (pp.name.endsWith(".zip")){

                            val zz=ZipFile(pp)
                            val h=zz.getFileHeader(picture.content)
                            if (ChaptersNameUtils.isImage(h.fileName)) {
                                inputStream = zz.getInputStream(h)
                            }


                        }else if (pp.isDirectory){

                            val ps=File(pp,picture.content)

                            if (ps.exists()){

                                if (ChaptersNameUtils.isImage(ps.name)){
                                    inputStream=FileInputStream(ps)
                                }
                            }
                        }
                    }
                }
            }

            return inputStream
        }


        /**
         * 根据漫画以及章节获取里面的图片并返回
         */
        fun getChapterPictures(comic: Comic, chapter: Chapter): MutableList<Picture> {


            val pics: MutableList<Picture> = ArrayList()

            if (comic.zip > 0) {

                val zipFile = ZipFile(comic.path)

                val h = zipFile.getFileHeader(chapter.name)

                if (h == null && chapter.name.equals(comic.title)) {//表示这章节就是这本漫画

                    val list = zipFile.fileHeaders

                    for (l in list) {

                        if (ChaptersNameUtils.isImage(l.fileName)) {


                            val p = Picture(0, l.fileName, 0, chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                            pics.add(p)
                        }

                    }

                    return pics

                }


                //有zip包或文件夹

                if (h != null && h.fileName.endsWith(".zip")) {

                    val p = getCachePath(comic.title, chapter.name)

                    zipFile.extractFile(h, p)

                    val f = File(p, chapter.name)

                    val zip = ZipFile(f)

                    val headers = zip.fileHeaders

                    for (j in headers) {

                        if (ChaptersNameUtils.isImage(j.fileName)) {

                            val o = Picture(0, j.fileName, 0, chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                            pics.add(o)
                        }
                    }

                    f.delete()//删除文件

                }else if (h!=null&&h.isDirectory){//是文件夹，注意，这里要排序了

                    val hs=zipFile.fileHeaders

                    for (k in hs){

                        if (k.fileName.startsWith(h.fileName)&&!k.isDirectory){//文件夹形式开头

                            val a=Picture(0,k.fileName,0,chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                            pics.add(a)

                        }


                    }

                    pics.sortWith(Comparator { p0, p1 -> order(p0!!.content)- order(p1!!.content) })



                }

            } else {//文件夹

                val f=File(comic.path)

                if (f.isDirectory){


                    val ls=f.list()

                    if (ls!=null&&ls.isNotEmpty()){

                        val op=ls.get(0);
                        if (ChaptersNameUtils.isImage(op)){

                            for (i in ls){

                                val s=Picture(0,i,0,chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                                pics.add(s)

                            }
                            pics.sortWith(Comparator {p0, p1 -> order(p0!!.content) - order(p1!!.content)})

                            return pics
                        }

                    }

                    val ch=File(f,chapter.name)

                    if (ch.exists()){

                        if (ch.name.endsWith(".zip")){//是zip

                            val z=ZipFile(ch)
                            val zs=z.fileHeaders

                            for (zz in zs){

                                if (ChaptersNameUtils.isImage(zz.fileName)) {

                                    val pp = Picture(0, zz.fileName, 0, chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                                    pics.add(pp)
                                }

                            }


                        }else if(ch.isDirectory){//是文件夹

                            val fs=ch.list()

                            if (fs!=null) {

                                for (ff in fs) {

                                    if (ChaptersNameUtils.isImage(ff)) {

                                        val ps = Picture(0, ff, 0, chapter.id,comic.zip,comic.title,comic.path,chapter.name)
                                        pics.add(ps)

                                    }

                                }

                                //排序，怕乱
                                pics.sortWith(Comparator { p0, p1 -> order(p0!!.content) - order(p1!!.content) })
                            }
                        }
                    }
                }
            }
            return pics
        }


        private fun splitName(name: String): String {

            var n = name

            if (name.contains("/")) {
                n = name.substring(name.indexOf("/"), name.length)
            }

            return n

        }

        private fun getCachePath(title: String, name: String): String {


            return RuminasuApp.context.getExternalFilesDir(null)!!.absolutePath + "/comic_file/" + title + "/" + name


        }

    }
}