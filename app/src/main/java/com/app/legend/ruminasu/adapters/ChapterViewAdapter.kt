package com.app.legend.ruminasu.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.utils.glide.InputStreamDataModel
import com.bumptech.glide.Glide

class ChapterViewAdapter : BaseAdapter<ChapterViewAdapter.ViewHolder>() {


    var pictures:MutableList<Picture>? =null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (pictures==null){
            return
        }

        val p=pictures!!.get(position)

        Glide.with(holder.view).load(p).into(holder.imageView)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val view=LayoutInflater.from(p0.context).inflate(R.layout.vertical_image_item,p0,false)
        val viewHolder=ViewHolder(view)


        return viewHolder
    }

    override fun getItemCount(): Int {

        if (pictures!=null){
            return pictures!!.size
        }

        return super.getItemCount()
    }


    inner class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

        val imageView:ImageView=itemView.findViewById(R.id.image_item)
        val view=itemView
    }
}