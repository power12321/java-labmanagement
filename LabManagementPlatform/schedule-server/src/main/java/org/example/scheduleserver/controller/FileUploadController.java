package org.example.scheduleserver.controller;

import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.profile}")
    private String uploadDir;

    /**
     * 上传文件
     * @param files 文件数组
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    file.transferTo(filePath.toFile());
                    String fileUrl = "http://localhost:8032" + "/uploads/" + fileName;
                    fileUrls.add(fileUrl);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtils.success(fileUrls);
    }

    /**
     * 下载文件
     * @param fileName 文件名
     * @return
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            org.springframework.core.io.Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentLength(resource.contentLength())
                        .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }
}