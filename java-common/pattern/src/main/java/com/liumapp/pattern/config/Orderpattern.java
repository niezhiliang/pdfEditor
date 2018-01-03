package com.liumapp.pattern.config;

/**
 * Created by liumapp on 9/1/17.
 * E-mail:liumapp.com@gmail.com
 * home-page:http://www.liumapp.com
 * an order should be like : http://oss.aliyun.com/test_testfile_doc_5172912
 */
public class Orderpattern {

    /**
     * a:/usr/local/
     *
     * b:http://oss.aliyun.com/test/
     */
    private String savePath;

    /**
     * filename without suffix
     */
    private String fileName;

    /**
     * suffix without "."
     */
    private String type;

    /**
     * which unit is byte
     */
    private Long size;

    public static Orderpattern parse(String line) {
        Orderpattern orderpattern= new Orderpattern();

        String[] items = line.split("[\\s_]+");

        if (items.length < 4) {
            return null;
        }

        orderpattern.savePath = items[0];
        orderpattern.fileName = items[1];
        orderpattern.type = items[2];
        orderpattern.size = Long.parseLong(items[3]);

        return orderpattern;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
