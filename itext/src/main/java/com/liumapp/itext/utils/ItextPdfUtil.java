package com.liumapp.itext.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import org.springframework.stereotype.Component;
@Component
public class ItextPdfUtil {
    /**
     * 给pdf文档加电子签名(暂时只能加图片)
     * @param southPath  pdf源文件地址
     * @param DepositPath 签章后的保存地址
     * @param imgPaht   要插入的签章图片
     * @param XPort  签章图片的起始X坐标
     * @param YPort  签章图片的起始Y坐标
     * @param CAWidth  签名图片的宽度
     * @param CAHeigth 签名图片的长度
     * @return
     * @throws IOException
     * @throws DocumentException
     */
     public   boolean addCAToPdf(String southPath,String DepositPath,String imgPaht,float XPort,float YPort,
                                       float CAWidth,float CAHeigth) {
         PdfStamper stamper = null;
         try {
             //创建一个pdf读入流
             PdfReader reader = new PdfReader(southPath);
             //根据一个pdfreader创建一个pdfStamper.用来生成新的pdf.
             stamper = new PdfStamper(reader,
                     new FileOutputStream(DepositPath));

             //这个字体是itext-asian.jar中自带的 所以不用考虑操作系统环境问题.
             BaseFont bf = BaseFont.createFont("STSong-Light",
                     "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED); // set font
             //baseFont不支持字体样式设定.但是font字体要求操作系统支持此字体会带来移植问题.
             Font font = new Font(bf, 10);
             font.setStyle(Font.BOLD);
             font.getBaseFont();
             //页数是从1开始的
             for (int i = 1; i <= 1; i++) {
                 //获得pdfstamper在当前页的上层打印内容.也就是说 这些内容会覆盖在原先的pdf内容之上.
                 PdfContentByte over = stamper.getOverContent(i);
                 //用pdfreader获得当前页字典对象.包含了该页的一些数据.比如该页的坐标轴信息.
                 PdfDictionary p = reader.getPageN(i);
                 //拿到mediaBox 里面放着该页pdf的大小信息.
                 PdfObject po = p.get(new PdfName("MediaBox"));
                 System.out.println(po.isArray());
                 //po是一个数组对象.里面包含了该页pdf的坐标轴范围.
                 PdfArray pa = (PdfArray) po;
                 System.out.println(pa.size());
                 //看看y轴的最大值.
                 System.out.println(pa.getAsNumber(pa.size() - 1));
                 Image image = Image.getInstance(imgPaht);
                 //设置image对象的输出位置pa.getAsNumber(pa.size()-1).floatValue() 是该页pdf坐标轴的y轴的最大值
                 image.setAbsolutePosition(XPort, YPort);//0, 0, 841.92, 595.32
                 image.scaleToFit(CAWidth, CAHeigth);
                 over.addImage(image);
             }
             return true;
         }
         catch (IOException e) {
                 e.printStackTrace();
             } catch (DocumentException e) {

             e.printStackTrace();
         } finally {
             try {
                 stamper.close();
             } catch (DocumentException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return false;
     }

     public  List<String> pdf2Img(String southPath,String saveDirPath) throws IOException {
         List<String> pathList = new ArrayList<String>();
         File dir = new File(saveDirPath);
         if (!dir.exists()) {
             dir.mkdir();
         }
         File file = new File(southPath);
         if (!file.exists()) {
             return null;
         }
         RandomAccessFile raf = new RandomAccessFile(file, "r");
         FileChannel channel = raf.getChannel();
         ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel
                 .size());
         PDFFile pdffile = new PDFFile(buf);

         //System.out.println("页数： " + pdffile.getNumPages());

         String getPdfFilePath = saveDirPath;//System.getProperty("user.dir") + "\\pdfPicFile";

         //System.out.println("getPdfFilePath is  :" + getPdfFilePath);

         for (int i = 1; i <= pdffile.getNumPages(); i++) {
             // draw the first page to an image
             PDFPage page = pdffile.getPage(i);

             // get the width and height for the doc at the default zoom
             Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
                     .getWidth(), (int) page.getBBox().getHeight());

             // generate the image
             java.awt.Image img = page.getImage(rect.width, rect.height, // width &
                     // height
                     rect, // clip rect
                     null, // null for the ImageObserver
                     true, // fill background with white
                     true // block until drawing is done
             );

             BufferedImage tag = new BufferedImage(rect.width, rect.height,
                     BufferedImage.TYPE_INT_RGB);
             tag.getGraphics().drawImage(img, 0, 0, rect.width, rect.height,
                     null);

             // 输出到文件流
             FileOutputStream out = new FileOutputStream(getPdfFilePath + "\\"
                     + i + ".jpg");

             pathList.add( getPdfFilePath + "\\" + i + ".jpg");
             System.out.println("成功保存图片到:" + getPdfFilePath + "\\" + i + ".jpg");

            /*
             * JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
             * encoder.encode(tag); // JPEG编码 out.close();
             */
             JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
             JPEGEncodeParam param2 = encoder.getDefaultJPEGEncodeParam(tag);
             param2.setQuality(1f, true);// 1f是提高生成的图片质量
             encoder.setJPEGEncodeParam(param2);
             encoder.encode(tag); // JPEG编码
             out.close();

         }
         return pathList;

     }

    public static void main(String[] args) {
          /* new ItextPdfUtil().addCAToPdf("C:/word/test0.pdf","C:/word/test00.pdf","C:/word/sign.png",
                    350f,0f,100f,150f);*/
        try {
           new ItextPdfUtil().pdf2Img("C:/office/contract1505898941975.pdf","C:jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
