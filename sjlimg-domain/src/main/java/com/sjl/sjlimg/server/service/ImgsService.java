/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjlimg.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sjl.sjlimg.server.entity.Imgs;
import com.sjl.sjlimg.server.entity.ImgsRepository;
import com.sjl.sjlimg.server.utils.FastDFSClient;
import com.sjl.sjlimg.server.utils.FastDFSFile;
import com.sjl.sjlimg.server.vo.TokenVo;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Administrator
 */
@Service
public class ImgsService {

    private static Logger logger = LoggerFactory.getLogger(ImgsService.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ImgsRepository imgsRepository;

    public List<Imgs> findAll() {
        return imgsRepository.findAll();
    }

    public void deleteById(Long id) {
        imgsRepository.deleteById(id);
    }

    @Transactional
    public String save(Imgs imgs) {
        imgsRepository.save(imgs);
        return imgs.getId().toString();
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @Transactional
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

        Imgs imgs = new Imgs();
        imgs.setSrcPath(path);
        imgs.setFileName(fileName);
        String token = redisTemplate.opsForValue().get("token");
        String userName = userNameByToken(token);
        logger.debug(token);
        imgs.setUserId(userName);
        this.save(imgs);
        return path;
    }

    private String userNameByToken(String token) {
        String[] tokenSplit = token.split("\\.");
        byte[] base64decodedBytes = java.util.Base64.getDecoder().decode(tokenSplit[1]);
        try {
            String t = new String(base64decodedBytes, "utf-8");

            Gson gson2 = new GsonBuilder().enableComplexMapKeySerialization().create();
            TokenVo vo = gson2.fromJson(t, new TypeToken<TokenVo>() {
            }.getType());
            if (vo != null) {
                return vo.getUser_name();
            }
        } catch (UnsupportedEncodingException ex) {
            logger.debug(null, ex);
        }
        return null;
    }

}
