package com.redmount.template.system.controller;

import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@Api(tags = "文件接口")
@RequestMapping("/file")
public class FileController {
    private String fileRootPath="D:\\";

    @PostMapping
    @ApiOperation("文件上传")
    public Result upload(MultipartFile[] files) {
        String filePath = "";
        // 多文件上传
        for (MultipartFile file : files) {
            // 上传简单文件名
            String originalFilename = file.getOriginalFilename();
            // 存储路径
            filePath = new StringBuilder(fileRootPath)
                    .append(System.currentTimeMillis())
                    .append(originalFilename)
                    .toString();
            try {
                // 保存文件
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultGenerator.genSuccessResult(filePath);
    }
}
