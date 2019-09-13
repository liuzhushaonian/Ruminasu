package com.app.legend.ruminasu.fragments


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.activityes.ComicViewActivity
import com.app.legend.ruminasu.activityes.callBack.OnChapterClick
import com.app.legend.ruminasu.adapters.ChapterListAdapter
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.presenters.ComicChaptersPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicChaptersFragment
import kotlinx.android.synthetic.main.fragment_comic_chapter.*

/**
 * A simple [Fragment] subclass.
 * 显示漫画章节列表
 * 列表形式或者表格按钮形式
 * 提供正序与倒序
 *
 */
class ComicChapterFragment : BasePresenterFragment<IComicChaptersFragment, ComicChaptersPresenter>(),IComicChaptersFragment {


    private var comic: Comic? =null
    private var type:Type?=null
    private var adapter: ChapterListAdapter = ChapterListAdapter()
    private val linearLayoutManager=LinearLayoutManager(context)
    private lateinit var recyclerView: RecyclerView

    override fun createPresenter(): ComicChaptersPresenter {
        return ComicChaptersPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        getData()

        val view=inflater.inflate(R.layout.fragment_comic_chapter, container, false)

        recyclerView=view.findViewById(R.id.chaptersRecyclerView)

        return view
    }

    private fun getData(){
        comic=arguments?.getParcelable("comic")
        type=arguments?.getParcelable("type")
        presenter.getChapters(comic!!,type!!)

    }


    /**
     * 获取到章节
     * 给设置上
     */
    override fun setChapters(chapters: MutableList<Chapter>) {
        initList(chapters)
    }

    override fun refresh(p: Int) {
        adapter.notifyItemChanged(p)
    }


    /**
     * 初始化网格列表
     */
    private fun initGridList(){


    }

    /**
     * 初始化列表
     */
    private fun initList(chapters: MutableList<Chapter>){

        chapters.reverse()//倒序

        adapter.onChapterClick=object :OnChapterClick{

            override fun onChapterClick(position: Int, chapter: Chapter) {
                clickChapter(position,chapter,chapters as ArrayList<Chapter>)
            }

        }

        adapter.chaptersList=chapters

        recyclerView.adapter=adapter

        recyclerView.layoutManager=linearLayoutManager

    }

    private fun clickChapter(p:Int,chapter: Chapter,chapters:ArrayList<Chapter>){

        val intent=Intent(activity,ComicViewActivity::class.java)

        intent.putExtra("comic",comic)
        intent.putExtra("chapter",chapter)
        intent.putParcelableArrayListExtra("chapters",chapters)
        intent.putExtra("position",p)

        startActivity(intent)

    }


}
