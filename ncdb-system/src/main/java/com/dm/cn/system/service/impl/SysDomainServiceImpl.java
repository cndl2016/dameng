package com.dm.cn.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.domain.SysUser;
import com.dm.cn.common.core.enums.SyncTaskEnum;
import com.dm.cn.common.core.enums.UserPropEnum;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.IdWorker;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.dm.cn.common.security.utils.SecurityUtils;
import com.dm.cn.common.utils.I18nMessageUtil;
import com.dm.cn.quartz.constant.ScheduleConstants;
import com.dm.cn.quartz.entity.server.SysJob;
import com.dm.cn.quartz.service.SysJobService;
import com.dm.cn.system.constant.MessageTipConstant;
import com.dm.cn.system.entity.param.SyncTaskParam;
import com.dm.cn.system.entity.param.SyncUserParam;
import com.dm.cn.system.entity.server.SysDomain;
import com.dm.cn.system.entity.server.SysDomainMapping;
import com.dm.cn.system.entity.server.UserProp;
import com.dm.cn.system.mapper.SysDomainMapper;
import com.dm.cn.system.mapper.SysDomainMappingMapper;
import com.dm.cn.system.service.ISysUserService;
import com.dm.cn.system.service.SysDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author root
 * @date 2023/05/08
 */
