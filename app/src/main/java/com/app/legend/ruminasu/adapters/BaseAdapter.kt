package com.app.legend.ruminasu.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

abstract class BaseAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}