package com.app.legend.ruminasu.utils;

import android.util.Log;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.util.List;

public class ZipUtils {

    public static File getBook(String path){

        if (path.endsWith(".zip")){//zip包

            try {
                ZipFile zipFile=new ZipFile(path);

                List list=zipFile.getFileHeaders();

                FileHeader header= (FileHeader) list.get(0);


            } catch (ZipException e) {
                e.printStackTrace();
            }

        }else {


        }

        return null;

    }


}
