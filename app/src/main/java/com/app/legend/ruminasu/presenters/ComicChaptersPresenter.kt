package com.app.legend.ruminasu.presenters

import android.util.Log
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.presenters.interfaces.IComicChaptersFragment
import com.app.legend.ruminasu.utils.*
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

                        getChaptersBook(comic,it)//继续获取封面，如果没有的话
                    }

                }
            )

    }

    /**
     * 步骤2 从数据库内获取不到章节，从文件内直接拿
     */
    private fun getChaptersFromFile(comic: Comic,type: Type){

        Observable.create(ObservableOnSubscribe<MutableList<Chapter>> {

            val chapters:MutableList<Chapter> = ComicUtils.getChapterByType(comic, type)
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

                if (chapter.book.isBlank()) {

                    val book = ComicUtils.getChapterBook(comic, chapter)
                    chapter.book = book!!

                    Database.getDefault(RuminasuApp.context).updateChapter(chapter)//更新

                    val p = chapters.indexOf(chapter);
                    it.onNext(p)
                }

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

}