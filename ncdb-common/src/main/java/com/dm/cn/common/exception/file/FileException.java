package com.dm.cn.common.exception.file;

import com.dm.cn.common.exception.base.BaseException;

/**
 * 文件信息异常类
 *
 * @author dameng.cn
 */
public class FileException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file", code, args, null);
    }

}
