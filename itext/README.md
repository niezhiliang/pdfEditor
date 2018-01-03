# itext编辑pdf文档
* ItextPdfUtil.java
## 依赖jar
* itext-asian-5.2.0.jar
* itext-xtra-5.5.6.jar
* itext-pdfa-5.5.6.jar
* itextpdf-5.5.11.jar
* itext-asian-5.2.0.jar
* bcprov-jdk15on-1.49.jar
* bcpkix-jdk15on-1.49.jar
* pdf-renderer-1.0.5.jar
* pdfrenderer.jar  这个不指定版本
### 说明
* 这个类主要使用来对pdf格式的文档进行操作的,对pdf文件插入一个图片,还有个
就是将pdf文件按页转换成图片
### 方法
* addCAToPdf(String southPath,String DepositPath,String imgPaht,float XPort,float YPort,
                                     float CAWidth,float CAHeigth)
1. southPath  pdf文件地址
2. DepositPath 编辑后保存的地址
3. XPort   YPort  插入图片的x轴y轴位置
4. CAWidth  CAHeigth  图片的宽度和高度  
返回值 boolean 
                                       
* pdf2Img(String southPath,String saveDirPath)    
1. southPath   源文件地址
2. saveDirPath 要返回的文件夹   
返回值类型  List<String>  多张图片的路径集合                                
                                       

