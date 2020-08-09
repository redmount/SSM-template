package com.redmount.template.system.controller;

import com.redmount.template.base.model.SysFile;
import com.redmount.template.base.service.SysFileBaseService;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@Api(tags = "文件接口")
@RequestMapping("/file")
public class FileController {

    @Value("${upload-file-path}")
    private String uploadFilePath;

    @Autowired
    SysFileBaseService service;

    @PostMapping
    @ApiOperation("文件上传")
    public Result upload(MultipartFile[] files) throws IOException {
        String filePath = "";
        List<SysFile> fileList = new ArrayList<SysFile>();
        // 多文件上传
        for (MultipartFile file : files) {
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            // 原始扩展名
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 保存的文件名
            String savedFileName = UUID.randomUUID().toString() + suffix;
            // 保存的文件路径
            String savedPath = uploadFilePath + "/"
                    + Calendar.getInstance().get(Calendar.YEAR) + "/"
                    + Calendar.getInstance().get(Calendar.MONTH) + '/';
            // 返回的结构
            SysFile sysFile = new SysFile();
            // 原始文件名
            sysFile.setOriFileName(originalFileName);
            // 保存在服务器上的相对地址
            sysFile.setServerFileName(savedPath + savedFileName);
            // 扩展名
            sysFile.setSuffix(suffix);
            // 文件大小
            sysFile.setSize(file.getSize());
            fileList.add(sysFile);
            File savedFile = new File(savedPath);
            if (!savedFile.exists()) {
                savedFile.mkdirs();
            }
            file.transferTo(new File(savedPath + savedFileName));
            // 存记录
            service.save(sysFile);

        }
        return ResultGenerator.genSuccessResult(fileList);
    }
}
