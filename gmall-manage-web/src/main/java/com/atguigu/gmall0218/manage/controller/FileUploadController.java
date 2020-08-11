package com.atguigu.gmall0218.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 任青成
 * @date 2020/8/11 20:42
 */
@RestController
@CrossOrigin
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileServerUrl;

    @RequestMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws IOException, MyException {

        String imgUrl = fileServerUrl;

        if (file != null) {
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);

            TrackerClient trackerClient = new TrackerClient();
            // 获取连接
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer, null);
            //获取上传文件的名称
            String orginalFilename = file.getOriginalFilename();
            //获取文件的后缀名
            String extName = StringUtils.substringAfterLast(orginalFilename, ".");
            //上传图片
            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);

            for (int i = 0; i < upload_file.length; i++) {
                String s = upload_file[i];
                imgUrl += "/" + s;
            }
        }
        //return "http://192.168.67.219/group1/M00/00/00/wKhD2106tuSAY9S9AACGx2c4tJ4084.jpg";
        return imgUrl;
    }

}
