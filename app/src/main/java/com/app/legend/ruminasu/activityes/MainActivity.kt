package com.app.legend.ruminasu.activityes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.activityes.callBack.OnComicClick
import com.app.legend.ruminasu.adapters.MainAdapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.presenters.MainPresenter
import com.app.legend.ruminasu.presenters.interfaces.IMainActivity
import com.app.legend.ruminasu.utils.FileUtils
import com.app.legend.ruminasu.utils.MainItemSpace
import java.io.File
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BasePresenterActivity <IMainActivity,MainPresenter>() ,IMainActivity{


    private lateinit var layoutManager: GridLayoutManager
    private var adapter=MainAdapter()
    private var permission=Array(1){ Manifest.permission.WRITE_EXTERNAL_STORAGE}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        click()
        initList()
        initToolbar()
        getPermission()
    }


    override fun createPresenter(): MainPresenter {

        return MainPresenter(this)
    }

    private fun initList(){
        layoutManager= GridLayoutManager(this, 3)
        adapter.setClick(onComicClick = object : OnComicClick {
            override fun comicClick(position: Int, comic: Comic) {
                clickComic(position,comic)
            }
        })
        book_list.layoutManager= layoutManager
        book_list.adapter=adapter
        book_list.addItemDecoration(MainItemSpace())

    }

    private fun initToolbar(){
        this.toolbar.title=""
        setSupportActionBar(toolbar)
    }

    private fun getPermission(){

        if (ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permission,1000)
        }else{
            getData()
        }

    }

    private fun getData(){
        presenter.getData(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            1000->{
                if (grantResults.isNotEmpty() &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getData()
                }else{
                    Toast.makeText(this,"请赋予权限！",Toast.LENGTH_SHORT).show()
                }
            }

            2000->{

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    /**
     * 获取到数据
     * 先保存到数据库，并获取id以及封面
     */
    override fun setData(list: List<Comic>) {

        no_path.visibility=View.GONE
        book_list.visibility=View.VISIBLE
        adapter.setComicList(list)


//        load(list)
    }

    override fun showInfo() {

        no_path.visibility=View.VISIBLE
        book_list.visibility=View.GONE

    }

    override fun load(list: List<Comic>) {
        presenter.saveComicAndGetBook(this,list)
    }


    override fun refresh(p:Int) {

        adapter.notifyItemChanged(p)

    }

    fun click(){

        no_path.setOnClickListener {

            val intent= Intent(Intent.ACTION_GET_CONTENT)

            intent.type="*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(intent,100)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){

            100 -> {

                if (data!=null){

                    val p=FileUtils.getFilePathByUri(this,data.data);
                    val file=File(p)
                    if (file.exists()){
                        presenter.addPath(file.parent,this)
                        getData()
                    }
                }
            }
        }
    }

    /**
     * 点击后跳转到下一个Activity
     */
    private fun clickComic(p:Int,comic: Comic){

        val intent=Intent(this,ComicInfoActivity::class.java)

        intent.putExtra("comic",comic)

        startActivity(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

}
