package net.javaguides.product_service.service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

public interface StorageService {
    void uploadFile(MultipartFile file, String fileName);
    String deleteFile(String fileName);
}
