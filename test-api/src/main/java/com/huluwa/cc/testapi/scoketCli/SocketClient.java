
package com.huluwa.cc.testapi.scoketCli;

import com.huluwa.cc.testapi.module.ResultMsg;
import com.liumapp.DNSQueen.queen.Queen;
import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Created by liumapp on 9/1/17.
 * E-mail:liumapp.com@gmail.com
 * home-page:http: www.liumapp.com
 */
@Component
 public class SocketClient extends TestCase {

     private Queen queen;


    /**
     *调用远程API将文档转换成pdf,并返回文件路径
     * @param filePath 文件路径  格式如下:
     *                 C:/word_test0_docx_52012
     * @return
     */
     public String office2Pdf (String filePath) {
         try {
             queen = new Queen();//118.190.136.193
             queen.setAddress("118.190.136.193");
             queen.connect();
             queen.say(filePath);
             String json = queen.hear();
             JSONObject jsonObj;
//             ResultMsg rsg = new ResultMsg();
//			try {
//				rsg.setIsSuccess(jsonObj.getString("isSuccess"));
//				rsg.setPdfPath(jsonObj.getString("pdfPath"));
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//             if (rsg.getIsSuccess().equals("success")) {
//                 return rsg.getPdfPath();
//             }
             try {
            	 jsonObj = new JSONObject(json);
				if (jsonObj.getString("isSuccess").equals("success")) {
					 return json;
				 }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         } catch (IOException e) {
             e.printStackTrace();
         }
         return null;
     }

 }

