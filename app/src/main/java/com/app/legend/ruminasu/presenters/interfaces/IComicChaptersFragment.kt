package com.app.legend.ruminasu.presenters.interfaces

import com.app.legend.ruminasu.beans.Chapter

interface IComicChaptersFragment {

    fun setChapters(chapters:MutableList<Chapter>)

    fun refresh(p:Int)

}