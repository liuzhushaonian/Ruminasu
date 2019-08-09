package com.app.legend.ruminasu.activityes


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.presenters.ComicInfoPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicInfoActivity

/**
 * 显示漫画的具体章节
 */
class ComicInfoActivity : BasePresenterActivity<IComicInfoActivity,ComicInfoPresenter>(),IComicInfoActivity {


    private lateinit var toolbar:Toolbar
    private lateinit var scrollLayout:NestedScrollView
    private lateinit var comicBook:ImageView
    private lateinit var comicTitle:TextView
    private lateinit var comicType:TabLayout
    private lateinit var comicChaptersPager:ViewPager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic_info)

        getComponent()

//        presenter=createPresenter()
    }

    override fun createPresenter(): ComicInfoPresenter {
        return ComicInfoPresenter(this)
    }

    private fun getComponent(){

        toolbar=findViewById(R.id.comic_info_toolbar)
        scrollLayout=findViewById(R.id.scroll_layout)
        comicBook=findViewById(R.id.comic_book)
        comicTitle=findViewById(R.id.comic_title)
        comicType=findViewById(R.id.comic_type)
        comicChaptersPager=findViewById(R.id.comic_chapter_pager)

    }

}
