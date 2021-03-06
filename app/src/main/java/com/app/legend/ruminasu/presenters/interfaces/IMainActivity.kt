package com.app.legend.ruminasu.presenters.interfaces

import com.app.legend.ruminasu.beans.Comic

interface IMainActivity {

    fun setData(list: List<Comic>)

    fun showInfo()

    fun refresh(p:Int)

    fun load(list: List<Comic>)

}