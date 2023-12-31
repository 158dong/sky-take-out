package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> unload(MultipartFile file){

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取.后面的后缀名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成UUid
            String uuid = UUID.randomUUID().toString();
            //生成新的文件名
            String newName = uuid + extension;
            return Result.success(aliOssUtil.upload(file.getBytes(),newName));
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
            return Result.error(e.toString());
        }
    }
}
