package com.app.legend.ruminasu.presenters

import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.presenters.interfaces.IComicViewActivity
import com.app.legend.ruminasu.utils.ComicUtils
import com.app.legend.ruminasu.utils.Database
import com.app.legend.ruminasu.utils.RuminasuApp
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ComicViewPresenter(activity: IComicViewActivity) :BasePresenter<IComicViewActivity>(){

    private val activity:IComicViewActivity

    init {
        attachView(activity)
        this.activity=this.getView()!!
    }

    fun getPicture(comic: Comic,chapter: Chapter){

        getPictureFromDatabase(comic, chapter)

    }


    private fun getPictureFromDatabase(comic: Comic,chapter: Chapter){

        Observable.create(ObservableOnSubscribe<MutableList<Picture>> {

            val list=Database.getDefault(RuminasuApp.context).getPicInfo(chapter)
            it.onNext(list)


        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(

                onNext = {

                    if (it.isNotEmpty()){

                        activity.setData(it)

                    }else{

                        getPictureFromFile(comic,chapter)

                    }


                },

                onComplete = {


                }

            )

    }


    private fun getPictureFromFile(comic: Comic,chapter: Chapter){

        Observable.create(ObservableOnSubscribe<MutableList<Picture>> {

            val list=ComicUtils.getChapterPictures(comic,chapter)
            it.onNext(list)
            it.onComplete()

        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(

                onNext = {
                    activity.setData(it)
                    saveToDatabase(comic,it,chapter)
                },

                onComplete = {

                }

            )
    }

    private fun  saveToDatabase(comic: Comic,list: MutableList<Picture>,chapter: Chapter){

        for (p in list){

            Database.getDefault(RuminasuApp.context).addPic(comic,chapter,p)

        }

    }

}