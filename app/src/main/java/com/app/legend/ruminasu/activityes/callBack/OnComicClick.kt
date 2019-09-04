package com.app.legend.ruminasu.activityes.callBack

import com.app.legend.ruminasu.beans.Comic

interface OnComicClick {

    public fun comicClick(position:Int,comic: Comic)

}