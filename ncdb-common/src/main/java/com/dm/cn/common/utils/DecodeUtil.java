package com.dm.cn.common.utils;


import com.dm.cn.common.core.constant.MgConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.exception.ServiceException;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * 加解码工具类
 *
 * @author DAMENG
 * @date 2024/10/30
 */
public class DecodeUtil {
    private static final Logger log = LoggerFactory.getLogger(DecodeUtil.class);

    private static final String ENCRYPT_ALGORITHM = "HmacSHA256";

    /**
     * 加密签名
     *
     * @param signStr   签名
     * @param timestamp 时间戳
     * @return {@link String}
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String encryptSign(String signStr, int timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        // 将json格式的签名加上时间戳当做要加密的签名字符串
        String timeAndSignStr = timestamp + "\n" + signStr;
        Mac mac = Mac.getInstance(ENCRYPT_ALGORITHM);
        mac.init(new SecretKeySpec(timeAndSignStr.getBytes(StandardCharsets.UTF_8), ENCRYPT_ALGORITHM));
        byte[] signData = mac.doFinal(new byte[]{});
        return new String(Base64.encodeBase64(signData));
    }

    /**
     * 钉钉自定义机器人安全设置
     * 把timestamp+"\n"+密钥当做签名字符串，使用HmacSHA256算法计算签名，然后进行Base64 encode，最后再把签名参数再进行urlEncode，得到最终的签名（需要使用UTF-8字符集）
     *
     * @param secret    密钥
     * @param timestamp 时间戳
     * @return {@link String}
     */
    public static String dingHmacSha256(String secret, String timestamp) {
        try {
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance(ENCRYPT_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ENCRYPT_ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("钉钉HmacSHA256加密失败:{}", e);
        }
        return null;
    }

    /**
     * 校验code合法性
     *
     * @param code 槽列表，逗号隔开
     * @return false 不合法，true 合法
     */
    private static boolean checkCodeValidate(String code) {
        String regex = "^([0-9]\\d*(-[1-9]\\d*)?)$";
        Pattern pattern = Pattern.compile(regex);
        if ("".equals(code)) {
            // code为空，即组下无槽
            return false;
        }
        Arrays.stream(code.split(SymbolConstants.COMMA)).forEach(entry -> {
            if (!pattern.matcher(entry).matches()) {
                log.error(String.format("%s code=%s", MgConstants.SLOT_DECODE_CHECK_ERROR, code));
                throw new ServiceException(MgConstants.SLOT_DECODE_CHECK_ERROR + entry);
            }
        });
        return true;
    }
}
