package com.dm.cn.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dm.cn.common.enums.RestStatusEnum;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @Copyright: Harbin Institute of Technology.All rights reserved.
 * @Description: RestTemplate工具类
 * @author: thailandking
 * @since: 2020/3/19 15:24
 * @history: 1.2020/3/19 created by thailandking
 */
@Data
@Configuration
public class RestTemplateUtil {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateUtil.class);

    /**
     * @Author thailandking
     * @Date 2020/3/19 17:37
     * @LastEditors thailandking
     * @LastEditTime 2020/3/19 17:37
     * @Description 获取自定义RestTemplate
     */
    public RestTemplate getCustomRestTemplate(Integer timeout) {
        //校验
        if (timeout == null || timeout <= 0) {
            timeout = 1000 * 5;
        }
        //timeout
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setConnectTimeout(timeout)
                .setSocketTimeout(3 * timeout)
                .build();
        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(config).setRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
        HttpClient httpClient = builder.build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }

    /**
     * @Author thailandking
     * @Date 2020/3/19 17:37
     * @LastEditors thailandking
     * @LastEditTime 2020/3/19 17:37
     * @Description 获取自定义header
     */
    public HttpHeaders getCustomHeaders(Map<String, String> headerParams, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(headerParams);
        if (mediaType != null) {
            headers.setContentType(mediaType);
        }
        return headers;
    }

    /**
     * @Author thailandking
     * @Date 2020/3/19 17:47
     * @LastEditors thailandking
     * @LastEditTime 2020/3/19 17:47
     * @Description 处理调用结果 (接口返回数据必须能解析成Map对象格式)
     */
    public Map<String, Object> handleResult(ResponseEntity<String> resp) {
        Map<String, Object> resultMap = new HashMap<>(2);
        if (resp.getStatusCodeValue() == RestStatusEnum.SUCCESS.getValue()) {
            String body = resp.getBody();
            Map dataMap = JSON.parseObject(body, Map.class);
            resultMap.put("success", true);
            resultMap.put("data", dataMap);
        } else {
            resultMap.put("success", false);
        }
        return resultMap;
    }

    /**
     * POST 请求Json数据
     *
     * @param tokenUrl          请求URL
     * @param jsonParams   请求Json数据
     * @param timeout      超时时间
     * @param headerParams 头部参数
     * @return 数据Map，success标识
     */
    public ResponseEntity<String> postByJson(String tokenUrl, String jsonParams, Integer timeout, Map<String, String> headerParams) {
        RestTemplate customRestTemplate = getCustomRestTemplate(timeout);
        HttpHeaders customHeaders = getCustomHeaders(headerParams, MediaType.APPLICATION_JSON);
        return customRestTemplate.postForEntity(tokenUrl, new HttpEntity<>(jsonParams, customHeaders), String.class);
    }

    /**
     * GET 请求Params数据
     * inputParams必须为String
     *
     * @param url          请求URL
     * @param inputParams  请求Params数据
     * @param timeout      超时时间
     * @param headerParams 头部参数
     * @return 数据Map，success标识
     */
    public Map<String, Object> getParams(String url, Map<String, String> inputParams, Integer timeout, Map<String, String> headerParams) {
        RestTemplate customRestTemplate = getCustomRestTemplate(timeout);
        HttpHeaders customHeaders = getCustomHeaders(headerParams, null);
        HttpEntity entity = new HttpEntity<>(customHeaders);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.setAll(inputParams);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(params).build().encode().toUri();
        ResponseEntity<String> resp = customRestTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        return handleResult(resp);
    }

    /**
     * 发送ｐｏｓｔ请求
     *
     * @param json
     * @param url
     * @return {@link String}
     */
    public static String sendPost(JSONObject json, String url) {
        log.info("------------------------------post request: \n" + json.toJSONString() + "\nurl: " + url);
        //创建默认的ｈｔｔｐ连接
        CloseableHttpClient client = HttpClients.createDefault();
        //创建ｐｏｓｔ请求
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        // sonar扫描误判：basic认证方式不作改动 cn
        post.addHeader("Authorization", "Basic YWRtaW46");
        String response;
        try {
            //创建请求实体
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            //创建请求响应
            HttpResponse httpResponse = client.execute(post);
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStream, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            //读取ｂｕｆｆｅｒ中的响应数据
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            //关闭资源
            inStream.close();

            response = sb.toString();
            if (httpResponse.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK) {
                log.info(String.format("请求发送成功, json: %s\n", json.toString()));
                if (!ObjectUtils.isEmpty(client)) {
                    client.close();
                }
            } else {
                log.info(String.format("请求发送失败, json: %s\n", json.toString()));
                if (!ObjectUtils.isEmpty(client)) {
                    client.close();
                }
            }
        } catch (Exception e) {
            log.error("sendPost exception: {}", e);
            throw new RuntimeException(e);
        }
        return response;
    }
}
