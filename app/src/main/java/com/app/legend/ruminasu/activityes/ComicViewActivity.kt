package com.app.legend.ruminasu.activityes


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.adapters.ChapterViewAdapter
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.presenters.ComicViewPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicViewActivity
import com.app.legend.ruminasu.utils.ComicUtils
import com.app.legend.ruminasu.utils.Conf
import kotlinx.android.synthetic.main.activity_comic_view.*


/**
 * 查看漫画的Activity
 * viewpager形式
 * 章节连续
 * 左右滑动或上下滑动
 * 快速跳过
 * 后续再说吧，完成比完美更重要
 */
class ComicViewActivity : BasePresenterActivity<IComicViewActivity,ComicViewPresenter>(),IComicViewActivity {




    private var comic:Comic?=null
    private var chapter:Chapter?=null
    private var adapter:ChapterViewAdapter= ChapterViewAdapter()
    private var mode=Conf.VERTICAL

    var linearLayoutManager:LinearLayoutManager= LinearLayoutManager(this)


    override fun createPresenter(): ComicViewPresenter {
        return ComicViewPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic_view)

        initInfos()

        initData()
    }

    /**
     * 初始化comic以及chapter
     * 然后根据这两个获取真正的图片并阅览
     */
    private fun initInfos(){

        comic=intent.getParcelableExtra("comic")
        chapter=intent.getParcelableExtra("chapter")

        mode=getSharedPreferences(Conf.SHARED, Context.MODE_PRIVATE).getInt(Conf.READ_MODE,Conf.VERTICAL)
    }

    override fun setData(list: MutableList<Picture>) {


        when(mode){

            Conf.VERTICAL->{//垂直模式

                initRecyclerView(list)

            }

            Conf.HORIZONTAL->{



            }

        }

    }


    fun initData(){

        presenter.getPicture(comic!!,chapter!!)

    }

    private fun initRecyclerView(list: MutableList<Picture>) {

        adapter=ChapterViewAdapter()
        adapter.pictures=list
        verticalRecyclerView.adapter=adapter
        verticalRecyclerView.layoutManager=linearLayoutManager

    }


}
