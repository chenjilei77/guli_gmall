package com.chen.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PmsUploadUtil {

    public static String uploadImage(MultipartFile file) {
        String url ="";

        // 配置fdfs的全局链接地址
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        try {
            byte[] bytes = file.getBytes();//得到上传文件的二进制字节对象

            String originalFilename = file.getOriginalFilename();

            String extName =  originalFilename.substring(originalFilename.indexOf(".")+1);//获取后缀名

            String[] uploadInfos = storageClient.upload_file(bytes, extName, null);

            url = "http://192.168.134.129";

            for (String uploadInfo : uploadInfos) {
                url += "/"+uploadInfo;

                //url = url + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }
}
