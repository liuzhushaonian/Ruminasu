package com.app.legend.ruminasu.activityes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.presenters.ComicInfoPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicInfoActivity

class ComicInfoActivity : BasePresenterActivity<IComicInfoActivity,ComicInfoPresenter>(),IComicInfoActivity {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic_info)
        presenter=createPresenter()


    }

    override fun createPresenter(): ComicInfoPresenter {
        return ComicInfoPresenter(this)
    }

    private fun getComponent(){



    }

}
