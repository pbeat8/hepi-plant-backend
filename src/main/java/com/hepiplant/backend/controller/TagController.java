package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.TagDto;
import com.hepiplant.backend.dto.UserDto;
import com.hepiplant.backend.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagDto> addTag(@RequestBody TagDto tagDto){
        return ResponseEntity.ok().body(tagService.add(tagDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TagDto>> getTags(){
        return ResponseEntity.ok().body(tagService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{name}")
    public ResponseEntity<TagDto> getTagByName(@PathVariable String name){
        return  ResponseEntity.ok().body(tagService.getByName(name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok().body(tagService.delete(id));
    }
}
