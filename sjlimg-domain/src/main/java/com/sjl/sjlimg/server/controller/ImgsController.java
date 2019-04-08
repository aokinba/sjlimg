package com.sjl.sjlimg.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImgsController {

    @RequestMapping(value = "/uploadPage", method = {RequestMethod.GET})
    public String test() {
        System.out.println("111111111");
        return "test";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String queryCardDetail(@RequestParam("file") MultipartFile file) throws Exception {

        String rootPath = "C:\\Users\\Administrator\\Desktop\\images";
        String filePath = rootPath + "/";
        File dir = new File(filePath);
        if (!dir.isDirectory()) {
            dir.mkdir();
        }

        String fileOriginalName = file.getOriginalFilename();
        String newFileName = UUID.randomUUID() + fileOriginalName.substring(fileOriginalName.lastIndexOf("."));
        File writeFile = new File(filePath + newFileName);
        //文件写入磁盘
        file.transferTo(writeFile);
        //返回存储的相对路径+文件名称
//        return "" + year + month + "/" + newFileName;
        return "hello world!";
    }
}
