package com.dm.cn.system.entity.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息对应属性
 *
 * @author dameng
 * @date 2023/05/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProp {

    /** 用户名对应属性 */
    private String userNameProp;

    /** 昵称对应属性 */
    private String nickNameProp;

    /** 手机号对应属性 */
    private String phoneProp;

    /** 邮箱对应属性 */
    private String mailProp;

    /** 属性字符串 */
    private String propAttrs;
}
