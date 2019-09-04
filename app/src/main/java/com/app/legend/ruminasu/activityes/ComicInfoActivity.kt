package com.app.legend.ruminasu.activityes

import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.tabs.TabLayout
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.adapters.ComicChapterPagerAdapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.presenters.ComicInfoPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicInfoActivity
import kotlinx.android.synthetic.main.activity_comic_info.*

/**
 * 显示漫画的具体章节
 */
class ComicInfoActivity : BasePresenterActivity<IComicInfoActivity,ComicInfoPresenter>(),IComicInfoActivity {

    private lateinit var pagerAdapter:ComicChapterPagerAdapter
    private var comic: Comic? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic_info)

        initInfo()

        getType()

        initToolbar()


//        presenter=createPresenter()
    }

    override fun createPresenter(): ComicInfoPresenter {
        return ComicInfoPresenter(this)
    }


    /**
     * 获取分类
     */
    private fun getType(){


        presenter.getType(comic!!)

    }

    /**
     * 初始化pager
     * 在内部Fragment初始化章节，需要传输comic
     */
    override fun initPager(list: List<Type>) {

        if (list.size>1) {

            comic_type.visibility=View.VISIBLE

            for (t in list) {
                comic_type.addTab(comic_type.newTab().setText(t.content))
            }
        }else{
            comic_type.visibility=View.GONE

        }

        pagerAdapter= ComicChapterPagerAdapter(supportFragmentManager)
//        pagerAdapter.list=list
//        pagerAdapter.comic=this.comic!!

        pagerAdapter.initData(list,comic!!)

        comic_chapter_pager.adapter=pagerAdapter
        comic_chapter_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(comic_type))
        comic_type.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(comic_chapter_pager))

    }


    private fun initToolbar(){


        setSupportActionBar(comic_info_toolbar)

        comic_info_toolbar.navigationIcon=getDrawable(R.drawable.ic_arrow_back_black_24dp)
        comic_info_toolbar.setNavigationOnClickListener {finish()}
        comic_info_toolbar.title=comic!!.title



    }

    private fun  initInfo(){

        this.comic=intent.getParcelableExtra<Comic>("comic")
        comic_title.text=comic!!.title

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.comic_info_menu,menu)

        return true
    }


}
