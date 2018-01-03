package com.huluwa.cc.testapi.controller;

import com.example.demo.service.PdfOperation;
import com.huluwa.cc.testapi.module.AjaxResult;
import com.huluwa.cc.testapi.module.DataJson;
import com.huluwa.cc.testapi.module.ResultMsg;
import com.huluwa.cc.testapi.module.UploadJson;
import com.huluwa.cc.testapi.scoketCli.SocketClient;
import com.huluwa.cc.testapi.utils.UrlUtil;
import com.liumapp.common.oss.config.Configure;
import com.liumapp.common.oss.utils.OssUtil;

import javassist.runtime.Desc;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

	/*
	 * 文件上传到oss的路径
	 */
	private final String OSS_PATH = "office_";
	private final String PDF_PATH = "pdf/";
	/**
	 * 本地测试临时文件存放地址
	 */
	private final String LOCAL_PATH = "C:/office/";
	/**
	 * 服务器临时存放文件地址
	 */
	private final String LINUX_PATH = "/root/office2pdf/test-api/temp";
    @Autowired
    private SocketClient client;

    @GetMapping("/")
    public String index () {
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public UploadJson upload (@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            // 获取文件名加时间戳防止文件重复
            String fileName = file.getOriginalFilename();
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //oss上该文件的名称
            String ossFileName = "contract"+System.currentTimeMillis()+suffixName;
            // 解决中文问题，liunx下中文路径，图片显示问题
            // fileName = UUID.randomUUID() + suffixName;
            String os = System.getProperty("os.name");
            File dest = null;
            if (os.startsWith("Windows")) {

                dest = new File(LOCAL_PATH + ossFileName);
            } else {

                dest = new File(LINUX_PATH+ossFileName);
            }
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
                OssUtil oss = new OssUtil();
                if (suffixName.toLowerCase().equals(".pdf")) {

                    oss.uploadFile(PDF_PATH+ossFileName, dest);
                } else {

                    oss.uploadFile(OSS_PATH.replace("_","/")+ossFileName, dest);

                }
                if (dest.exists()) {//删除临时文件
                    dest.delete();
                }

            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String returnjson = client.office2Pdf(OSS_PATH+ossFileName.replace(".","_")+"_"+file.getSize());
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(returnjson);

                if (returnjson!=null) {
                    System.out.println(jsonObj.getString("pdfPath"));
                    DataJson json = new DataJson();
                    json.setSrc(jsonObj.getString("pdfPath"));
                    json.setImgPath(jsonObj.getString("pdf1Path"));
                    json.setTitle(fileName.substring(0,fileName.lastIndexOf(".")));
                    UploadJson ujson = new UploadJson();
                    ujson.setCode(0);
                    ujson.setData(json);
                    ujson.setMsg("文件上传成功");
                    return  ujson;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }
    @PostMapping("/index2")
    public String page (ModelMap model, String pdf_path,String pdfname) {
        OssUtil oss = new OssUtil();
        Configure configure = new Configure();
        String ossUrl = configure.getAccessBaseUrl();
        String key = pdf_path.replace(ossUrl+"/","");
        String fileName = pdf_path.replace(ossUrl+"/pdf/","");
        String os = System.getProperty("os.name");
        String temp =null;
        if (os.startsWith("Windows")) {
             temp = LOCAL_PATH;
        } else {
            temp = LINUX_PATH;
        }
        oss.downloadFile(key,new File(temp+fileName));
        PdfOperation operation = new PdfOperation();
        List<String> list = operation.pdfToImg(LOCAL_PATH+fileName);
        list =  operation.upLoadImg(list);
        model.addAttribute("imglist",list);
        model.addAttribute("imgsize",list.size());
        model.addAttribute("pdfname",pdfname);
         return "/page";
    }

}
