package com.liumapp.pdfbox.utils;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */
public class pdfbox {
/*public static void main(String[] args){
		String inputFile="D:/test2.pdf";
		String image="D:/310006.jpg";
		//String image2="D:/308906_300.jpg";
		String outputFile="D:/test.pdf";
		try {
			createPDFFromImage(inputFile,image,outputFile);

		} catch (COSVisitorException e) {

			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("error");
			e.printStackTrace();
		}

	}*/
	/*public static void createPDFFromImage( String inputFile, String image, String outputFile)
	         throws IOException, COSVisitorException
	     {
	         // the document
	         PDDocument doc = null;
	         try
	         {
	             doc = PDDocument.load(inputFile);

	             //我们将图片放在第一页,当然我们也可以指定坐标,下面例子就是指定坐标
				 doc.getDocumentCatalog();
	             PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get(0);
	             PDXObjectImage ximage = null;
	             if( image.toLowerCase().endsWith(".jpg" ));
	             {
	            	FileInputStream is = new FileInputStream(image);
	                 ximage = new PDJpeg(doc,is);
	                 is.close();
	             }
	             *//*else if (image.toLowerCase().endsWith(".tif") || image.toLowerCase().endsWith(".tiff"))
	             {
	            	 new RandomAccessFile(new File(image));
	                 ximage = new PDCcitt;
	             }*//*


					 BufferedImage awtImage = ImageIO.read( new File( image ));
					 ximage = new PDPixelMap(doc, awtImage);
	             PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);

	             //contentStream.drawImage(ximage, 20, 20 );
	             // 更好的方法: http://stackoverflow.com/a/22318681/535646
	            float scale = 0.5f; //如果图像太大,请减小此值
	             System.out.println(ximage.getHeight());
	             System.out.println(ximage.getWidth());

	            contentStream.drawXObject(ximage, 50, 200, 50, 60);
	             contentStream.close();
	             doc.save( outputFile );

	         }
	         finally
	         {
	             if( doc != null )
	             {
	                 doc.close();
	             }
	         }
	     }*/

    /*public static void main(String[] args) throws IOException {
        PDDocument doc = PDDocument.load("D:/test2.pdf");
        int pageCount = doc.getNumberOfPages();
        System.out.println(pageCount);
        List pages = doc.getDocumentCatalog().getAllPages();
        for(int i=0;i<pages.size();i++){
            PDPage page = (PDPage)pages.get(i);
            BufferedImage image = page.convertToImage();
            Iterator iter = ImageIO.getImageWritersBySuffix("jpg");
            ImageWriter writer = (ImageWriter)iter.next();
            File outFile = new File("D:/"+i+".jpg");
            FileOutputStream out = new FileOutputStream(outFile);
            ImageOutputStream outImage = ImageIO.createImageOutputStream(out);
            writer.setOutput(outImage);
            writer.write(new IIOImage(image,null,null));
        }
        doc.close();
        System.out.println("over");
    }*/
    public static void main(String[] args) {
        String pdfFile = "D:/test0.pdf";
        //String image = "D:/310006.jpg";
        //String image2="D:/308906_300.jpg";
        //String outputFile = "D:/test.pdf";
        List<String> imgList=new ArrayList<String>();
        imgList.add("D:/310006.jpg");
        imgList.add("D:/308906_300.jpg");

        try {
            createPDFFromImage(pdfFile,imgList,200,50,0.2f);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
    }

    public static void createPDFFromImage(String pdfFile, List<String> imgList,int x, int y, float scale) throws IOException, COSVisitorException {
        // the document
        PDDocument doc = null;
        try {
            doc = new PDDocument();
            doc = PDDocument.load(pdfFile);
            Iterator iter = imgList.iterator();
            int imgIndex=0;
            while(iter.hasNext()) {
                //PDPage page = new PDPage();
                //doc.addPage(page);
                PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get(1);
                BufferedImage tmp_image = ImageIO.read(new File(iter.next().toString()));
                BufferedImage image = new BufferedImage(tmp_image.getWidth(), tmp_image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                image.createGraphics().drawRenderedImage(tmp_image, null);

                PDXObjectImage ximage = new PDPixelMap(doc, image);

                imgIndex++;


                PDPageContentStream contentStream = new PDPageContentStream(
                        doc, page,true,true);

                contentStream.drawXObject(ximage, x, y, ximage.getWidth()*scale, ximage.getHeight()*scale);

                contentStream.close();
            }
            doc.save(pdfFile);
        } finally {
            if (doc != null) {

                doc.close();
            }
        }
    }
}
