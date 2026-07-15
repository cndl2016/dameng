package com.dm.cn.system.config;

import com.alibaba.fastjson.JSON;
import com.dm.cn.common.annotation.RepeatSubmit;
import com.dm.cn.common.config.InfoConfig;
import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.filter.RepeatedlyRequestWrapper;
import com.dm.cn.common.utils.http.HttpHelper;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.config.security.utils.AbstractHeaderInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通用配置
 *
 * @author dameng.cn
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    /**
     * 不需要拦截地址
     */
    public static final String[] EXCLUDE_URLS = {"/login", "/logout", "/refresh"};

    public final String REPEAT_PARAMS = "repeatParams";

    public final String REPEAT_TIME = "repeatTime";

    @Value("${token.header}")
    private String header;

    @Resource
    private CacheService cacheService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /** 本地文件上传路径 */
        registry.addResourceHandler(TokenConstants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + InfoConfig.getProfile() + "/");

        /** swagger配置 */
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.
                addInterceptor(getHeaderInterceptor())
                .excludePathPatterns(EXCLUDE_URLS)
                .addPathPatterns("/**")
                .order(-10);
    }

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 有效期 1800秒
        config.setMaxAge(1800L);
        // 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // 返回新的CorsFilter
        return new CorsFilter(source);
    }

    /**
     * 自定义请求头拦截器
     */
    public AbstractHeaderInterceptor getHeaderInterceptor() {
        return new AbstractHeaderInterceptor() {
            @Override
            public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation) {
                String nowParams = "";
                if (request instanceof RepeatedlyRequestWrapper) {
                    RepeatedlyRequestWrapper repeatedlyRequest = (RepeatedlyRequestWrapper) request;
                    nowParams = HttpHelper.getBodyString(repeatedlyRequest);
                }
                // body参数为空，获取Parameter的数据
                if (StringUtils.isEmpty(nowParams)) {
                    nowParams = JSON.toJSONString(request.getParameterMap());
                }
                Map<String, Object> nowDataMap = new HashMap<String, Object>(16);
                nowDataMap.put(REPEAT_PARAMS, nowParams);
                nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

                // 请求地址（作为存放cache的key值）
                String url = request.getRequestURI();

                // 唯一值（没有消息头则使用请求地址）
                String submitKey = StringUtils.trimToEmpty(request.getHeader(header));

                // 唯一标识（指定key + url + 消息头）
                String cacheRepeatKey = TokenConstants.REPEAT_SUBMIT_KEY + url + submitKey;
                Object sessionObj = cacheService.getCacheObject(cacheRepeatKey);
                if (sessionObj != null) {
                    Map<String, Object> sessionMap = (Map<String, Object>) sessionObj;
                    if (sessionMap.containsKey(url)) {
                        Map<String, Object> preDataMap = (Map<String, Object>) sessionMap.get(url);
                        if (compareParams(nowDataMap, preDataMap) && compareTime(nowDataMap, preDataMap, annotation.interval())) {
                            return true;
                        }
                    }
                }
                Map<String, Object> cacheMap = new HashMap<String, Object>(1);
                cacheMap.put(url, nowDataMap);
                cacheService.setCacheObject(cacheRepeatKey, cacheMap, annotation.interval(), TimeUnit.MILLISECONDS);
                return false;
            }
        };
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int interval) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        if ((time1 - time2) < interval) {
            return true;
        }
        return false;
    }
}
