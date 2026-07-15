package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.TokenConstants;
import com.dm.cn.common.core.domain.LoginBody;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.system.service.impl.SysLoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * token 控制
 *
 * @author dameng
 */
@RestController
public class TokenController {

    @Resource
    private SysLoginService sysLoginService;

    @PostMapping("login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        Map<String, String> ajax = new HashMap<>(1);
        // 生成令牌
        String token = sysLoginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid(), loginBody.getLanguage());
        ajax.put(TokenConstants.ACCESS_TOKEN, token);
        return AjaxResult.success(ajax);
    }

    @PostMapping("getToken")
    public AjaxResult getToken(@RequestBody LoginBody loginBody) {
        Map<String, String> ajax = new HashMap<>(1);
        String token = sysLoginService.getToken(loginBody.getUsername(), loginBody.getPassword(), loginBody.getLanguage());
        ajax.put(TokenConstants.ACCESS_TOKEN, token);
        return AjaxResult.success(ajax);
    }

}