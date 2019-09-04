package com.app.legend.ruminasu.utils

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.app.legend.ruminasu.beans.*
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
                "book TEXT," +
                "is_zip INTEGER default(-1)" +//是否为zip,是1，否-1
                ")"

        //章节表
        const val T_CHAPTER="CREATE TABLE IF NOT EXISTS t_chapter (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "read INTEGER default(-1)," +
                "book TEXT," +
                "c_order INTEGER," +
                "comic_id INTEGER," +
                "order_name TEXT," +
                "c_type INTEGER" +
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

        //类型表
        const val T_TYPE="CREATE TABLE IF NOT EXISTS t_type (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "comic_id INTEGER," +
                "content TEXT" +
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
            db.execSQL(T_TYPE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    /**
     * 获取用户添加的路径
     */
    public fun getPaths():List<Path>{

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
    public fun addPaths(path:String):Int{
        val sql= "insert into t_path (path) values('$path')"

        Log.d("sql--->>",sql)

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
    public fun deletePath(p:Path):Int{

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
    public fun addComic(comic: Comic){


        val search="select * from t_comic where title='${comic.title}' and path='${comic.path}'";

        val cursor:Cursor=sqLiteDatabase.rawQuery(search,null);

        if (cursor.count>0){//表示已存在，且就是这本漫画

            cursor.close()

            return

        }

        val insert="insert into t_comic (title,hide,path,book,is_zip) values ('${comic.title}',${comic.hide},'${comic.path}','${comic.book}',${comic.zip})"

        sqLiteDatabase.execSQL(insert)

        val last="select last_insert_rowid() from t_comic"//获取最新插入的id

        val cursor1 = sqLiteDatabase.rawQuery(last, null)

        if (cursor1.moveToFirst()) {

            val id = cursor1.getInt(0)

            comic.id=id//设置id

            //                    Log.d("result----->>>",""+result);

        }

        cursor1.close()


    }

    /**
     * 更新漫画信息，隐藏或是封面替换
     */
    public fun updateComic(comic: Comic):Int{

        val up="update t_comic set hide = ${comic.hide},book = '${comic.book}' where id = ${comic.id}"

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
    public fun deleteComic(comic: Comic):Int{

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
    public fun getAllComic():List<Comic>{

        val all:MutableList<Comic> =ArrayList()

        val select="select * from t_comic"

        val cursor:Cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.moveToFirst()){

            do {

                val comic= Comic(cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getInt(cursor.getColumnIndex("hide")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("is_zip")),
                    true
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
    public fun getComics(hide:Int):List<Comic>{

        val all:MutableList<Comic> =ArrayList()
        val select="select * from t_comic where hide =$hide"
        val cursor:Cursor=sqLiteDatabase.rawQuery(select,null)
        if (cursor.moveToFirst()){
            do {
                val comic= Comic(cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getInt(cursor.getColumnIndex("hide")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("is_zip")),
                    true
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
     * 添加章节到数据库，先检测是否已存在，不存在就加入
     * 检测方法是查询该漫画的id以及章节名是否同时存在
     */
    public fun addChapter(comic: Comic,chapter: Chapter){

        val s="select * from t_chapter where comic_id = ${comic.id} and name = '${chapter.name}'"
        val cursor= sqLiteDatabase.rawQuery(s,null)

        if (cursor.count<=0){//不存在，保存

            val add = "insert into t_chapter (name,read,book,c_order,c_type,order_name,comic_id) values ('${chapter.name}',${chapter.read},'${chapter.book}',${chapter.order},${chapter.type},'${chapter.orderNmae}',${chapter.comicId})"

            sqLiteDatabase.execSQL(add)

            //获取id，设置上

            val last="select last_insert_rowid() from t_chapter"//获取最新插入的id

            val cursor1 = sqLiteDatabase.rawQuery(last, null)

            if (cursor1.moveToFirst()) {

                val id = cursor1.getInt(0)

                chapter.id=id//设置id
            }
            cursor1.close()
        }

        cursor.close()


    }



    public fun updateChapter(chapter: Chapter):Int{

        var r=-1
        val update="update t_chapter set read =${chapter.read} , book = '${chapter.book}',c_order=${chapter.order},c_type=${chapter.type},order_name='${chapter.orderNmae}' where id = ${chapter.id}"
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
    public fun getChapters(comic: Comic):List<Chapter>{

        val chapters:MutableList<Chapter> = ArrayList()

        val select="select * from t_chapter where comic_id = ${comic.id}"

        val cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.moveToFirst()){

            do {

                val chapter=Chapter(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("c_order")),
                    cursor.getInt(cursor.getColumnIndex("c_type")),
                    cursor.getInt(cursor.getColumnIndex("read")),
                    cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("order_name")),
                    cursor.getInt(cursor.getColumnIndex("comic_id"))
                    )

                chapters.add(chapter)

            }while (cursor.moveToNext())


        }

        cursor.close()

        return chapters
    }


    /**
     * 获取type章节，搜索数据库
     */
    public fun getChaptersByType(comic: Comic,type: Type):List<Chapter>{

        val chapters:MutableList<Chapter> = ArrayList()

        val select="select * from t_chapter where comic_id = ${comic.id} and c_type = ${type.id}"

        val cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.moveToFirst()){

            do {

                val chapter=Chapter(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("c_order")),
                    cursor.getInt(cursor.getColumnIndex("c_type")),
                    cursor.getInt(cursor.getColumnIndex("read")),
                    cursor.getString(cursor.getColumnIndex("book")),
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("order_name")),
                    cursor.getInt(cursor.getColumnIndex("comic_id"))
                )

                chapters.add(chapter)

            }while (cursor.moveToNext())


        }

        cursor.close()

        return chapters


    }

    /**
     * ----------------书签表------------------
     */


    /**
     * 添加书签
     */
    public fun addMark(comic: Comic,chapter: Chapter,page:Int):Int{

        var r=-1;

        val add="insert into t_bookmark (comic_id,chapter_id,page) values (${comic.id},${chapter.id},$page)"

        r=try {
            sqLiteDatabase.execSQL(add)
            1
        }catch (e:Exception){
            -1
        }

        return r

    }

    /**
     * 删除一个书签
     */
    public fun deleteMark(bookMark: BookMark):Int{

        val delete="delete from t_bookmark where id = ${bookMark.id}"

        val r=try {

            sqLiteDatabase.execSQL(delete)
            1
        }catch (e:Exception){

            -1
        }

        return r
    }


    /**
     * 根据漫画获取该漫画的书签
     */
    public fun getComicMark(comic: Comic):List<BookMark>{

        val marks:MutableList<BookMark> =ArrayList()

        val get="select * from t_bookmark where comic_id = ${comic.id}"

        val cursor:Cursor=sqLiteDatabase.rawQuery(get,null);

        if (cursor.moveToFirst()){

            do {

                val m= BookMark(cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("comic_id")),
                    cursor.getInt(cursor.getColumnIndex("chapter_id")),
                    cursor.getInt(cursor.getColumnIndex("page"))
                    )

                marks.add(m)
            }while (cursor.moveToNext())
        }

        cursor.close()
        return marks
    }

    /**
     * --------------------------------历史表------------------------------
     */

    /**
     * 添加阅读历史，先查询是否已存在该漫画，是就更新，不是就添加，在线程内执行
     */
    public fun addHistory(comic: Comic,chapter: Chapter,page: Int){
        val get="select comic_id from t_history where comic_id = ${comic.id} limit 1"
        val cursor:Cursor=sqLiteDatabase.rawQuery(get,null)
        if (cursor.count>0){//已存在，更新
            val update="update t_history set chapter_id = ${chapter.id},page = $page where comic_id = ${comic.id}"
            sqLiteDatabase.execSQL(update)
        }else{//不存在
            val add="insert into t_history (comic_id,chapter_id,page) values (${comic.id},${chapter.id},$page)"
            sqLiteDatabase.execSQL(add)
        }
        cursor.close()
    }

    /**
     * -----------------------------类型表------------------------------
     */

    /**
     * 添加类型，传入……？type对象吧……
     * 返回id? 这怎么返回？
     */
    public fun addTypes(type:Type){

        //查找同一漫画同样的type，如果存在就不保存
        val select="select * from t_type where comic_id = ${type.comicId} and content = '${type.content}'"

        val cursor=sqLiteDatabase.rawQuery(select,null)

        if (cursor.count<=0){//不存在

            val add ="insert into t_type (comic_id,content) values (${type.comicId},'${type.content}')"

            sqLiteDatabase.execSQL(add)

            val last="select last_insert_rowid() from t_comic"//获取最新插入的id

            val cursor1 = sqLiteDatabase.rawQuery(last, null)

            if (cursor1.moveToFirst()) {

                val id = cursor1.getInt(0)

                type.id=id//设置id
            }
            cursor1.close()
        }
        cursor.close()
    }


    /**
     * 获取漫画的type
     */
    public fun getTypeByComicId(comic: Comic):MutableList<Type>{

        val list = ArrayList<Type>()
        val get="select * from t_type where comic_id = ${comic.id}"
        val cursor=sqLiteDatabase.rawQuery(get,null)
        if (cursor.moveToFirst()){
            do {
                val id=cursor.getInt(cursor.getColumnIndex("id"))
                val comicId=cursor.getInt(cursor.getColumnIndex("comic_id"))
                val content=cursor.getString(cursor.getColumnIndex("content"))
                val type=Type(id,comicId,content)

                list.add(type)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return list

    }




}