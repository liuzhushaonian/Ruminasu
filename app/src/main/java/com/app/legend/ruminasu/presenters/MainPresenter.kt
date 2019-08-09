package com.app.legend.ruminasu.presenters

import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.presenters.interfaces.IMainActivity
import com.app.legend.ruminasu.utils.Conf
import com.app.legend.ruminasu.utils.ZipUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import java.io.File

class MainPresenter(activty: IMainActivity?) : BasePresenter<IMainActivity>() {

    private lateinit var activty:IMainActivity

    init {
        attachView(activty!!)
        this.activty = this.getView()!!
    }

    fun getData(){

        data()

    }

    /**
     * 获取数据
     */
    private fun data(){

        Observable.create(ObservableOnSubscribe<List<Comic>> {
            e ->

            val path=Conf.PATH;

            val file = File(path)

            if (!file.exists()){
                file.mkdirs()
            }

            val list=getList(file)

            e.onNext(list)
            e.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(


                onNext = {
                    activty.setData(it)
                }

            )
    }

    /**
     * 获取漫画。可能是文件夹，也可能是zip包
     */
    private fun getList(file: File):List<Comic>{


        val list=file.listFiles()

        val comicList:MutableList<Comic> = ArrayList()

        for (f:File in list){

            val comic=Comic(null,f.name,0,0,null)

            if (f.isDirectory){

                comic.path=f.absolutePath
                comic.title=f.name

                comicList.add(comic)


            }else if (f.name.endsWith("zip")){

                comic.title=f.name.replace(".zip","")//去掉.zip后缀
                comic.path=f.absolutePath

                ZipUtils.getFirstBook(f.absolutePath)

                comicList.add(comic)

            }

        }

        return comicList

    }

}