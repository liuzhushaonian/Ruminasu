package com.app.legend.ruminasu.presenters

import com.app.legend.ruminasu.presenters.interfaces.IComicChaptersFragment

class ComicChaptersPresenter(private var fragment: IComicChaptersFragment):BasePresenter<IComicChaptersFragment> (){


    init {
        attachView(this.fragment)
    }





}