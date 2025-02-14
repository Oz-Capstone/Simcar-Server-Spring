package com.hsu.simcar.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${file.upload.path}")
    private String uploadPath;
    
    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + 
            extractExtension(originalFilename);
        
        String absolutePath = new File(uploadPath).getAbsolutePath();
        
        // 업로드 경로가 없으면 생성
        File directory = new File(absolutePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // 파일 저장
        String filePath = absolutePath + File.separator + storedFileName;
        file.transferTo(new File(filePath));
        
        return storedFileName;
    }

    private String extractExtension(String filename) {
        int dot = filename.lastIndexOf(".");
        if (dot == -1) {
            return "";
        }
        return filename.substring(dot);
    }

    public void deleteFile(String storedFileName) throws IOException {
        String absolutePath = new File(uploadPath).getAbsolutePath();
        File file = new File(absolutePath + File.separator + storedFileName);
        
        if (!file.exists()) {
            throw new IOException("파일이 존재하지 않습니다: " + storedFileName);
        }
        
        if (!file.delete()) {
            throw new IOException("파일 삭제 실패: " + storedFileName);
        }
    }
}
