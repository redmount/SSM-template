package com.redmount.template.system.controller;

import com.redmount.template.base.model.SysFile;
import com.redmount.template.base.service.SysFileBaseService;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultGenerator;
import com.redmount.template.core.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RestController
@Api(tags = "文件接口")
@RequestMapping("/file")
public class FileController {

    static char FILE_SEPARATOR_CHAR = File.separatorChar;
    @Autowired
    SysFileBaseService service;
    @Value("${upload-file-path}")
    private String uploadFilePath;

    @PostMapping
    @ApiOperation("文件上传")
    public Result upload(MultipartFile[] files) throws IOException {
        List<SysFile> fileList = new ArrayList<SysFile>();
        // 多文件上传
        for (MultipartFile file : files) {
            // 原始文件名
            String originalFileName = file.getOriginalFilename();
            // 原始扩展名
            String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
            // 保存的文件名
            String savedFileName = UUID.randomUUID() + suffix;
            // 保存的文件路径
            String savedPath = uploadFilePath
                    + Calendar.getInstance().get(Calendar.YEAR) + FILE_SEPARATOR_CHAR
                    + Calendar.getInstance().get(Calendar.MONTH) + FILE_SEPARATOR_CHAR;
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

    @GetMapping("/downloadAsAttachment")
    public void downloadAsAttachment(@RequestParam("pk") String pk, HttpServletResponse response) throws UnsupportedEncodingException {
        SysFile fileRecord = service.findById(pk);
        if (fileRecord == null) {
            throw new ServiceException("附件记录不存在");
        }

        File file = new File(fileRecord.getServerFileName());
        if (!file.isFile()) {
            throw new ServiceException("文件不存在");
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileRecord.getOriFileName(), "UTF-8"));

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream())) {
            byte[] buffer_ = new byte[1024];
            int n = bufferedInputStream.read(buffer_);
            while (n != -1) {
                bufferedOutputStream.write(buffer_);
                n = bufferedInputStream.read(buffer_);
            }
        } catch (Exception e) {
        }
    }
}
