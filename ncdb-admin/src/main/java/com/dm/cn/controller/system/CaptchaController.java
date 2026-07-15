package com.dm.cn.controller.system;

import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.system.service.impl.SysLoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码操作处理
 *
 * @author dameng
 */
@RestController
public class CaptchaController {

    @Resource
    private SysLoginService loginService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode() {
        return loginService.createCaptcha();
    }
}
