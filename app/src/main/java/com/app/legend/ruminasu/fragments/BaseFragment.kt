package com.app.legend.ruminasu.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.app.legend.ruminasu.utils.Database

abstract class BaseFragment : Fragment(){

    protected var database:Database?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database= Database.getDefault(context)
    }
}