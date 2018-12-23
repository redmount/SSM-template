package com.redmount.template;

import com.redmount.template.configurer.ApplicationStartupConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 朱峰
 * @date 2018年11月9日
 */
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class Application {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.addListeners(new ApplicationStartupConfigurer());
        springApplication.run(args);
    }
}

