package com.hnusteasyweibo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            response.put("success", false);
            response.put("message", "只支持图片文件");
            return ResponseEntity.badRequest().body(response);
        }

        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            response.put("success", false);
            response.put("message", "文件大小不能超过10MB");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 创建上传目录
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String newFilename = UUID.randomUUID().toString() + extension;
            String filePath = uploadDir + File.separator + newFilename;

            // 保存文件
            file.transferTo(new File(filePath));

            // 返回文件URL
            String fileUrl = "/uploads/" + newFilename;
            response.put("success", true);
            response.put("url", fileUrl);
            response.put("message", "上传成功");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "文件上传失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}