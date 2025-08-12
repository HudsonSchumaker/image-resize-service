package com.example.imageservice.controller;

import com.example.imageservice.service.ImageResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageResizeService imageResizeService;

    @Autowired
    public ImageController(ImageResizeService imageResizeService) {
        this.imageResizeService = imageResizeService;
    }

    @PostMapping("/resize")
    public ResponseEntity<byte[]> resizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height) {
        try {
            byte[] resizedImage = imageResizeService.resize(file, width, height);
            
            // Determine content type based on original file
            String contentType = "image/jpeg"; // default
            String filename = file.getOriginalFilename();
            if (filename != null) {
                if (filename.toLowerCase().endsWith(".png")) {
                    contentType = "image/png";
                } else if (filename.toLowerCase().endsWith(".bmp")) {
                    contentType = "image/bmp";
                }
            }
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(resizedImage);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/resize-with-format")
    public ResponseEntity<byte[]> resizeImageWithFormat(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam("format") String format) {
        try {
            byte[] resizedImage = imageResizeService.resizeWithFormat(file, width, height, format);
            
            String contentType = "image/" + format.toLowerCase();
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(resizedImage);
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}