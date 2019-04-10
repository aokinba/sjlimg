package com.sjl.sjlimg.server.controller;

import com.sjl.sjlimg.server.service.ImgsService;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImgsController {

    private static Logger logger = LoggerFactory.getLogger(ImgsController.class);

    @Autowired
    private ImgsService imgsService;

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
            String path = imgsService.saveFile(file);
            map.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'  path = '" + path + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "uploadStatus";
    }

}
