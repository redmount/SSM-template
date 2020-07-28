package com.redmount.template;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.redmount.template.configurer.ApplicationStartupConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 朱峰
 * @date 2018年11月9日
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationStartupConfigurer());
        springApplication.run(args);
    }
}

