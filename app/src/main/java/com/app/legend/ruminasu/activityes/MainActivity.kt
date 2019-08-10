package com.app.legend.ruminasu.activityes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.*
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.app.legend.ruminasu.R
import com.app.legend.ruminasu.adapters.MainAdapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.presenters.MainPresenter
import com.app.legend.ruminasu.presenters.interfaces.IMainActivity
import com.app.legend.ruminasu.utils.FileUtils
import com.app.legend.ruminasu.utils.MainItemSpace
import java.io.File
import java.net.URI

class MainActivity : BasePresenterActivity <IMainActivity,MainPresenter>() ,IMainActivity{

    private lateinit var  recycler:RecyclerView
    private lateinit var layoutManager:GridLayoutManager
    private lateinit var adapter:MainAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var no_path:TextView
    private lateinit var addBtn:FloatingActionButton

    private var permission=Array(1){ Manifest.permission.WRITE_EXTERNAL_STORAGE}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getComponent()
        click()
        initList()
        initToolbar()
        getPermission()

    }


    override fun createPresenter(): MainPresenter {

        return MainPresenter(this)
    }


    private fun getComponent(){

        recycler=findViewById(R.id.book_list)
        toolbar=findViewById(R.id.toolbar)
        no_path=findViewById(R.id.no_path)
        addBtn=findViewById(R.id.add_btn)

    }

    private fun initList(){

        layoutManager=GridLayoutManager(this,3)
        adapter= MainAdapter()

        recycler.layoutManager= layoutManager
        recycler.adapter=adapter
        recycler.addItemDecoration(MainItemSpace())

    }

    private fun initToolbar(){

        this.toolbar.title=""

        setSupportActionBar(toolbar)


    }

    private fun getPermission(){

        if (ContextCompat.checkSelfPermission(this, permission[0]) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,permission,1000)

        }else{

//            val comic:Comic=Comic("","","",0,0)

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

    override fun setData(list: List<Comic>) {

        no_path.visibility=View.GONE
        recycler.visibility=View.VISIBLE
        adapter.setComicList(list)

    }

    override fun showInfo() {

        no_path.visibility=View.VISIBLE
        recycler.visibility=View.GONE

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

}
