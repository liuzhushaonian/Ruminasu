package com.app.legend.ruminasu.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Type
import com.app.legend.ruminasu.fragments.ComicChapterFragment


class ComicChapterPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    lateinit var list:List<Type>
    lateinit var comic: Comic

    fun initData(tt:List<Type>,c:Comic){
        this.list=tt
        this.comic=c

    }


    override fun getItem(p0: Int): Fragment {

        val type=list.get(p0)

        val fragment= ComicChapterFragment()

        val bundle=Bundle()

        bundle.putParcelable("comic",comic)

        bundle.putParcelable("type",type)

        fragment.arguments=bundle

        return fragment

    }

    override fun getCount(): Int {
        return list.size
    }
}