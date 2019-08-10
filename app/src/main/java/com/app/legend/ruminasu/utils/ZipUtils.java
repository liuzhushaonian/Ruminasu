package com.app.legend.ruminasu.utils;


import android.util.Log;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import java.io.*;
import java.util.List;

public class ZipUtils {

    /**
     * 获取指定位置下的文件的第一页，也就是封面
     * @param path 路径
     * @return 返回该文件
     */
    public static File getFirstBook(String path){

        if (path.endsWith(".zip")){//zip包

            try {
                ZipFile zipFile=new ZipFile(path);

                List list=zipFile.getFileHeaders();

                FileHeader header= (FileHeader) list.get(0);

                Log.d("header--->>",header.getFileName());

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
            }

        }else {//判断是不是文件夹


        }

        return null;

    }


}
