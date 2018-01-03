package com.liumapp.itext;

import com.liumapp.itext.utils.ItextPdfUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        ItextPdfUtil pdfUtil = new ItextPdfUtil();
        pdfUtil.addCAToPdf("C:/word/test0.pdf","C:/word/test00.pdf","C:/word/sign.png",
                    350f,0f,100f,150f);
        try {
            pdfUtil.pdf2Img("C:/word/test00.pdf","C:jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
