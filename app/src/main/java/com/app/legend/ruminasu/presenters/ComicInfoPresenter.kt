package com.app.legend.ruminasu.presenters

import com.app.legend.ruminasu.presenters.interfaces.IComicInfoActivity

class ComicInfoPresenter(activity: IComicInfoActivity) : BasePresenter<IComicInfoActivity>() {

    private lateinit var activity:IComicInfoActivity


    init {

        attachView(activity)
        this.activity= this.getView()!!

    }



}