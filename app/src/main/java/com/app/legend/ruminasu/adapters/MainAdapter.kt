package com.app.legend.ruminasu.adapters

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.activityes.callBack.OnComicClick
import com.app.legend.ruminasu.beans.Comic
import com.bumptech.glide.Glide


class MainAdapter : BaseAdapter<MainAdapter.ViewHolder>(){

    private var comicList: List<Comic>? =null
    private var onComicClick:OnComicClick?=null

    public fun setClick(onComicClick:OnComicClick){

        this.onComicClick=onComicClick

    }


    public fun setComicList(list: List<Comic>){

        this.comicList=list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val view=LayoutInflater.from(p0.context).inflate(R.layout.comic_item,p0,false)

        val holder = ViewHolder(view)

        holder.view.setOnClickListener {

            val p=holder.adapterPosition
            val c=comicList!!.get(p)

            onComicClick!!.comicClick(p,c)

        }

        return holder
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


        val comic= this.comicList?.get(p1)

        p0.title.text = comic?.title

        Glide.with(p0.view)
            .load(comic!!.book)
            .error(R.drawable.mo)
            .fallback(R.drawable.mo)
            .into(p0.book)

    }

    override fun getItemCount(): Int {

        if (this.comicList==null){
            return 0
        }

        return this.comicList!!.size
    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var book:ImageView=itemView.findViewById(R.id.book)
        var title:TextView=itemView.findViewById(R.id.title)
        var view:View=itemView

        init {
            reDraw()
        }

        /**
         * 重绘view的大小
         */
        private fun reDraw(){

            val params=itemView.layoutParams
            val space=itemView.resources.getDimensionPixelOffset(R.dimen.item_space)*4
            val width:Int=((itemView.resources.displayMetrics.widthPixels-space)/3)

//            Log.d("width--->>>",space.toString())

            val height:Int=(width*1.333333).toInt()

            params.height=height
            params.width=width

            itemView.layoutParams=params

        }

    }
}