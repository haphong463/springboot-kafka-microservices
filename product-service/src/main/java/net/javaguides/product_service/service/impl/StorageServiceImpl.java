package net.javaguides.product_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import net.javaguides.product_service.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Value("${application.bucket.name}")
    private String bucket;

    @Autowired
    private AmazonS3 s3Client;

    @Async
    @Override
    public void uploadFile(MultipartFile file, String fileName){
        File fileObject = convertMultipartFileToFile(file, fileName);

        s3Client.putObject(new PutObjectRequest(bucket, fileName, fileObject));
        fileObject.delete();
        log.info(fileName + " uploaded ...");
    }

    @Override
    public String deleteFile(String fileName){
        s3Client.deleteObject(bucket, fileName);
        return fileName + " removed ...";
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile, String fileName){
        File convertedFile = new File(multipartFile.getOriginalFilename());
        try(FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(multipartFile.getBytes());
        }catch(IOException e){
            log.error("Error converting multipartFile to file: ", e);
        }
        return convertedFile;
    }
}
