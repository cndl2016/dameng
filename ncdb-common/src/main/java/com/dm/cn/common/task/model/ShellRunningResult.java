package com.dm.cn.common.task.model;




import com.dm.cn.common.task.constant.TaskConstant;
import com.dm.cn.common.task.handle.TaskResultHandle;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * shell执行结果结果处理
 * @author dyy
 * @date 2025-02-28
 */
public class ShellRunningResult implements TaskResultHandle {

    private final StringBuffer result = new StringBuffer();

    private final AtomicInteger status = new AtomicInteger(TaskConstant.STATUS_UNSTART);

    @Override
    public void sendResult(String msg) {
        result.append(msg);
    }

    @Override
    public void sendStatus(int status) {
        this.status.set(status);
    }

    @Override
    public String getResult() {
        return result.toString();
    }

    @Override
    public int getStatus() {
        if(status.get() > 0){
            return TaskConstant.STATUS_FAILURE;
        }
        return status.get();
    }
}
