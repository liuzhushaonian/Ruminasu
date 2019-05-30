package com.app.legend.ruminasu.activityes

import android.os.Bundle
import android.os.PersistableBundle
import com.app.legend.ruminasu.presenters.BasePresenter

abstract class BasePresenterActivity <V,T : BasePresenter<V>> : BaseActivity(){

    protected lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        presenter=createPresenter()
        presenter.attachView(this as V)
    }

    protected abstract fun createPresenter():T

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }


}