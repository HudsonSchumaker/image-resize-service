package br.schumaker.img.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

@Service
public class ImageResizeService {

    public byte[] resize(MultipartFile file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        // Determine the output format based on the original file
        String originalFormat = getImageFormat(file.getOriginalFilename());
        String outputFormat = originalFormat != null ? originalFormat : "jpg";
        
        // Create the appropriate BufferedImage type based on format
        int imageType = getImageType(outputFormat);
        
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, imageType);
        
        // Handle transparency for PNG
        if ("png".equalsIgnoreCase(outputFormat)) {
            Graphics2D g2d = outputImage.createGraphics();
            g2d.setComposite(AlphaComposite.Src);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(resultingImage, 0, 0, null);
            g2d.dispose();
        } else {
            Graphics2D g2d = outputImage.createGraphics();
            g2d.drawImage(resultingImage, 0, 0, null);
            g2d.dispose();
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, outputFormat, baos);
        return baos.toByteArray();
    }
    
    public byte[] resizeWithFormat(MultipartFile file, int width, int height, String outputFormat) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        // Validate output format
        if (!isValidFormat(outputFormat)) {
            throw new IllegalArgumentException("Unsupported format: " + outputFormat);
        }
        
        int imageType = getImageType(outputFormat);
        
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, imageType);
        
        Graphics2D g2d = outputImage.createGraphics();
        if ("png".equalsIgnoreCase(outputFormat)) {
            g2d.setComposite(AlphaComposite.Src);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, outputFormat, baos);
        return baos.toByteArray();
    }
    
    private String getImageFormat(String filename) {
        if (filename == null) return null;
        
        String lowerCase = filename.toLowerCase();
        if (lowerCase.endsWith(".png")) return "png";
        if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg")) return "jpg";
        if (lowerCase.endsWith(".bmp")) return "bmp";
        return null;
    }
    
    private int getImageType(String format) {
        switch (format.toLowerCase()) {
            case "png":
                return BufferedImage.TYPE_INT_ARGB;
            case "bmp":
            case "jpg":
            case "jpeg":
            default:
                return BufferedImage.TYPE_INT_RGB;
        }
    }
    
    private boolean isValidFormat(String format) {
        return format != null && 
               ("png".equalsIgnoreCase(format) || 
                "jpg".equalsIgnoreCase(format) || 
                "jpeg".equalsIgnoreCase(format) || 
                "bmp".equalsIgnoreCase(format));
    }
}
