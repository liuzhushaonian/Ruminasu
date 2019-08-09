package com.app.legend.ruminasu.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
                "book TEXT" +
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







}