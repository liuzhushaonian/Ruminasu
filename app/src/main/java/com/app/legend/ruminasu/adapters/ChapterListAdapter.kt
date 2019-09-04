package com.app.legend.ruminasu.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.bumptech.glide.Glide
import net.lingala.zip4j.ZipFile

class ChapterListAdapter : BaseAdapter<ChapterListAdapter.ViewHolder>() {

    lateinit var chaptersList:MutableList<Chapter>
    var comic:Comic?=null


    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val c=chaptersList.get(p1)

        val name=c.name.replace(".zip","")
        p0.name.text=name

        Glide.with(p0.view).load(c.book).into(p0.chapterBook)

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val view=LayoutInflater.from(p0.context).inflate(R.layout.chapter_item,p0,false)


        val viewHolder=ViewHolder(view)

        viewHolder.view.setOnClickListener {


        }


        return viewHolder
    }

    override fun getItemCount(): Int {

        return chaptersList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var chapterBook:ImageView = itemView.findViewById(R.id.chapter_book)
        var chapterMark:ImageView=itemView.findViewById(R.id.chapter_mark)
        var name:TextView=itemView.findViewById(R.id.chapter_name)
        var view:View=itemView


    }


    private fun bindImage(imageView: ImageView,chapter: Chapter){

        if (chapter.name.endsWith(".zip")){



        }else{



        }



    }

}