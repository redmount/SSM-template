package com.redmount.template.configurer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.redmount.template.core.Result;
import com.redmount.template.core.ResultCode;
import com.redmount.template.core.exception.AuthorizationException;
import com.redmount.template.core.exception.ServiceException;
import com.redmount.template.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Spring MVC 配置
 *
 * @author 朱峰
 */
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);
    /**
     * 当前的运行模式
     */
    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registStaticResource(registry);
    }

    private void registStaticResource(ResourceHandlerRegistry registry) {
        if ("dev".equals(env)) {
            registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
        super.addResourceHandlers(registry);
    }

    /**
     * 使用阿里 FastJson 作为JSON MessageConverter
     *
     * @param converters MVC转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializeFilters(new FastJsonPropertyPreFilter());
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect); // 禁用循环引用特性
        // 按需配置，更多参考FastJson文档哈
        converter.setFastJsonConfig(config);
        converter.setDefaultCharset(Charset.forName("UTF-8"));
        converters.add(converter);
    }

    /**
     * 统一异常处理
     *
     * @param exceptionResolvers 异常处理器
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add((request, response, handler, e) -> {
            logger.info("------------------------异常开始------------------------");
            logger.info("地址:" + request.getRequestURI());
            logger.info("地址栏参数:" + request.getQueryString());
            logger.info("请求方式:" + request.getMethod());
            logger.info("Header:" + RequestUtil.getHeaderStringFromRequest(request));
            // logger.info("Body:" + RequestUtil.getBodyStringFromRequest(request));
            logger.info("请求IP:" + RequestUtil.getIpAddress(request));
            Result result = new Result();
            if (e instanceof ServiceException) {
                //业务失败的异常，如“账号或密码错误”
                result.setCode(ResultCode.SERVICE_EXCEPTION)
                        .setException(((ServiceException) e).getException())
                        .setData(null);
                logger.info(JSON.toJSONString(((ServiceException) e).getException()));
                // 如果返回的业务异常有值,则在message里输入此值
                if (((ServiceException) e).getException() != null) {
                    result.setMessage(((ServiceException) e).getException().getTitle());
                } else {
                    result.setMessage("未知异常");
                }
                logger.info(e.getMessage());
            } else if (e instanceof AuthorizationException) {
                result.setCode(ResultCode.UNAUTHORIZED).setMessage("登录失效");
            } else if (e instanceof NoHandlerFoundException) {
                result.setCode(ResultCode.NOT_FOUND).setMessage("接口 [" + request.getRequestURI() + "] 不存在");
            } else if (e instanceof ServletException) {
                result.setCode(ResultCode.FAIL).setMessage(e.getMessage()).setData(e.getStackTrace());
            } else {
                result.setCode(ResultCode.INTERNAL_SERVER_ERROR).setMessage("接口 [" + request.getRequestURI() + "] 内部错误：【" + e.getMessage() + "】，请联系管理员");
                String message;
                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    message = String.format("接口 [%s] 出现异常，方法：%s.%s，异常摘要：%s",
                            request.getRequestURI(),
                            handlerMethod.getBean().getClass().getName(),
                            handlerMethod.getMethod().getName(),
                            e.toString());
                } else {
                    message = e.toString();
                }
                if ("dev".equals(env)) {
                    result.setData(e.getStackTrace());
                }
                logger.error(message, e);
            }
            responseResult(response, result);
            logger.info("------------------------异常结束------------------------");
            return new ModelAndView();
        });
    }

    /**
     * 解决跨域问题
     *
     * @param registry 跨域注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowCredentials(false).maxAge(3600);
    }

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!"dev".equals(env)) {
            // 非dev模式下,绕过token验证
            registry.addInterceptor(authenticationInterceptor())
                    .addPathPatterns("/**");    // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
        }
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    private void responseResult(HttpServletResponse response, Result result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(result.getCode());
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}
