package com.app.legend.ruminasu.presenters

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.app.legend.ruminasu.activityes.MainActivity
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Path
import com.app.legend.ruminasu.presenters.interfaces.IMainActivity
import com.app.legend.ruminasu.utils.*
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

    private var activty:IMainActivity

    init {
        attachView(activty!!)
        this.activty = this.getView()!!
    }


    /**
     * 检测有没有地址，没有就提示添加，有就按地址查询漫画
     * 从数据库获取数据
     */
    fun getData(activity: Activity){

        Observable.create(ObservableOnSubscribe<List<Comic>> {
            e->

            val comics=Database.getDefault(activity).getComics(Conf.SHOW)

            e.onNext(comics)
            e.onComplete()

        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(

                onNext = {

                    checkComics(activity,it)

                }

            )


    }

    /**
     * 检测数据库内搜索出来的漫画是否为0，如果是，则检测路径表，如果没有路径，就提示选择路径，如果有，则搜索该路径
     */
    private fun checkComics(activity: Activity,comics:List<Comic>){

        if (comics.isNotEmpty()){//有数据
            activty.setData(comics)

            saveComicAndGetBook(activity,comics)

        }else{//没有数据
            getPaths()
        }
    }

    /**
     * 获取路径，检测是否为0
     */
    private fun getPaths(){

        Observable.create(ObservableOnSubscribe<List<Path>> {

            val paths=Database.getDefault(RuminasuApp.context).getPaths()
            it.onNext(paths)
            it.onComplete()

        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(

            onNext = {
                checkPaths(it)
            }
        )
    }

    private fun checkPaths(paths:List<Path>){

        if (paths.isEmpty()){//空的，显示提示

            activty.showInfo()

        }else{//有路径，根据路径搜索其下的漫画

            data(paths)

        }
    }





    /**
     * 获取数据
     */
    private fun data(paths: List<Path>){

        Observable.create(ObservableOnSubscribe<List<Comic>> {

            val list:MutableList<Comic> =ArrayList()

            for (path in paths) {
                val folder=File(path.path)
                list.addAll(getList(folder))
            }

            it.onNext(list)
            it.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(


                onNext = {
                    activty.setData(it)
                    activty.load(it)
//                    activty.load(it)
                }

            )
    }

    /**
     * 获取漫画。可能是文件夹，也可能是zip包
     */
    private fun getList(file: File):List<Comic>{

        val list=file.listFiles()
        val comicList:MutableList<Comic> = ArrayList()

        if (list==null){

            return comicList
        }


        for (f:File in list){

            val comic=Comic("",f.name,"",-1,0,1,false)

            if (f.isDirectory){
                comic.path=f.absolutePath
                comic.title=f.name
                comic.zip=-1
                comicList.add(comic)
            }else if (f.name.endsWith("zip")){

                comic.title=f.name.replace(".zip","")//去掉.zip后缀
                comic.path=f.absolutePath
                comic.zip=1
//                ZipUtils.getFirstBook(f.absolutePath,comic.title)
                comicList.add(comic)
            }
        }
        return comicList
    }

    fun addPath(path:String,activity: Activity){

       val r= Database.getDefault(activity).addPaths(path)

        if (r>0){

            Toast.makeText(activity,"路径添加成功",Toast.LENGTH_LONG).show()
        }else{

            Toast.makeText(activity,"路径添加失败",Toast.LENGTH_LONG).show()

        }

    }

    /**
     * 获取id，以及保存封面
     */
    fun saveComicAndGetBook(a: Activity, comics: List<Comic>){

        Observable.create(ObservableOnSubscribe<Int> {

            for (c in comics){

                if (c.book.isBlank()) {

                    //先获取封面，再保存到数据库
                    val book = ComicUtils.getComicBook(c)
                    c.book = book//赋值，无论是不是null,封面获取完成
                    Database.getDefault(a).addComic(c)

                    val p = comics.indexOf(c)

                    it.onNext(p)
                }

            }

            it.onComplete()

        }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {

                    activty.refresh(it)

                },

                onComplete = {

                }
            )

    }


}