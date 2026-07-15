package com.dm.cn.controller.system;

import com.dm.cn.common.core.domain.LoginUser;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.core.utils.file.FileTypeUtils;
import com.dm.cn.common.core.utils.file.MimeTypeUtils;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.common.utils.file.service.ISysFileService;
import com.dm.cn.system.config.security.service.CacheService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 个人信息 业务处理
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(SysProfileController.class);

    @Resource
    private ISysUserService userService;

    @Resource
    private CacheService cacheService;

    @Resource
    private ISysFileService remoteFileService;

    /**
     * windows环境基础路径
     */
    private static final String WIN_BASE_PATH = "d:/opt/files/";

    /**
     * linux环境基础路径
     */
    private static final String LINUX_BASE_PATH = "/opt/files/";

    /**
     * 操作系统名称
     */
    private static final String OS_NAME = "os.name";

    /**
     * windows操作系统名称
     */
    private static final String WIN = "windows";

    /**
     * 基础路径
     */
    private static String BASE_PATH = LINUX_BASE_PATH;

    static {
        if (System.getProperty(OS_NAME).toLowerCase().contains(WIN)) {
            BASE_PATH = WIN_BASE_PATH;
        }
    }

    /**
     * 个人信息
     */
    @GetMapping
    @ApiOperation(value = "获取个人信息")
    public AjaxResult profile() {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(username));
        ajax.put("roleGroupEn", userService.selectUserRoleGroupEn(username));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改用户")
    public AjaxResult updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhoneNumber())
                && !userService.checkPhoneUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_UPDATE_PHONE_EXIST, user.getUserName()));
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && !userService.checkEmailUnique(user)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_UPDATE_EMAIL_EXIST, user.getUserName()));
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        if (userService.updateUserProfile(user) > 0) {
            // 更新缓存用户信息
            loginUser.getSysUser().setNickName(user.getNickName());
            loginUser.getSysUser().setPhoneNumber(user.getPhoneNumber());
            loginUser.getSysUser().setEmail(user.getEmail());
            loginUser.getSysUser().setSex(user.getSex());
            cacheService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    @ApiOperation(value = "重置密码")
    public AjaxResult updatePwd(String oldPassword, String newPassword) {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_PERSON_UPDATE_PASS_FAILED));
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_PERSON_PASS_SAME));
        }
        if (userService.resetUserPwd(username, SecurityUtils.encryptPassword(newPassword)) > 0) {
            // 更新缓存用户密码
            LoginUser loginUser = SecurityUtils.getLoginUser();
            loginUser.getSysUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            cacheService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error();
    }

    /**
     * 头像上传
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    @ApiOperation(value = "头像上传")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) {
        if (!file.isEmpty()) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String extension = FileTypeUtils.getExtension(file);
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
                throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_PERSON_PIC_FORMAT_WRONG, Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION)));
            }
            try {
                String url = remoteFileService.uploadFile(file);
                if (userService.updateUserAvatar(loginUser.getUsername(), url)) {
                    AjaxResult ajax = AjaxResult.success();
                    ajax.put("imgUrl", url);
                    String avatarBinary = userService.avatarToBinary(url);
                    ajax.put("avatarBinary", avatarBinary);
                    // 更新缓存用户头像
                    loginUser.getSysUser().setAvatar(url);
                    loginUser.getSysUser().setAvatarBinary(avatarBinary);
                    cacheService.setLoginUser(loginUser);
                    return ajax;
                }
            } catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
                log.error("avatar exception: {}", e);
                return AjaxResult.error();
            }
        }
        return AjaxResult.error();
    }
}
