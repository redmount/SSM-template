package com.redmount.template.system.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("externalFile")
public class ExternalFileController {
    @Value("${ali-oss-access-key}")
    private String aliOSSAccessKey;

    @Value("${ali-oss-bucket-address}")
    private String aliOSSBucketAddress;
}
