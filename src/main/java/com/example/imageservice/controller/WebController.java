package com.example.imageservice.controller;

import com.example.imageservice.service.ImageResizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WebController {

    private final ImageResizeService imageResizeService;

    @Autowired
    public WebController(ImageResizeService imageResizeService) {
        this.imageResizeService = imageResizeService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/resize")
    public String resizeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("width") int width,
            @RequestParam("height") int height,
            @RequestParam(value = "format", required = false) String format,
            Model model,
            HttpSession session) {
        
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select an image file");
                return "index";
            }

            byte[] resizedImage;
            String outputFormat;
            
            if (format != null && !format.isEmpty()) {
                resizedImage = imageResizeService.resizeWithFormat(file, width, height, format);
                outputFormat = format;
            } else {
                resizedImage = imageResizeService.resize(file, width, height);
                outputFormat = getImageFormat(file.getOriginalFilename());
                if (outputFormat == null) outputFormat = "jpg";
            }

            // Store resized image data in session for download
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("data", resizedImage);
            imageData.put("format", outputFormat);
            imageData.put("filename", file.getOriginalFilename());
            imageData.put("width", width);
            imageData.put("height", height);
            session.setAttribute("resizedImageData", imageData);

            // Convert to base64 for display
            String base64Image = Base64.getEncoder().encodeToString(resizedImage);
            String dataUri = "data:image/" + outputFormat + ";base64," + base64Image;
            
            model.addAttribute("success", true);
            model.addAttribute("resizedImage", dataUri);
            model.addAttribute("originalFilename", file.getOriginalFilename());
            model.addAttribute("newWidth", width);
            model.addAttribute("newHeight", height);
            model.addAttribute("outputFormat", outputFormat.toUpperCase());
            
        } catch (IOException e) {
            model.addAttribute("error", "Error processing image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid format: " + e.getMessage());
        }
        
        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadImage(HttpSession session) {
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> imageData = (Map<String, Object>) session.getAttribute("resizedImageData");
            
            if (imageData == null) {
                return ResponseEntity.badRequest().build();
            }
            
            byte[] resizedImage = (byte[]) imageData.get("data");
            String outputFormat = (String) imageData.get("format");
            String originalFilename = (String) imageData.get("filename");
            int width = (int) imageData.get("width");
            int height = (int) imageData.get("height");
            
            // Create filename based on original name
            String baseFilename = originalFilename;
            if (baseFilename != null && baseFilename.contains(".")) {
                baseFilename = baseFilename.substring(0, baseFilename.lastIndexOf("."));
            } else {
                baseFilename = "image";
            }
            
            String filename = baseFilename + "_resized_" + width + "x" + height + "." + outputFormat;
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("image/" + outputFormat))
                    .body(resizedImage);
                    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private String getImageFormat(String filename) {
        if (filename == null) return null;
        
        String lowerCase = filename.toLowerCase();
        if (lowerCase.endsWith(".png")) return "png";
        if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) return "jpg";
        if (lowerCase.endsWith(".bmp")) return "bmp";
        return null;
    }
}