@Service
public class SysDomainServiceImpl extends ServiceImpl<SysDomainMapper, SysDomain>
        implements SysDomainService {

    private static final Logger log = LoggerFactory.getLogger(SysDomainServiceImpl.class);
    private static final String USER_NAME_PROP = "0";
    private static final String NICK_NAME_PROP = "1";
    private static final String PHONE_PROP = "2";
    private static final String MAIL_PROP = "3";

    @Resource
    private SysDomainMapper sysDomainMapper;

    @Resource
    private SysDomainMappingMapper sysDomainMappingMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private SysJobService sysJobService;

    @Resource
    private DbTypeBean dbTypeBean;

    @Override
    public List<SysDomain> selectDomainList(SysDomain domain) {
        domain.setH2(dbTypeBean.isH2());
        List<SysDomain> resultList = sysDomainMapper.selectDomainList(domain);
        resultList.forEach(result -> {
            result.setUserNo(sysDomainMapper.countDomainUser(result.getId()));
        });
        return resultList;
    }

    @Override
    public String checkDomainUnique(SysDomain domain) {
        Long domainId = ObjectUtils.isEmpty(domain.getId()) ? -1L : domain.getId();
        SysDomain info = sysDomainMapper.checkDomainUnique(domain);
        if (ObjectUtils.isNotEmpty(info) && info.getId().longValue() != domainId.longValue()) {
            return NumberConstants.ONE_STRING;
        }
        return NumberConstants.ZERO_STRING;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDomain(SysDomain domain) {
        try {
            // 测试域连接
            ldapConnect(domain);
        } catch (javax.naming.AuthenticationException e) {
            log.error("认证失败：{}", e);
            return -1;
        } catch (Exception e) {
            log.error("认证出错：{}", e);
            return -1;
        }
        domain.setId(IdWorker.getId().nextId());
        int result = sysDomainMapper.insertDomain(domain);
        if (result > 0) {
            // 新增域属性映射
            insertDomainMapping(domain);
        }
        return result;
    }

    private int insertDomainMapping(SysDomain domain) {
        List<SysDomainMapping> mappingList = domain.getMappingList();
        if (ObjectUtils.isNotNull(mappingList)) {
            for (SysDomainMapping mapping : mappingList) {
                if (ObjectUtils.isNull(mapping.getDomainUserProp())) {
                    mapping.setDomainUserProp(UserPropEnum.getDefault(mapping.getManagerUserProp()));
                }
                mapping.setId(IdWorker.getId().nextId());
                mapping.setDomainId(domain.getId());
            }
            return sysDomainMappingMapper.batchDomainMapping(mappingList);
        }
        return 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDomain(SysDomain domain) {
        try {
            // 测试域连接
            ldapConnect(domain);
        } catch (javax.naming.AuthenticationException e) {
            log.error("认证失败：{}", e);
            return -1;
        } catch (Exception e) {
            log.error("认证出错：{}", e);
            return -1;
        }
        int result = sysDomainMapper.updateDomain(domain);
        if (result > 0) {
            // 更新域属性映射
            sysDomainMappingMapper.batchDeleteByDomainId(domain.getId());
            // 新增域属性映射
            insertDomainMapping(domain);
        }
        return result;
    }

    @Override
    public SysDomain selectDomainById(Long domainId) {
        SysDomain result = sysDomainMapper.selectDomainById(domainId);
        // 查询域属性映射
        result.setMappingList(sysDomainMappingMapper.selectByDomainId(domainId));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDomainById(Long domainId) {
        // 删除域属性映射
        if (sysDomainMapper.countDomainUser(domainId) > 0) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_USER_EXIST));
        }
        sysDomainMappingMapper.batchDeleteByDomainId(domainId);
        removeDomainUserSyncJob(sysDomainMapper.selectDomainById(domainId));
        return sysDomainMapper.deleteDomainById(domainId);
    }

    @Override
    public List<SysUser> getDomainUser(Long domainId) {
        SysDomain domain = selectDomainById(domainId);
        if (ObjectUtils.isEmpty(domain)) {
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_NOT_EXIST));
        }
        List<SysUser> resultList = new ArrayList<>();
        try {
            resultList = getLdapUsers(domain);
            List<String> filterList = sysUserService.selectFilterUid(domain.getId());
            resultList = resultList.stream().filter(sysUser -> !filterList.contains(sysUser.getObjectGuid())).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取域用户出错：{}", e);
        }
        return resultList;
    }

    @Override
    public boolean syncDomainUser(SyncUserParam param) throws NamingException {
        if (ObjectUtils.isEmpty(param.getUserIdList())) {
            log.error("同步域用户失败：{}", I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_LIST_EMPTY));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_USER_LIST_EMPTY));
        }
        SysDomain domain = selectDomainById(param.getDomainId());
        if (ObjectUtils.isEmpty(domain)) {
            log.error("同步域用户失败：{}", I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_NOT_EXIST));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_DOMAIN_NOT_EXIST));
        }
        List<SysUser> remoteUserList = getLdapUsers(domain);
        List<SysUser> localUserList = sysUserService.selectUserByIds(param.getUserIdList());
        // 交集
        List<SysUser> updateList = remoteUserList.stream().filter(item ->
                localUserList.stream().map(SysUser::getObjectGuid)
                        .collect(Collectors.toList()).contains(item.getObjectGuid()))
                .collect(Collectors.toList());
        // 差集:manager存在而ad域不存在的用户
        List<SysUser> deleteList = localUserList.stream().filter(item ->
                !remoteUserList.stream().map(SysUser::getObjectGuid)
                        .collect(Collectors.toList()).contains(item.getObjectGuid()))
                .collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(deleteList)) {
            Long[] deleteIdArray = deleteList.stream().map(SysUser::getUserId).collect(Collectors.toList()).toArray(new Long[deleteList.size()]);
            sysUserService.deleteUserByIds(deleteIdArray);
        }
        if (!ObjectUtils.isEmpty(updateList)) {
            // todo 批量更新待优化
            sysUserService.updateBatchUserByUid(updateList);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleSyncTask(SyncTaskParam param) {
        SysDomain domain = selectDomainById(param.getDomainId());
        domain.setSyncEnable(param.getSyncEnable());
        domain.setSyncCron(param.getSyncCron());
        sysDomainMapper.updateDomain(domain);
        if (SyncTaskEnum.DISABLED.getValue().equals(param.getSyncEnable())) {
            removeDomainUserSyncJob(domain);
            return true;
        } else if (SyncTaskEnum.ENABLED.getValue().equals(param.getSyncEnable())) {
            addDomainUserSyncJob(domain);
            return true;
        } else {
            log.error("域任务操作失败：{}", I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_UNKNOWN_OPERATE_TYPE));
            throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_UNKNOWN_OPERATE_TYPE));
        }
    }

    private void addDomainUserSyncJob(SysDomain domain) {
        try {
            SysJob job = new SysJob();
            job.setJobId(IdWorker.getId().nextId());
            job.setJobName("userSyncJob");
            job.setJobGroup("ncdb-system");
            job.setJobParam(domain.getId().toString());
            job.setBeanTarget("com.dm.cn.system.service.job.DomainUserSyncJob");
            job.setBeanMethodTarget("userSyncJob");
            job.setCronExpression(domain.getSyncCron());
            job.setConcurrent(ScheduleConstants.CONCURRENT_DISABLED);
            job.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
            job.setCreateUser(SecurityUtils.getUsername());
            job.setCreateTime(new Date(System.currentTimeMillis() / NumberConstants.ONE_THOUSAND * NumberConstants.ONE_THOUSAND));
            job.setRemark("域用户同步" + domain.getId() + "任务");

            sysJobService.saveOrUpdate(job);
            sysJobService.insertJob(job);

            log.info("域用户同步任务" + domain.getId() + "任务创建完成！");
        } catch (Exception e) {
            log.error("addDomainUserSyncJob exception: {}", e);
            throw new ServiceException(e.getMessage());
        }
    }

    private void removeDomainUserSyncJob(SysDomain domain) {
        try {
            SysJob job = sysJobService.lambdaQuery().eq(SysJob::getJobParam, domain.getId().toString()).one();
            if (job != null) {
                sysJobService.removeById(job);
                sysJobService.deleteJob(job);
            }
        } catch (Exception e) {
            log.error("removeDomainUserSyncJob exception: {}", e);
            throw new ServiceException(e.getMessage());
        }
    }

    private static LdapContext ldapConnect(SysDomain domain) throws NamingException {
        String factory = "com.sun.jndi.ldap.LdapCtxFactory";
        String simple = "simple";
        String url = String.format(Constants.LDAP, domain.getServerHost(), domain.getServerPort());
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, url);
        //连接超时设置为3秒
        env.put("com.sun.jndi.ldap.connect.timeout", "3000");
        env.put(Context.SECURITY_AUTHENTICATION, simple);
        env.put(Context.SECURITY_PRINCIPAL, domain.getAdminAccount());
        env.put(Context.SECURITY_CREDENTIALS, domain.getAdminPass());
        env.put("java.naming.referral", "ignore");
        LdapContext ctx;
        Control[] connCtls = null;
        ctx = new InitialLdapContext(env, connCtls);
        log.info("认证成功:" + url);
        return ctx;
    }

    /**
     * 获取LDAP用户列表
     *
     * @param domain
     * @return {@link List}<{@link SysUser}>
     * @throws NamingException
     */
    private List<SysUser> getLdapUsers(SysDomain domain) throws NamingException {
        List<SysUser> adUserList = new ArrayList<>();
        UserProp userProp = parseAttr(domain.getMappingList());
        //定制返回类型
        String[] returnedAttrs = userProp.getPropAttrs().split(SymbolConstants.COMMA);
        //验证身份,获得ldapContext
        LdapContext ldapContext = null;
        try {
            ldapContext = ldapConnect(domain);
            //页面大小[1，1000）
            int pageSize = 1000;
            int totalResults = 0;
            //创建搜索控制器
            SearchControls searchControls = new SearchControls();
            //设置搜索范围,1、平级检索；2、树形检索
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //设置返回属性
            searchControls.setReturningAttributes(returnedAttrs);
            //开启分页查询
            ldapContext.setRequestControls(new Control[]{new PagedResultsControl(pageSize, Control.CRITICAL)});
            //根据设置的域节点、过滤器和搜索控制器搜索LDAP得到结果
            NamingEnumeration<SearchResult> search = ldapContext.search(domain.getBaseDn(), domain.getUserFilter(), searchControls);
            if (search == null || !search.hasMoreElements()) {
                log.info("未查询到LDAP用户");
                return adUserList;
            }
            while (search.hasMoreElements()) {
                // 处理查询到的结果
                SearchResult searchResult = search.next();
                NamingEnumeration<? extends Attribute> attrs = searchResult.getAttributes().getAll();
                // todo 改造为searchResult.getAttributes().get("属性")
                SysUser user = new SysUser();
                while (attrs.hasMoreElements()) {
                    Attribute attr = attrs.next();
                    if (attr.getID().equalsIgnoreCase(userProp.getUserNameProp())) {
                        user.setUserName(attr.get().toString());
                    } else if (attr.getID().equalsIgnoreCase(userProp.getNickNameProp())) {
                        user.setNickName(attr.get().toString());
                    } else if (attr.getID().equalsIgnoreCase(userProp.getPhoneProp())) {
                        user.setPhoneNumber(attr.get().toString());
                    } else if (attr.getID().equalsIgnoreCase(userProp.getMailProp())) {
                        user.setEmail(attr.get().toString());
                    } else if ("objectGUID".equalsIgnoreCase(attr.getID())) {
                        user.setObjectGuid(getObjectGuid(attr.get().toString().getBytes()));
                    }
                }
                if (!ObjectUtils.isEmpty(user.getUserName())) {
                    user.setDomainId(domain.getId());
                    adUserList.add(user);
                    //总数加1
                    totalResults++;
                }
            }
            log.info("总数={}", totalResults);
        } catch (IOException e) {
            log.error("获取域用户错误：{}", e);
        } finally {
            if (!ObjectUtils.isEmpty(ldapContext)) {
                ldapContext.close();
            }
        }
        return adUserList;
    }

    /**
     * 获取属性映射
     * @param mappingList
     * @return
     */
    private UserProp parseAttr(List<SysDomainMapping> mappingList) {
        UserProp prop = new UserProp();
        StringJoiner propAttrs = new StringJoiner(SymbolConstants.COMMA);
        mappingList.forEach(mapping -> {
            switch (mapping.getManagerUserProp()) {
                case USER_NAME_PROP:
                    prop.setUserNameProp(mapping.getDomainUserProp());
                    propAttrs.add(mapping.getDomainUserProp());
                    break;
                case NICK_NAME_PROP:
                    prop.setNickNameProp(mapping.getDomainUserProp());
                    propAttrs.add(mapping.getDomainUserProp());
                    break;
                case PHONE_PROP:
                    if (!ObjectUtils.isEmpty(mapping.getDomainUserProp())) {
                        prop.setPhoneProp(mapping.getDomainUserProp());
                        propAttrs.add(mapping.getDomainUserProp());
                    }
                    break;
                case MAIL_PROP:
                    if (!ObjectUtils.isEmpty(mapping.getDomainUserProp())) {
                        prop.setMailProp(mapping.getDomainUserProp());
                        propAttrs.add(mapping.getDomainUserProp());
                    }
                    break;
                default:
                    throw new ServiceException(I18nMessageUtil.getMessage(MessageTipConstant.SYSTEM_UNKNOWN_PROP_TYPE));
            }
        });
        propAttrs.add("objectGUID");
        propAttrs.add("name");
        prop.setPropAttrs(propAttrs.toString());
        return prop;
    }

    /**
     * guid转换，使用此方法获得的guid仍与ad域中不同，但能保证唯一性
     *
     * @param guid
     * @return
     */
    private static String getObjectGuid(byte[] guid) {
        String strGuid = "";
        StringBuilder byteGuid = new StringBuilder("");
        for (int c = 0; c < guid.length; c++) {
            byteGuid.append("\\").append(addLeadingZero((int) guid[c] & 0xFF));
        }
        strGuid = strGuid + addLeadingZero((int) guid[3] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[2] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[1] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[0] & 0xFF);
        strGuid = strGuid + "-";
        strGuid = strGuid + addLeadingZero((int) guid[5] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[4] & 0xFF);
        strGuid = strGuid + "-";
        strGuid = strGuid + addLeadingZero((int) guid[7] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[6] & 0xFF);
        strGuid = strGuid + "-";
        strGuid = strGuid + addLeadingZero((int) guid[8] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[9] & 0xFF);
        strGuid = strGuid + "-";
        strGuid = strGuid + addLeadingZero((int) guid[10] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[11] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[12] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[13] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[14] & 0xFF);
        strGuid = strGuid + addLeadingZero((int) guid[15] & 0xFF);
        return strGuid;
    }

    private static String addLeadingZero(int k) {
        return (k <= 0xF) ? "0" + Integer.toHexString(k) : Integer
                .toHexString(k);
    }
}




