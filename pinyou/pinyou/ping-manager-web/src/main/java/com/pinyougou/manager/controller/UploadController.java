package com.pinyougou.manager.controller;

import com.pinyougou.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    //注解注入服务器地址
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //获取文件的扩展名
        String filename = file.getOriginalFilename();
        String extName = filename.substring(filename.lastIndexOf("."));
        try {
            //创建客户端
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            //执行上传
            String path = client.uploadFile(file.getBytes(), extName);
            //将ip地址与返回的url拼接成完整的url路径
            String url = FILE_SERVER_URL+path;

            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
