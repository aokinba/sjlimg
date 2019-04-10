/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjlimg.server.service;

import com.sjl.sjlimg.server.entity.Imgs;
import com.sjl.sjlimg.server.entity.ImgsRepository;
import com.sjl.sjlimg.server.utils.FastDFSClient;
import com.sjl.sjlimg.server.utils.FastDFSFile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.Resource;
import org.apache.tomcat.util.codec.binary.Base64;
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
        try {
            Claims claims =  parseJWT(token, "springcloud123");
            System.out.println("11111111");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        System.out.println(token);
        imgs.setUserId(token);
        this.save(imgs);

        return path;
    }

    public static Claims parseJWT(String jwt, String secretKey) throws Exception {
        byte[] bytes = Base64.encodeBase64(secretKey.getBytes("utf-8"));
        Claims claims = Jwts.parser().setSigningKey(bytes).parseClaimsJws(jwt).getBody();
        return claims;
    }

}
