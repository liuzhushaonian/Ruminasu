package com.app.legend.ruminasu.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.app.legend.ruminasu.beans.Chapter;
import com.app.legend.ruminasu.beans.Comic;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipUtils {

    /**
     * 获取指定位置下的文件的第一页，也就是封面
     * @param path 路径
     * @return 返回该文件
     */
    public static String getFirstBook(String path,String comicName){

        if (path.endsWith(".zip")){//zip包

            try {
                ZipFile zipFile=new ZipFile(path);

                List<FileHeader> list=zipFile.getFileHeaders();

                for (FileHeader header:list){

                    int i=getNum(header.getFileName());

//                    System.out.println(i);

                    if (i==1){//获取第一话

                        String cache= Objects.requireNonNull(RuminasuApp.context.getExternalCacheDir()).getAbsolutePath()+"/"+comicName;
                        String pp= Objects.requireNonNull(RuminasuApp.context.getExternalFilesDir(null)).getAbsolutePath();

                        if (header.getFileName().endsWith(".zip")){//zip包，将其解压到缓存，之后再获取内部第一张图

//                            String cache= Objects.requireNonNull(RuminasuApp.context.getExternalCacheDir()).getAbsolutePath()+"/"+comicName;

                            zipFile.extractFile(header,cache);

                            String p=cache+"/"+header.getFileName();

                            ZipFile zip=new ZipFile(p);//获取章节zip，然后不解压的情况下获取里面第一张图

                            List<FileHeader> list1=zip.getFileHeaders();

                            if (list1.isEmpty()){
                                return null;
                            }

                            FileHeader h=list1.get(0);

                            InputStream inputStream=zip.getInputStream(h);

                            Bitmap b= BitmapFactory.decodeStream(inputStream);

                            String ppp=pp+"/comic_book/"+comicName+"/book";

                            File file=new File(ppp);

                            if (!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();//创建上层文件夹
                            }

                            OutputStream outputStream=new FileOutputStream(file);

                            b.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

//                            Log.d("cache--->>>",cache);

                            zip.getFile().delete();//删除解压出来的漫画章节

                            return file.getAbsolutePath();//好累~


                        }else if (header.isDirectory()){//文件夹

                            zipFile.extractFile(header,cache);//解压

                            String p=cache+"/"+header.getFileName();

                            File file=new File(p);

                            if (file.exists()&&file.isDirectory()&&file.listFiles().length>0){

                                File file1=file.listFiles()[0];//获取里面的第一张图

                                if (file1.getName().endsWith(".jpg")||file1.getName().endsWith(".png")){//是图片

                                    String ppp=pp+"/comic_book/"+comicName+"/book";
                                    File book=new File(ppp);

                                    if (!book.getParentFile().exists()){
                                        book.getParentFile().mkdirs();//创建上层文件夹
                                    }

                                    OutputStream outputStream=new FileOutputStream(book);

                                    Bitmap bitmap=BitmapFactory.decodeFile(file1.getAbsolutePath());

                                    bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                                    file.delete();

                                    return book.getAbsolutePath();

                                }



                            }

                        }else if (header.getFileName().endsWith(".jpg")||header.getFileName().endsWith(".png")){//是图片


                            InputStream inputStream=zipFile.getInputStream(header);

                            Bitmap b= BitmapFactory.decodeStream(inputStream);

                            String ppp=pp+"/comic_book/"+comicName+"/book";

                            File file=new File(ppp);

                            if (!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();//创建上层文件夹
                            }

                            OutputStream outputStream=new FileOutputStream(file);

                            b.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                            return file.getAbsolutePath();

                        }

                        break;
                    }

                }

//                orderByName(list);

//                FileHeader header= (FileHeader) list.get(0);
//
//                Log.d("header--->>",header.getFileName());

//                Log.d("heaser--->>>",header.);

//                if (header.getFileName().endsWith(".zip")){//是zip文件
//
//                    ZipFile one=new ZipFile()
//
//                }

//                InputStream inputStream=zipFile.getInputStream(header);
//
//                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);


            } catch (ZipException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }else {//判断是不是文件夹

            File c=new File(path);

            if (c.isDirectory()){//是文件夹

                File[] files=c.listFiles();

                for (File f:files){

                    int i=getNum(f.getName());

                    if (i==1){//可能是zip、文件夹或直接是图片

                        String cache= Objects.requireNonNull(RuminasuApp.context.getExternalCacheDir()).getAbsolutePath()+"/"+comicName;
                        String pp= Objects.requireNonNull(RuminasuApp.context.getExternalFilesDir(null)).getAbsolutePath();


                        if (f.getName().endsWith(".zip")){

                            String p=cache+"/"+f.getName();

                            ZipFile zip=new ZipFile(p);//获取章节zip，然后不解压的情况下获取里面第一张图

                            List<FileHeader> list1= null;
                            try {
                                list1 = zip.getFileHeaders();
                            } catch (ZipException e) {
                                e.printStackTrace();
                            }

                            if (list1.isEmpty()){
                                return null;
                            }

                            FileHeader h=list1.get(0);

                            InputStream inputStream= null;
                            try {
                                inputStream = zip.getInputStream(h);
                            } catch (ZipException e) {
                                e.printStackTrace();
                            }

                            Bitmap b= BitmapFactory.decodeStream(inputStream);

                            String ppp=pp+"/comic_book/"+comicName+"/book";

                            File file=new File(ppp);

                            if (!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();//创建上层文件夹
                            }

                            OutputStream outputStream= null;
                            try {
                                outputStream = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            b.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

//                            Log.d("cache--->>>",cache);

                            zip.getFile().delete();//删除解压出来的漫画章节

                            return file.getAbsolutePath();//好累~


                        }else if (f.isDirectory()){

                            String p=cache+"/"+f.getName();

                            File file=new File(p);

                            if (file.exists()&&file.isDirectory()&&file.listFiles().length>0){

                                File file1=file.listFiles()[0];//获取里面的第一张图

                                if (file1.getName().endsWith(".jpg")||file1.getName().endsWith(".png")){//是图片

                                    String ppp=pp+"/comic_book/"+comicName+"/book";
                                    File book=new File(ppp);

                                    if (!book.getParentFile().exists()){
                                        book.getParentFile().mkdirs();//创建上层文件夹
                                    }

                                    OutputStream outputStream= null;
                                    try {
                                        outputStream = new FileOutputStream(book);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }

                                    Bitmap bitmap=BitmapFactory.decodeFile(file1.getAbsolutePath());

                                    bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                                    file.delete();

                                    return book.getAbsolutePath();

                                }
                            }

                        } else if (f.getName().endsWith("jpg")||f.getName().endsWith("png")) {

                            InputStream inputStream= null;
                            try {
                                inputStream = new FileInputStream(f);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            Bitmap b= BitmapFactory.decodeStream(inputStream);

                            String ppp=pp+"/comic_book/"+comicName+"/book";

                            File file=new File(ppp);

                            if (!file.getParentFile().exists()){
                                file.getParentFile().mkdirs();//创建上层文件夹
                            }

                            OutputStream outputStream= null;
                            try {
                                outputStream = new FileOutputStream(file);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            b.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                            return file.getAbsolutePath();
                        }
                    }
                }
            }
        }

        return null;

    }

    private static void orderByName(List<FileHeader> list) {

        Collections.sort(list, (o1, o2) -> o1.getFileName().compareTo(o2.getFileName()));
        for (FileHeader file1 : list) {
            System.out.println(file1.getFileName());

        }
    }


    private static int getNum(String text){

        Pattern p= Pattern.compile("(\\d+)");

        Matcher m= p.matcher(text);

        if (m.find()){
//            System.out.println(text);
//            System.out.println(m.group(1));

            String s=m.group(1);

            return Integer.parseInt(s);
        }

        return -1;

    }


    /**
     * 章节图片存放在缓存空间/漫画名/章节名
     *
     *
     * @param comic 漫画对象
     * @param chapter 章节对象
     * @return 返回图片地址
     */
    public static String getChapterBook(Comic comic, Chapter chapter){

        String result="";

        //临时解压目录，过后要删除
        String cache= Objects.requireNonNull(RuminasuApp.context.getExternalCacheDir()).getAbsolutePath()+"/"+comic.getTitle();
        //图片保存目录，不删除
        String pp= Objects.requireNonNull(RuminasuApp.context.getExternalFilesDir(null)).getAbsolutePath();

        String name=chapter.getName().replace(".zip","");//章节名字作为图片名字

        String ppp=pp+"/comic_book/"+comic.getTitle()+"/"+name;


        if (comic.getZip()>0){//是zip文件

            //里面有三种情况，一种是直接图片，第二种是文件夹形式的章节，第三种是zip形式的章节

            ZipFile zipFile=new ZipFile(comic.getPath());

            List<FileHeader> headerList=null;

            try {
                headerList=zipFile.getFileHeaders();
            } catch (ZipException e) {
                e.printStackTrace();
            }

            if (headerList==null){
                return result;
            }

            //情况一，里面直接是图片文件

            for (FileHeader h:headerList){

                if (h.getFileName().endsWith(".jpg")||h.getFileName().endsWith(".png")){//确认里面直接是图片，直接获取第一张返回

                    InputStream inputStream=null;

                    try {
                        inputStream=zipFile.getInputStream(h);
                    } catch (ZipException e) {
                        e.printStackTrace();
                    }

                    Bitmap bitmap=BitmapFactory.decodeStream(inputStream);

                    //


                    File file=new File(ppp);

                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();//创建上层文件夹
                    }

                    OutputStream outputStream=null;

                    try {
                        outputStream=new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                    result=file.getAbsolutePath();

                    break;

                }

                break;

            }

            //情况二，里面是zip,直接获取吧，根据名字

            FileHeader header=null;

            try {
               header= zipFile.getFileHeader(chapter.getName());
            } catch (ZipException e) {
                e.printStackTrace();
            }

            if (header==null){
                return null;
            }

            if (header.isDirectory()){//是文件夹，解压到缓存目录，获取第一张图，删除解压文件

                try {
                    zipFile.extractFile(header,cache);
                } catch (ZipException e) {
                    e.printStackTrace();
                }

                String s=cache+"/"+header.getFileName();

                File folder=new File(s);

                File[] fs=folder.listFiles();

                if (fs!=null&&fs.length>0){

                    File p=fs[0];

                    if (p.getName().endsWith(".jpg")||p.getName().endsWith(".png")) {

                        Bitmap bitmap = BitmapFactory.decodeFile(fs[0].getAbsolutePath());
                        try {

                            File f = new File(ppp);

                            OutputStream outputStream = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);

                            result=f.getAbsolutePath();

                            folder.delete();//之后删除解压出来的文件

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }else if (header.getFileName().endsWith(".zip")){//zip文件，先解压，然后以流方式获取第一张图，注意后缀

                try {
                    zipFile.extractFile(header,cache);
                } catch (ZipException e) {
                    e.printStackTrace();
                }

                String p=cache+"/"+header.getFileName();//位置

                ZipFile c=new ZipFile(p);

                try {
                    List<FileHeader> headers=c.getFileHeaders();

                    if (headers!=null&&headers.size()>0){

                        FileHeader h=headers.get(0);
                        if (h.getFileName().endsWith(".jpg")||h.getFileName().endsWith(".png")){

                            InputStream inputStream=c.getInputStream(h);

                            Bitmap bitmap=BitmapFactory.decodeStream(inputStream);

                            File s=new File(ppp);

                            OutputStream outputStream=new FileOutputStream(s);

                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                            result=s.getAbsolutePath();

                            c.getFile().delete();

                        }
                    }



                } catch (ZipException | FileNotFoundException e) {
                    e.printStackTrace();
                }


            }




        }else {

            File c=new File(comic.getPath());//获取文件

            if (c.isDirectory()){

                File[] fileList=c.listFiles();
                if (fileList!=null&&fileList.length>0){//先检测内部是不是直接是图片

                    File first=fileList[0];

                    if (first.getName().endsWith(".jpg")||first.getName().endsWith(".png")){

                        Bitmap bitmap=BitmapFactory.decodeFile(first.getAbsolutePath());

                        File s=new File(ppp);

                        try {
                            OutputStream outputStream=new FileOutputStream(s);

                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                            result=s.getAbsolutePath();

                            return result;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                    }

                    //直接根据chapter的名字new File吧，废话少说

                    File cc=new File(c,chapter.getName());

                    if (cc.exists()){//文件存在

                        if (cc.isDirectory()){

                            File[] ccs=cc.listFiles();

                            if (ccs!=null&&ccs.length>0){

                                File l=ccs[0];

                                if (l.getName().endsWith(".jpg")||l.getName().endsWith(".png")){

                                    Bitmap bitmap=BitmapFactory.decodeFile(l.getAbsolutePath());

                                    File bs=new File(ppp);

                                    try {
                                        OutputStream outputStream=new FileOutputStream(bs);

                                        bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);

                                        result=bs.getAbsolutePath();

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }else if (cc.getName().endsWith(".zip")){//章节是zip

                            ZipFile z=new ZipFile(cc);

                            try {
                                List<FileHeader> headers=z.getFileHeaders();
                                if (headers.size()>0){

                                    FileHeader h=headers.get(0);//获取第一张，并查看是不是图片

                                    if (h.getFileName().endsWith(".jpg")||h.getFileName().endsWith(".png")){

                                        InputStream inputStream=z.getInputStream(h);
                                        Bitmap bitmap=BitmapFactory.decodeStream(inputStream);

                                        File b=new File(ppp);

                                        OutputStream outputStream=new FileOutputStream(b);

                                        bitmap.compress(Bitmap.CompressFormat.WEBP,100,outputStream);
                                        result=b.getAbsolutePath();
                                    }
                                }
                            } catch (ZipException | FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
