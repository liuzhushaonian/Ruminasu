package com.app.legend.ruminasu.presenters

import android.database.DatabaseUtils
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.presenters.interfaces.IComicInfoActivity
import com.app.legend.ruminasu.utils.Database
import com.app.legend.ruminasu.utils.RuminasuApp
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import net.lingala.zip4j.ZipFile
import java.io.File

class ComicInfoPresenter(activity: IComicInfoActivity) : BasePresenter<IComicInfoActivity>() {

    private lateinit var activity:IComicInfoActivity


    init {

        attachView(activity)
        this.activity= this.getView()!!

    }

    /**
     * 根据漫画获取话数并给每一话进行分类，可先对内部数字进行补位，排序
     */
    fun getType(comic: Comic){

        getTypeFromDatabase(comic)

    }


    private fun getTypeFromDatabase(comic: Comic){

        Observable.create(ObservableOnSubscribe<List<Type>> {

            val ts= Database.getDefault(RuminasuApp.context).getTypeByComicId(comic)
            it.onNext(ts)
            it.onComplete()

        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    //判断是否为空，是则调用其他方法解析
                    if (it.isEmpty()){
                        loadType(comic)
                    }else{
                        activity.initPager(it)
                    }
                }
            )
    }



    /**
     * 获取分类，在这之前，先搜索数据库吧
     */
    private fun loadType(comic: Comic){

        Observable.create(ObservableOnSubscribe<List<Type>> {

            val list=getLists(comic)

            val types=ArrayList<Type>()

            for (t in list){

                val s=Type(0,comic.id,t)
                types.add(s)
            }

            it.onNext(types)

            it.onComplete()

        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(


                onNext = {

                    activity.initPager(it)

                }

            )

    }


    /**
     * 从话的名字获取每一话的分类，获取分类list，然后初始化相关Fragment
     */
   private fun getLists(comic: Comic):List<String>{

       val types=ArrayList<String>()

        if (comic.zip>0){//zip

           val z = ZipFile(comic.path)

           val list=z.fileHeaders

           for (header in list){

               if (header.fileName.contains("_")){//有下划线

                   val t:String=slitNameByTag("_",header.fileName)
                   if (!types.contains(t)){
                       types.add(t)
                   }
               }
           }

       }else{//folder

           val file= File(comic.path)

           val list=file.listFiles()

           for (c in list){

               if (c.name.contains("_")){

                   val t=slitNameByTag("_",c.name)

                   if (!types.contains(t)){
                       types.add(t)
                   }
               }
           }
       }

       return types


   }


    private fun slitNameByTag(tag:String,name:String):String{

        val index=name.indexOf(tag)

        return name.substring(0,index)

    }

}