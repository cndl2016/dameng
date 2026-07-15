package com.dm.cn.controller.system;

import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.web.controller.BaseController;
import com.dm.cn.common.core.web.domain.AjaxResult;
import com.dm.cn.common.core.web.page.TableDataInfo;
import com.dm.cn.common.log.annotation.Log;
import com.dm.cn.common.log.enums.BusinessType;
import com.dm.cn.common.security.annotation.RequiresPermissions;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.entity.param.SyncTaskParam;
import com.dm.cn.system.entity.param.SyncUserParam;
import com.dm.cn.system.entity.server.SysDomain;
import com.dm.cn.system.service.ISysUserService;
import com.dm.cn.system.service.SysDomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.naming.NamingException;
import java.util.List;

/**
 * 域信息
 *
 * @author dameng
 */
@RestController
@RequestMapping("/system/domain")
public class SysDomainController extends BaseController {

    @Resource
    private SysDomainService sysDomainService;

    @Resource
    private ISysUserService userService;

    /**
     * 获取域列表
     */
    @RequiresPermissions("system:domain:list")
    @GetMapping("/list")
    @ApiOperation(value = "获取域列表")
    public TableDataInfo list(SysDomain domain) {
        startPage();
        List<SysDomain> list = sysDomainService.selectDomainList(domain);
        return getDataTable(list);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = {"/", "/{domainId}"})
    @ApiOperation(value = "根据用户编号获取详细信息")
    public AjaxResult getInfo(@PathVariable(value = "domainId", required = false) Long domainId) {
        return AjaxResult.success(sysDomainService.selectDomainById(domainId));
    }

    /**
     * 新增域
     */
    @RequiresPermissions("system:domain:add")
    @Log(title = "域管理", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "新增域")
    public AjaxResult add(@Validated @RequestBody SysDomain domain) {
        String address = String.format(Constants.IP_PORT_FORMAT, domain.getServerHost(), domain.getServerPort());
        if (NumberConstants.ONE_STRING.equals(sysDomainService.checkDomainUnique(domain))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_ADD_FAILED, address));
        }
        domain.setCreateUser(SecurityUtils.getUsername());
        int result = sysDomainService.insertDomain(domain);
        if (result == -1) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_CONNECT_FAILED, address));
        }
        return AjaxResult.success(result);
    }

    /**
     * 修改域
     */
    @RequiresPermissions("system:domain:edit")
    @Log(title = "域管理", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "修改域")
    public AjaxResult edit(@Validated @RequestBody SysDomain domain) {
        String address = String.format(Constants.IP_PORT_FORMAT, domain.getServerHost(), domain.getServerPort());
        if (NumberConstants.ONE_STRING.equals(sysDomainService.checkDomainUnique(domain))) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_UPDATE_FAILED, address));
        }
        domain.setUpdateUser(SecurityUtils.getUsername());
        int result = sysDomainService.updateDomain(domain);
        if (result == -1) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_CONNECT_FAILED, address));
        }
        return AjaxResult.success(result);
    }

    /**
     * 删除域
     */
    @RequiresPermissions("system:domain:delete")
    @Log(title = "域管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{domainId}")
    @ApiOperation(value = "删除域")
    public AjaxResult remove(@PathVariable Long domainId) {
        return toAjax(sysDomainService.deleteDomainById(domainId));
    }

    /**
     * 获取域用户选项
     */
    @GetMapping("/getDomainUser/{domainId}")
    @ApiOperation(value = "获取域用户选项")
    public AjaxResult getDomainUser(@PathVariable Long domainId) {
        return AjaxResult.success(sysDomainService.getDomainUser(domainId));
    }

    /**
     * 导入域用户
     */
    @RequiresPermissions("system:domain:import")
    @PostMapping("/importDomainUser")
    @ApiOperation(value = "导入域用户")
    public AjaxResult importDomainUser(@RequestBody List<SysUser> userList) {
        return AjaxResult.success(userService.importDomainUser(userList));
    }

    /**
     * 同步域用户属性
     */
    @RequiresPermissions("system:domain:sync")
    @PostMapping("/syncDomainUser")
    @ApiOperation(value = "同步域用户属性")
    public AjaxResult syncDomainUser(@RequestBody SyncUserParam param) throws NamingException {
        return AjaxResult.success(sysDomainService.syncDomainUser(param));
    }

    /**
     * 同步任务
     */
    @RequiresPermissions("system:domain:sync")
    @PostMapping("/handleSyncTask")
    @ApiOperation(value = "同步任务")
    public AjaxResult handleSyncTask(@RequestBody SyncTaskParam param) {
        return AjaxResult.success(sysDomainService.handleSyncTask(param));
    }

    /**
     * 获取域内的用户列表
     */
    @GetMapping("/getSyncUserList/{domainId}")
    @ApiOperation(value = "获取域内的用户列表")
    public List<SysUser> getSyncUserList(@PathVariable Long domainId) {
        return userService.getSyncUserList(domainId);
    }

}