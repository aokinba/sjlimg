package com.sjl.sjlimg.server.controller;

import com.sjl.sjlimg.server.utils.FastDFSClient;
import com.sjl.sjlimg.server.utils.FastDFSFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImgsController {

    private static Logger logger = LoggerFactory.getLogger(ImgsController.class);

    @RequestMapping(value = "/uploadPage", method = {RequestMethod.GET})
    public String test() {
        System.out.println("111111111");
        return "upload";
    }

    @PostMapping("/upload")
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
            ModelMap map) {
        if (file.isEmpty()) {
            map.addAttribute("message", "Please select a file to upload");
            return "uploadStatus";
        }

        try {
            // Get the file and save it somewhere
            String path = saveFile(file);
            map.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'  path = '" + path + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "uploadStatus";
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            logger.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            logger.error("upload file failed,please upload again!");
        }
        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }
}
