package com.app.legend.ruminasu.activityes


import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.adapters.ChapterViewAdapter
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Picture
import com.app.legend.ruminasu.presenters.ComicViewPresenter
import com.app.legend.ruminasu.presenters.interfaces.IComicViewActivity
import com.app.legend.ruminasu.utils.Conf
import kotlinx.android.synthetic.main.activity_comic_view.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * 查看漫画的Activity
 * viewpager形式
 * 章节连续
 * 左右滑动或上下滑动
 * 快速跳过
 * 后续再说吧，完成比完美更重要
 */
class ComicViewActivity : BasePresenterActivity<IComicViewActivity, ComicViewPresenter>(),
    IComicViewActivity {

    private var comic: Comic? = null
    private var chapter: Chapter? = null
    private var adapter: ChapterViewAdapter = ChapterViewAdapter()
    private var mode = Conf.VERTICAL
    private var chapters:MutableList<Chapter>?=null

    private var currentPosition=0//当前漫画章节位置

    private var currentPage=0//当前页数

    private var go_on=true

    var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)


    override fun createPresenter(): ComicViewPresenter {
        return ComicViewPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_comic_view)

        initInfos()

        initData()

        initToolbar()

        hideInfoViews()
    }

    override fun finish() {
        super.finish()
        go_on=false
    }

    override fun onDestroy() {
        super.onDestroy()
        go_on=false
    }

    /**
     * 初始化comic以及chapter
     * 然后根据这两个获取真正的图片并阅览
     */
    private fun initInfos() {

        comic = intent.getParcelableExtra("comic")
        chapter = intent.getParcelableExtra("chapter")

        chapters=intent.getParcelableArrayListExtra("chapters")

        currentPosition=intent.getIntExtra("position",0)


        mode = getSharedPreferences(Conf.SHARED, Context.MODE_PRIVATE).getInt(
            Conf.READ_MODE,
            Conf.VERTICAL
        )



    }

    override fun setData(list: MutableList<Picture>) {


        when (mode) {

            Conf.VERTICAL -> {//垂直模式

                initRecyclerViewTouch(list)

            }

            Conf.HORIZONTAL -> {


            }

        }

        initBottomInfos()//初始化底部的信息

    }


    fun initData() {

        presenter.getPicture(comic!!, chapter!!)

    }

    private fun initRecyclerViewTouch(list: MutableList<Picture>) {

        adapter = ChapterViewAdapter()
        adapter.pictures = list
        verticalRecyclerView.adapter = adapter
        verticalRecyclerView.layoutManager = linearLayoutManager

        initRecyclerViewTouch()


        verticalRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val p=linearLayoutManager.findFirstVisibleItemPosition()

                currentPage=p

                val info=getBottomInfos()

                bottom_info.text=info

            }
        })

    }

    private fun initToolbar() {

        comicViewToolbar.title = comic!!.title

        setSupportActionBar(comicViewToolbar)

        comicViewToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        comicViewToolbar.setNavigationOnClickListener {
            finish()
        }
    }


    /**
     * 隐藏toolbar以及底部的操作设置
     */
    private fun hideInfoViews() {

        comicViewToolbar.visibility = View.GONE
        tool_container.visibility = View.GONE

    }

    private fun hideByAnimation(view: View) {

        val animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(300)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {

                if (view.visibility == View.VISIBLE) {
                    view.visibility = View.GONE
                }

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
//                if (view.visibility==View.GONE){
//                    view.visibility=View.VISIBLE
//                }
            }

        })

        animator.start()


    }


    private fun showByAnimation(view: View) {

        val animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(300)

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {

//                if (view.visibility == View.GONE) {
//                    view.visibility = View.VISIBLE
//                }

            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {
                if (view.visibility==View.GONE){
                    view.visibility=View.VISIBLE
                }
            }

        })

        animator.start()


    }


    private fun getBottomInfos():String{

        val size=adapter.getSizes()+1

        val simpleDateFormat=SimpleDateFormat("HH:mm:ss", Locale.CHINA)

        val date=Date(System.currentTimeMillis())

        val time=simpleDateFormat.format(date)

        var name=chapter!!.name

        name=name.replace("/","")

        name=name.replace(".zip","")

        val p=currentPage+1

        val r="$name $p/$size $time"

        return r
    }


    /**
     * 底部信息
     * 当前话名字 从当前chapter获取
     * 当前页数 从当前pictues获取
     * 总页数 从当前pictures获取
     * 当前时间 从系统获取
     */
    private fun initBottomInfos() {

        val thread=object :Thread(){
            override fun run() {
                super.run()

                while (go_on){

                    val info=getBottomInfos()

                    handle.obtainMessage(10,info).sendToTarget()

                    sleep(1000)
                }

            }
        }.start()

    }


    private val handle=object :Handler(Looper.getMainLooper()){

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when(msg.what){

                10->{

                    val info:String=msg.obj.toString()
                    bottom_info.text=info
                }
            }
        }
    }



    /**
     * 触摸
     */
    private fun initRecyclerViewTouch() {


        verticalRecyclerView.setOnTouchListener(object : View.OnTouchListener {

            var r = false

            var x = 0f

            var y = 0f

            var dx = 0f
            var dy = 0f

            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {


                if (p1!!.action == MotionEvent.ACTION_DOWN) {

                    x = p1.x
                    y = p1.y
                    r = true
                }

                if (p1.action == MotionEvent.ACTION_MOVE) {

                    dx = p1.x - x
                    dy = p1.y - y

                    if (abs(dx) > 16 || abs(dy) > 16) {

                        r = false

                    }
                }

                if (p1.action == MotionEvent.ACTION_UP) {

                    if (r) {//将收集到的数据发出去，完美~

                        Log.d("xx--->>>", "$x")
                        Log.d("yy--->>>", "$y")

                        clickRecyclerView(x.toInt(),y.toInt())
                    }
                }
                return r
            }
        })

    }

    /**
     * 处理点击recyclerview后的事件，根据不同位置操作不同fun
     */
    private fun clickRecyclerView(x: Int, y: Int) {

        val width = window.decorView.measuredWidth
        val height = window.decorView.measuredHeight

        val oneThirdWidth = width / 3

        val oneThirdHeight = height / 3

        val halfWidth = width / 2

//        val halfHeight = height / 2

        if (y < oneThirdHeight || y > 2 * oneThirdHeight) {//上面1/3处||下面


            if (x < halfWidth) {//在左边

                clickLeft()

            } else if (x >= halfWidth) {//在右边

                clickRight()
            }


        } else if (y <= 2 * oneThirdHeight && y >= oneThirdHeight) {//中间

            if (x < oneThirdWidth) {//左边

                clickLeft()

            } else if (x >= oneThirdWidth && x <= oneThirdWidth * 2) {//中间，显示菜单

                togglePreference()

            } else if (x > 2 * oneThirdWidth) {//右边

                clickRight()

            }
        }
    }

    /**
     * 显示或隐藏设置
     */
    private fun togglePreference() {

        if (comicViewToolbar.visibility == View.GONE) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            showByAnimation(comicViewToolbar)


        } else if (comicViewToolbar.visibility == View.VISIBLE) {
            hideByAnimation(comicViewToolbar)

            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (tool_container.visibility == View.GONE) {
            showByAnimation(tool_container)
        } else if (tool_container.visibility == View.VISIBLE) {
            hideByAnimation(tool_container)
        }


    }


    private fun clickLeft(){

        val m=getSharedPreferences(Conf.SHARED, Context.MODE_PRIVATE).getInt(Conf.TOUCH_MODE,Conf.TOUCH_RIGHT)

        if (m==Conf.TOUCH_LEFT){

            autoSlide(Conf.UP)

        }else if (m==Conf.TOUCH_RIGHT){

            autoSlide(Conf.DOWN)

        }



    }


    private fun clickRight(){

        val m=getSharedPreferences(Conf.SHARED, Context.MODE_PRIVATE).getInt(Conf.TOUCH_MODE,Conf.TOUCH_RIGHT)

        if (m==Conf.TOUCH_LEFT){

            autoSlide(Conf.DOWN)

        }else if (m==Conf.TOUCH_RIGHT){

            autoSlide(Conf.UP)

        }

    }


    /**
     * 自动向上滑动
     */
    private fun autoSlide(type: Int) {

        val dy = window.decorView.measuredHeight / 2

        if (verticalRecyclerView.visibility == View.VISIBLE) {

            when (type) {

                Conf.UP -> {
                    verticalRecyclerView.smoothScrollBy(0, dy)
                }

                Conf.DOWN -> {
                    verticalRecyclerView.smoothScrollBy(0, -dy)
                }
            }
        }
    }


}
