package com.example.demo.service;

import com.example.demo.modual.FileConfig;
import com.liumapp.common.oss.utils.OssUtil;
import com.liumapp.itext.utils.ItextPdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfOperation {

    public List<String> pdfToImg (String pdfPath){
        FileConfig fileConfig = new FileConfig();
        List<String> list = new ArrayList<String>();
        ItextPdfUtil itext = new ItextPdfUtil();
        String os = System.getProperty("os.name");
        String savaPath = null;
        if (os.startsWith("Windows")) {

            savaPath =  fileConfig.windows_imgPath;
        } else {

            savaPath = fileConfig.linux_imgPath;
        }


        try {

          list = itext.pdf2Img(pdfPath,savaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> upLoadImg(List<String> list) {
          List<String> list_oss = new ArrayList<String>();
           if (list.size() > 0) {
               for (String path: list) {
                   File file = new File(path);
                   String key = FileConfig.oss_key+"pdf_"+System.currentTimeMillis()+".jpg";
                   OssUtil oss = new OssUtil();
                   oss.uploadFile(key,file);
                   //background-image:url(${item}) Thyemleaf不支持样式的编写,直接把样式返回到界面
                   list_oss.add( FileConfig.oss_path+key);
               }
           }
           return list_oss;
    }

    public static void main(String[] args) {
//        File file = new File(new FileConfig().windows_imgPath);
//        if (!file.exists()) {
//            System.out.println("1111");
//            file.mkdir();
//        }
        
    }
}
