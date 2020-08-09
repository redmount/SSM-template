package com.redmount.template.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传辅助类, 将
 */
public class FileUtil {
    /**
     * 转换
     *
     * @param multipartFile
     * @return
     */
    public static File MultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 用uuid作为文件名，防止生成的临时文件重复
        final File file = File.createTempFile(UUID.randomUUID().toString(), prefix);
        multipartFile.transferTo(file);
        return file;
    }
}
