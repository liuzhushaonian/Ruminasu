package com.app.legend.ruminasu.fragments

import android.os.Bundle
import android.os.PersistableBundle
import com.app.legend.ruminasu.presenters.BasePresenter

abstract class BasePresenterFragment<V,T : BasePresenter<V>> :BaseFragment(){

    protected lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter=createPresenter()
        presenter.attachView(this as V)

    }

    protected abstract fun createPresenter():T

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }



}