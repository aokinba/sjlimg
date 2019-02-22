/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjl.sjlimg.server.service;

import com.sjl.sjlimg.server.entity.Folder;
import com.sjl.sjlimg.server.entity.FolderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Administrator
 */
@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    public List<Folder> findAll() {
        return folderRepository.findAll();
    }

    public void deleteById(Long id) {
        folderRepository.deleteById(id);
    }

    @Transactional
    public String save(Folder folder) {
        folderRepository.save(folder);
        if (folder.getName().equals("test")) {
            throw new RuntimeException("测试");
        }
        if (folder.getName().equals("test2")) {
            int d = 1 / 0;
        }
        return folder.getId().toString();
    }
}
