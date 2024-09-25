package net.javaguides.product_service.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Async
    public void uploadFile(MultipartFile file, String publicId) {
        try {
            // Đọc ảnh từ MultipartFile thành BufferedImage
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            // Resize ảnh, ví dụ resize width về 800px (giữ nguyên tỷ lệ)
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, 800);

            // Chuyển ảnh resized thành byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            byte[] resizedBytes = outputStream.toByteArray();

            // Upload ảnh đã resize lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(resizedBytes, ObjectUtils.asMap(
                    "public_id", publicId,
                    "quality", "auto:good" // Sử dụng nén tự động với chất lượng tốt
            ));

            // Tạo URL mà không chứa version để tiện lợi
            String url = cloudinary.url().generate(uploadResult.get("public_id").toString());

            log.info("Uploaded file URL: {}", url);

        } catch (IOException e) {
            log.error("IO Exception during file upload", e);
        } catch (Exception e) {
            log.error("Unexpected exception during file upload", e);
        }
    }
}
