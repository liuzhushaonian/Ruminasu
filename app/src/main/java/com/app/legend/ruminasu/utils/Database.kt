package com.app.legend.ruminasu.utils

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.legend.ruminasu.beans.Chapter
import com.app.legend.ruminasu.beans.Comic
import com.app.legend.ruminasu.beans.Path
import java.lang.Exception

/**
 * 数据库
 */
class Database(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {



    companion object{

        private const val DATABASE_NAME="ruminasu";
        private const val VERSION=1;


        //漫画表
        const val T_COMIC="CREATE TABLE IF NOT EXISTS t_comic (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "hide INTEGER default(-1)," +
                "path TEXT," +
                "book TEXT" +
                ")"

        //章节表
        const val T_CHAPTER="CREATE TABLE IF NOT EXISTS t_chapter (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "read INTEGER default(-1)," +
                "book TEXT," +
                "c_order INTEGER," +
                "comic_id INTEGER" +
                ")"

        //书签表
        const val T_BOOKMARK="CREATE TABLE IF NOT EXISTS t_bookmark (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "comic_id INTEGER," +
                "chapter_id INTEGER," +
                "page INTEGER" +
                ")"

        //历史表
        const val T_HISTORY="CREATE TABLE IF NOT EXISTS t_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "comic_id INTEGER UNIQUE," +
                "chapter_id INTEGER," +
                "page INTEGER" +
                ")"


        //路径表
        const val T_PATH="CREATE TABLE IF NOT EXISTS t_path (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "path TEXT NOT NULL UNIQUE" +
                ")"

        private var database: Database? = null

        fun getDefault(context: Context?):Database {

            if (database==null){

                database= Database(context,DATABASE_NAME,null, VERSION)

            }

            return database as Database

        }

    }

    private var sqLiteDatabase: SQLiteDatabase = readableDatabase


    override fun onCreate(db: SQLiteDatabase?) {

        if (db != null) {
            db.execSQL(T_COMIC)
            db.execSQL(T_CHAPTER)
            db.execSQL(T_BOOKMARK)
            db.execSQL(T_HISTORY)
            db.execSQL(T_PATH)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    /**
     * 获取用户添加的路径
     */
    fun getPaths():List<Path>{

        val sql="select * from t_path"
        val cursor:Cursor=sqLiteDatabase.rawQuery(sql,null)
        val paths:MutableList<Path> =ArrayList()
        if (cursor.moveToFirst()){
            do {
                val p= Path(cursor.getInt(cursor.getColumnIndex("id")),cursor.getString(cursor.getColumnIndex("path")))
                paths.add(p)
            }while (cursor.moveToNext())
            cursor.close()
        }
        return paths
    }

    /**
     * -----------路径---------------------
     */

    /**
     * 添加路径
     */
    fun addPaths(path:String):Int{
        val sql= "insert into t_path (path) values($path)"
        var result:Int=-1
        try {
            sqLiteDatabase.execSQL(sql)
            result=1;
        }catch (e:Exception){
            result=-1
        }
        return result;
    }

    /**
     * 删除路径
     */
    fun deletePath(p:Path):Int{

        val sql="delete from t_path where id = ${p.id}"
        var result=-1
        try {

            sqLiteDatabase.execSQL(sql)
            result=1

        }catch (e:Exception){

            result=-1

        }
        return result

    }


    /**
     * ------------------漫画表----------------------
     */


    /**
     * 添加漫画到表内
     * 插入前先查询该漫画是否已存在，检查title以及path，如果相同则不插入
     */
    fun addComic(comic: Comic){


        val search="select from t_comic where title=${comic.title} and path=${comic.path}";

        val cursor:Cursor=sqLiteDatabase.rawQuery(search,null);

        if (cursor.count>0){//表示已存在，且就是这本漫画

            cursor.close()

            return

        }

        val insert="insert into t_comic (title,hide,path,book) values (${comic.title},${comic.hide},${comic.path},${comic.book})"

        sqLiteDatabase.execSQL(insert)

    }

    /**
     * 更新漫画信息，隐藏或是封面替换
     */
    fun updateComic(comic: Comic):Int{

        val up="update t_comic set hide = ${comic.hide} and book = ${comic.book} where id = ${comic.id}"

        var result=-1

        try {
            sqLiteDatabase.execSQL(up)
            result=1
        }catch (e:Exception){
            result=-1
        }

        return result

    }

    /**
     * 删除漫画
     */
    fun deleteComic(comic: Comic):Int{

        val delete="delete from t_comic where id = ${comic.id}"
        var r=-1

        try {

            sqLiteDatabase.execSQL(delete)
            r=1
        }catch (e:Exception){
            r=-1
        }

        return r

    }

    /**
     * 查询全部
     */
    fun getAllComic():List<Comic>{

        val all:MutableList<Comic> =ArrayList()

        val select="select * from t_comic"

        val cursor:Cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.moveToFirst()){

            do {

                val comic= Comic(cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getInt(cursor.getColumnIndex("hide")),
                    cursor.getInt(cursor.getColumnIndex("id"))
                    )

                all.add(comic)


            }while (cursor.moveToNext())

        }

        cursor.close()

        return all

    }

    /**
     * 获取隐藏或是非隐藏的漫画，入参-1非隐藏，入参1为隐藏
     */
    fun getComics(hide:Int):List<Comic>{

        val all:MutableList<Comic> =ArrayList()
        val select="select * from t_comic where hide =$hide"
        val cursor:Cursor=sqLiteDatabase.rawQuery(select,null)
        if (cursor.moveToFirst()){
            do {
                val comic= Comic(cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getInt(cursor.getColumnIndex("hide")),
                    cursor.getInt(cursor.getColumnIndex("id"))
                )
                all.add(comic)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return all
    }

    /**
     * --------------------------章节表-----------------------------
     */


    /**
     * 添加章节
     * 检测是否已存在，根据name以及comic_id
     */
    fun addChapters(list: List<Chapter>):Int{

        var r=-1;

        for (c in list) {

            val add = "insert into t_chapter (read,book,c_order,type) values (${c.read},${c.book},${c.order},${c.type})"

            sqLiteDatabase.execSQL(add)

            r=1;
        }

        return r
    }


    fun updateChapter(chapter: Chapter):Int{

        var r=-1
        val update="update t_chapter set read =${chapter.read} , book = ${chapter.book},c_order=${chapter.order},type=${chapter.type} where id = ${chapter.id}"
        r = try {
            sqLiteDatabase.execSQL(update)
            1
        }catch (e:Exception){
            -1
        }
        return r
    }

    /**
     * 根据漫画查询该漫画的章节
     */
    fun getChapters(comic: Comic):List<Chapter>{

        val chapters:MutableList<Chapter> = ArrayList()

        val select="select * from t_chapter where comic_id = ${comic.id}"

        val cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.moveToFirst()){

            do {

                val chapter=Chapter(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("c_order")),
                    cursor.getString(cursor.getColumnIndex("type")),
                    cursor.getInt(cursor.getColumnIndex("read")),
                    cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getInt(cursor.getColumnIndex("id"))
                    )

                chapters.add(chapter)

            }while (cursor.moveToNext())


        }

        cursor.close()

        return chapters


    }


}