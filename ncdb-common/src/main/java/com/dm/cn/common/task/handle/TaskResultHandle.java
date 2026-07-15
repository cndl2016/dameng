package com.dm.cn.common.task.handle;

/**
 * 任务结果处理
 *
 * @author dyy
 * @date 2025-02-28
 */
public interface TaskResultHandle {

    /**
     * 发送执行结果，按行发送
     *
     * @param msg 执行结果
     */
    void sendResult(String msg);

    /**
     * 发送执行状态
     *
     * @param status 执行状态
     */
    void sendStatus(int status);

    /**
     * 获取执行结果
     *
     * @return
     */
    String getResult();

    /**
     * 获取执行状态
     *
     * @return
     */
    int getStatus();
}
