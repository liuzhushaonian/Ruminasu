package com.app.legend.ruminasu.presenters

import com.app.legend.ruminasu.activityes.BasePresenterActivity
import java.lang.ref.Reference
import java.lang.ref.WeakReference

abstract class BasePresenter<T> {

    private lateinit var res: Reference<T>


    public fun attachView(view: T){
        res=WeakReference<T>(view)
    }

    protected fun getView(): T? {

        return res.get()

    }

    public fun isViewAttached():Boolean{

        return res.get()!=null

    }

    public fun detachView(){
        res.clear()
    }


}