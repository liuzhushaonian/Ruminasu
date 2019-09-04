package com.app.legend.ruminasu.presenters

import android.util.Log
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.presenters.interfaces.IComicChaptersFragment
import com.app.legend.ruminasu.utils.ChaptersNameUtils
import com.app.legend.ruminasu.utils.Database
import com.app.legend.ruminasu.utils.RuminasuApp
import com.app.legend.ruminasu.utils.ZipUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.lingala.zip4j.ZipFile
import java.io.File

class ComicChaptersPresenter(private var fragment: IComicChaptersFragment):BasePresenter<IComicChaptersFragment> (){


    init {
        attachView(this.fragment)
        this.fragment=getView()!!
    }

    public fun getChapters(comic: Comic,type: Type){

        Database.getDefault(RuminasuApp.context).addTypes(type)

        getComicChapters(comic,type)
    }


    /**
     * 阶段1 从数据库内获取
     * 如果没有就从文件内获取
     */
    private fun getComicChapters(comic: Comic,type: Type){

        Observable.create(ObservableOnSubscribe<MutableList<Chapter>> {

            val chapters:MutableList<Chapter> = getChaptersFromDatabase(comic, type)
//            if (chapters.isEmpty()){
//
//
//                chapters=getChapterByType(comic, type)
//
//                saveChaptersToDatabase(comic,chapters)
//            }

            it.onNext(chapters)
            it.onComplete()


        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {

                    if (it.isEmpty()){
                        getChaptersFromFile(comic, type)
                    }else {

                        fragment.setChapters(it)
                    }

                }
            )

    }

    /**
     * 步骤2 从数据库内获取不到章节，从文件内直接拿
     */
    private fun getChaptersFromFile(comic: Comic,type: Type){

        Observable.create(ObservableOnSubscribe<MutableList<Chapter>> {

            val chapters:MutableList<Chapter> = getChapterByType(comic, type)
//            if (chapters.isEmpty()){
//
//
//                chapters=getChapterByType(comic, type)
//
                saveChaptersToDatabase(comic,chapters)//在这里保存到数据库并获取id
//            }

            it.onNext(chapters)
            it.onComplete()


        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {

                    if (it.isEmpty()){//如果是空的，返回提示吧

                    }else {
                        fragment.setChapters(it)//在这里获取封面吧
                        getChaptersBook(comic,it)
                    }


                }
            )


    }




    /**
     * 获取封面图
     * 保存到缓存地
     * zip文件如何提取？
     * 文件夹又如何提取？
     */
    private fun getChaptersBook(comic: Comic,chapters: MutableList<Chapter>){

        Observable.create(ObservableOnSubscribe<Int> {

            for (chapter in chapters){

                val book=ZipUtils.getChapterBook(comic,chapter)
                chapter.book=book

                Database.getDefault(RuminasuApp.context).updateChapter(chapter)//更新

                val p=chapters.indexOf(chapter);
                it.onNext(p)

            }

            it.onComplete()

        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {

                    fragment.refresh(it)

                }
            )


//        val book=ZipUtils.getChapterBook(comic,chapter)
//        chapter.book=book

//        Log.d("book--->>>",chapter.book)

    }



    /**
     * 重命名给顺序命名
     */
    private fun  reName(chapter: Chapter){

//        chapter.orderNmae= String.format("%04d",chapter.name)
//        Log.d("orderNmae--->>>",chapter.orderNmae)

    }


    /**
     * 从数据库内获取章节，如果没有，就直接从文件内获取
     *
     */
    private fun getChaptersFromDatabase(comic: Comic,type: Type):MutableList<Chapter>{

        val chapters:MutableList<Chapter> =
            Database.getDefault(RuminasuApp.context).getChaptersByType(comic,type) as MutableList<Chapter>


        return chapters
    }


    /**
     * 保存章节到数据库内
     * 同时获取id，在这之前，先获取顺序名
     */
    private fun saveChaptersToDatabase(comic: Comic,chapters:MutableList<Chapter>){

            for (c in chapters){

                Database.getDefault(RuminasuApp.context)
                    .addChapter(comic,c)

            }
    }



    /**
     * 根据type生成chapter对象，并设置好id，orderName等属性，然后保存到数据库内
     * 内部可能是zip、文件夹或是直接是图片
     * wdmnd 难写的一批
     */
    private fun getChapterByType(comic: Comic,type:Type):MutableList<Chapter>{

        val chapters:MutableList<Chapter> = ArrayList()

        if (comic.zip>0){//是zip文件

            val z=ZipFile(comic.path)
            val header=z.fileHeaders
            if (header.size>0){
                val h= header[0]
                if (ChaptersNameUtils.isImage(h.fileName)){//说明里面是纯粹的图片，直接将整个封装成chapter

                    if (comic.title.contains(type.content)) {


                        val c = Chapter(comic.title, 1, type.id, -1, comic.book, 0, "",comic.id)

                        chapters.add(c)

                    }

                    return chapters
                }

                for (f in header){

                    if (f.fileName.contains(type.content)) {

                        val c = Chapter(f.fileName, 0, type.id, -1, "", 0, "",comic.id)

                        chapters.add(c)
                    }
                }
            }

        }else{//是文件夹

            val file=File(comic.path)

            if(file.isDirectory){

                val list:Array<File> = file.listFiles()

                if (list.isNotEmpty()){

                    val p=list[0]
                    if (ChaptersNameUtils.isImage(p.name)){//判断第一个文件是不是图片，是就直接返回吧，这里面只是图片了

                        if (file.name.contains(type.content)) {

                            val c = Chapter(comic.title, 0, type.id, -1, "", 0, "",comic.id)

                            chapters.add(c)
                        }
                        return chapters //??? 这里为啥要返回？？
                    }

                    for (f in list){

                        if (f.name.contains(type.content)) {

                            val c = Chapter(f.name, 0, type.id, -1, "", 0, "",comic.id)

                            chapters.add(c)
                        }
                    }
                }
            }
        }

        return chapters
    }

}