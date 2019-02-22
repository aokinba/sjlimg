package com.sjl.sjlimg.server.controller;

import com.sjl.sjlimg.server.entity.Folder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjl.sjlimg.server.service.FolderService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/folder")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @RequestMapping(method = RequestMethod.GET)
    public Mono<List<Folder>> getFolder() {
        return Mono.just(folderService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Mono<Long> delete(@PathVariable Long id) throws Exception {
        folderService.deleteById(id);
        return Mono.just(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Mono<String> save(@ModelAttribute Folder folder) throws Exception {
        return Mono.just(folderService.save(folder));
    }
}
