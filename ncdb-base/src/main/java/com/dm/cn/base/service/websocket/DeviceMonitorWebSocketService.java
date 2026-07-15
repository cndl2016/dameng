package com.dm.cn.base.service.websocket;

import com.dm.cn.base.utils.WebSocketSendMsgUtil;
import com.dm.cn.common.core.utils.SpringUtils;
import com.dm.cn.common.utils.ThreadPoolManager;
import com.dm.cn.common.utils.server.NetWork;
import com.dm.cn.common.utils.server.Server;
import com.dm.cn.device.service.SshdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DAMENG
 */
@ServerEndpoint("/ws/monitor")
@Component
public class DeviceMonitorWebSocketService {
    private static final Logger log = LoggerFactory.getLogger(DeviceMonitorWebSocketService.class);
    private static final AtomicInteger ON_LINE = new AtomicInteger(0);
    private static CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<Session>();
    private static CopyOnWriteArraySet<String> ipSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessionSet.add(session);
        int count = ON_LINE.incrementAndGet();
        log.info("current count：{}", count);
        String deviceIp = session.getQueryString().split("=")[2];
        ipSet.add(deviceIp);
        ThreadPoolManager.getInstance().execute(() -> {
                    while (sessionSet.size() > 0 && ipSet.contains(deviceIp)) {
                        try {
                            Server server = SpringUtils.getBean(SshdService.class).deviceMonitor(deviceIp);
                            NetWork netWork = server.getNetWork();
                            double bytesRecv = netWork.getBytesRecv();
                            double bytesSent = netWork.getBytesSent();
                            BigDecimal bg = BigDecimal.valueOf(server.getCpu().getUsed()).setScale(2, RoundingMode.HALF_UP);
                            double cpuRate = bg.doubleValue();
                            double memRate = server.getMem().getUsage();
                            double jvmRate = server.getJvm().getUsage();
                            double memUse = server.getMem().getUsed();
                            double jvmUse = server.getJvm().getUsed();
                            List<Object[]> cpuList = new ArrayList<Object[]>();
                            List<Object[]> jvmList = new ArrayList<Object[]>();
                            List<Object[]> memList = new ArrayList<Object[]>();
                            List<Object[]> inputList = new ArrayList<Object[]>();
                            List<Object[]> outputList = new ArrayList<Object[]>();

                            Date currentDate = new Date();
                            Object[] cpuItem = new Object[]{currentDate, cpuRate};
                            Object[] memItem = new Object[]{currentDate, memRate};
                            Object[] jvmItem = new Object[]{currentDate, jvmRate};
                            Object[] inputItem = new Object[]{currentDate, bytesRecv};
                            Object[] outputItem = new Object[]{currentDate, bytesSent};
                            cpuList.add(cpuItem);
                            memList.add(memItem);
                            jvmList.add(jvmItem);
                            outputList.add(outputItem);
                            inputList.add(inputItem);

                            Map<String, Object> result = new HashMap<String, Object>(8);
                            result.put("monitorCPU", cpuList);
                            result.put("server", server);
                            result.put("monitorMem", memList);
                            result.put("monitorJvm", jvmList);
                            result.put("monitorInput", inputList);
                            result.put("monitorOutput", outputList);
                            result.put("cpuUsage", cpuRate);
                            result.put("memUsage", memRate);
                            result.put("jvmUsage", jvmRate);
                            result.put("memUse", memUse);
                            result.put("jvmUse", jvmUse);
                            result.put("input", bytesRecv);
                            result.put("output", bytesSent);
                            broadCastMsg(result);
                            Thread.sleep(5000L);
                        } catch (Exception e) {
                            log.error("sendMessage websocket error：{}", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        );
    }

    @OnClose
    public static void onClose(Session session) {
        String deviceIp = session.getQueryString().split("=")[2];
        ipSet.remove(deviceIp);
        sessionSet.remove(session);
        log.info("current count：{}", ON_LINE.decrementAndGet());
    }

    @OnMessage
    public static void onMessage(String message, Session session) {
        log.info("receiveMessage{}", message, session.getId());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("websocket error：{}，Session ID： {}", error.getMessage(), session.getId());
    }

    /**
     * broadCast message
     *
     * @param result
     */
    private static void broadCastMsg(Map<String, Object> result) {
        for (Session session : sessionSet) {
            if (session.isOpen()) {
                WebSocketSendMsgUtil.sendMessage(result, session);
            }
        }
    }
}