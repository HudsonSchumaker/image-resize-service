# Image Resize Service

A modern Spring Boot application that provides both REST API and web interface for resizing images. Built with Spring Boot 3.0 and Java 17, supporting multiple image formats with high-quality scaling algorithms.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.0-blue)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.1.3-purple)

## 🌟 Features

### Core Functionality
- **Multiple Format Support**: JPG/JPEG, PNG, and BMP image formats
- **Format Conversion**: Convert between different image formats while resizing
- **Transparency Preservation**: Maintains PNG transparency and alpha channels
- **High-Quality Scaling**: Advanced algorithms with anti-aliasing and interpolation
- **Dual Interface**: Both REST API and user-friendly web interface

### Web Interface Features
- **Responsive Design**: Bootstrap 5 responsive UI that works on all devices
- **Live Preview**: Instant preview of resized images
- **Drag & Drop**: Easy file upload with validation
- **Download Support**: Direct download of resized images
- **Error Handling**: Clear error messages and validation
- **Format Selection**: Choose output format independently from input

### API Features
- **RESTful Endpoints**: Clean REST API for programmatic access
- **Flexible Sizing**: Specify custom width and height
- **Format Specification**: Optional output format parameter
- **Proper HTTP Headers**: Correct content types and disposition headers

## 🏗️ Project Structure

```
image-resize-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── br/
│       │       └── schumaker/
│       │           └── img/
│       │               ├── ImageServiceApp.java             # Main application class
│       │               ├── controller/
│       │               │   ├── ImageController.java         # REST API endpoints
│       │               │   └── WebController.java           # Web interface controller
│       │               └── service/
│       │                   └── ImageResizeService.java      # Core resize logic
│       └── resources/
│           ├── application.properties                       # Configuration
│           ├── static/
│           │   └── css/                                    # Custom styles
│           └── templates/
│               └── index.html                              # Thymeleaf template
├── pom.xml                                                 # Maven dependencies
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/image-resize-service.git
   cd image-resize-service
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - **Web Interface**: http://localhost:8080
   - **API Base URL**: http://localhost:8080/api/images

## 🖥️ Web Interface Usage

1. Open your browser and navigate to `http://localhost:8080`
2. Click "Choose File" and select an image (JPG, PNG, or BMP)
3. Set your desired width and height in pixels
4. Optionally select an output format (or keep original)
5. Click "Resize Image" to see the preview
6. Click "Download Resized Image" to save the result

## 🔧 REST API Usage

### Endpoints

#### 1. Resize Image (Keep Original Format)
```http
POST /api/images/resize
Content-Type: multipart/form-data

Parameters:
- file: Image file (required)
- width: Target width in pixels (required)
- height: Target height in pixels (required)
```

**Example with cURL:**
```bash
curl -X POST \
  http://localhost:8080/api/images/resize \
  -F "file=@/path/to/image.jpg" \
  -F "width=800" \
  -F "height=600" \
  --output resized_image.jpg
```

#### 2. Resize with Format Conversion
```http
POST /api/images/resize-with-format
Content-Type: multipart/form-data
```
Parameters:
```
- file: Image file (required)
- width: Target width in pixels (required)
- height: Target height in pixels (required)
- format: Output format - jpg, png, or bmp (required)
```

**Example with cURL:**
```bash
curl -X POST \
  http://localhost:8080/api/images/resize-with-format \
  -F "file=@/path/to/image.png" \
  -F "width=1024" \
  -F "height=768" \
  -F "format=jpg" \
  --output converted_image.jpg
```

### Response Format
- **Success**: Returns the resized image as binary data with appropriate `Content-Type` header
- **Error**: Returns HTTP error status with error message

## 🛠️ Configuration

### Application Properties
Located in `src/main/resources/application.properties`:

```properties
# Server configuration
server.port=8080

# File upload limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Thymeleaf configuration
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML
```

### Supported Image Formats

| Format | Input | Output | Transparency | Notes |
|--------|-------|--------|--------------|-------|
| JPEG/JPG | ✅ | ✅ | ❌ | Best for photos |
| PNG | ✅ | ✅ | ✅ | Supports transparency |
| BMP | ✅ | ✅ | ❌ | Uncompressed format |

## 🏗️ Dependencies

### Core Dependencies
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <!-- Image Processing -->
    <dependency>
        <groupId>org.imgscalr</groupId>
        <artifactId>imgscalr-lib</artifactId>
        <version>4.2</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 🎨 Technical Details

### Image Processing Features
- **Scaling Algorithm**: Uses `Image.SCALE_SMOOTH` for high-quality results
- **Rendering Hints**: Applies anti-aliasing and bilinear interpolation
- **Color Space**: Handles RGB and ARGB color spaces appropriately
- **Memory Efficient**: Processes images in memory without temporary files

### Security Considerations
- File type validation based on file extensions
- File size limits to prevent memory exhaustion
- Input validation for dimensions and parameters

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

Run with coverage:
```bash
mvn test jacoco:report
```

## 📝 Examples

### Batch Processing Script
```bash
#!/bin/bash
# Resize multiple images to thumbnail size
for file in *.jpg; do
    curl -X POST \
      http://localhost:8080/api/images/resize \
      -F "file=@$file" \
      -F "width=150" \
      -F "height=150" \
      --output "thumb_$file"
done
```

### JavaScript Integration
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('width', 800);
formData.append('height', 600);
formData.append('format', 'png');

fetch('/api/images/resize-with-format', {
    method: 'POST',
    body: formData
})
.then(response => response.blob())
.then(blob => {
    const url = URL.createObjectURL(blob);
    // Display or download the resized image
});
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [Issues](https://github.com/yourusername/image-resize-service/issues) page
2. Create a new issue with detailed information
3. Include sample images and error messages if applicable

## 🚀 Future Enhancements

- [ ] Support for additional formats (WebP, TIFF, GIF)
- [ ] Batch processing capabilities
- [ ] Image compression options
- [ ] Watermark functionality
- [ ] Cloud storage integration
- [ ] Rate limiting and authentication
- [ ] Image metadata preservation