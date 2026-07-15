package com.dm.cn.common.utils.ip;

import com.dm.cn.common.core.constant.Constants;
import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.utils.ServletUtils;
import com.dm.cn.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取IP方法
 *
 * @author dameng.cn
 */
public class IpUtils {
    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);

    private final static String REGX_0_255 = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
    /**
     * 匹配 ip
     */
    private final static String REGX_IP = "((" + REGX_0_255 + "\\.){3}" + REGX_0_255 + ")";
    private final static String REGX_IP_WILDCARD = "(((\\*\\.){3}\\*)|(" + REGX_0_255 + "(\\.\\*){3})|(" + REGX_0_255 + "\\." + REGX_0_255 + ")(\\.\\*){2}" + "|((" + REGX_0_255 + "\\.){3}\\*))";
    /**
     * 匹配网段
     */
    private final static String REGX_IP_SEG = "(" + REGX_IP + "\\-" + REGX_IP + ")";

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    public static String getIpAddr() {
        return getIpAddr(ServletUtils.getRequest());
    }

    /**
     * 检查是否为内部IP地址
     *
     * @param ip IP地址
     * @return 结果
     */
    public static boolean internalIp(String ip) {
        byte[] addr = textToNumericFormatV4(ip);
        return internalIp(addr) || "127.0.0.1".equals(ip);
    }

    /**
     * 获取IP地址
     *
     * @return 本地IP地址
     */
    public static String getHostIp() {
        try {
            //从网卡中获取IP
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                //用于排除回送接口,非虚拟网卡,未在使用中的网络接口
                if (!netInterface.isLoopback() && !netInterface.isVirtual() && netInterface.isUp()) {
                    //返回和网络接口绑定的所有IP地址
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("IP地址获取失败" + e);
        }
        return "127.0.0.1";
    }

    /**
     * 获取主机名
     *
     * @return 本地主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("getHostName exception: {}", e);
        }
        return "未知";
    }


    /**
     * 校验ip是否符合过滤串规则
     *
     * @param filter 过滤IP列表,支持后缀'*'通配,支持网段如:`10.10.10.1-10.10.10.99`
     * @param ip     校验IP地址
     * @return boolean 结果
     */
    public static boolean isMatchedIp(String filter, String ip) {
        if (StringUtils.isEmpty(filter) || StringUtils.isEmpty(ip)) {
            return false;
        }
        String[] ips = filter.split(";");
        for (String iStr : ips) {
            if (isIp(iStr) && iStr.equals(ip)) {
                return true;
            } else if (isIpWildCard(iStr) && ipIsInWildCardNoCheck(iStr, ip)) {
                return true;
            } else if (isIpSegment(iStr) && ipIsInNetNoCheck(iStr, ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从字符串中提取ip及端口号
     *
     * @param nodes ip所在行集合
     * @return ip 端口号集合
     */
    public static List<String> getIpFromString(List<String> nodes) {
        List<String> ips = new LinkedList<>();
        nodes.stream().forEach(node -> {
            String[] split = node.split(" ");
            ips.add(split[3] + " " + split[4]);
        });
        return ips;
    }

    /**
     * 比较ip大小 host1大于host2返回1,小于返回-1,相等返回0
     *
     * @param host1
     * @param host2
     * @return int
     */
    public static int compareIp(String host1, String host2) {
        String[] arr1 = host1.split(SymbolConstants.IP_SPLIT_REGEX);
        String[] arr2 = host2.split(SymbolConstants.IP_SPLIT_REGEX);

        for (int i = 0; i < Math.min(arr1.length, arr2.length); i++) {
            if (Integer.parseInt(arr1[i]) != Integer.parseInt(arr2[i])) {
                return Integer.compare(Integer.parseInt(arr1[i]), Integer.parseInt(arr2[i]));
            }
        }

        return Integer.compare(arr1.length, arr2.length);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    private static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(SymbolConstants.COMMA) > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (false == isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return StringUtils.substring(ip, 0, 255);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    private static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 是否为IP
     */
    private static boolean isIp(String ip) {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP);
    }

    /**
     * 是否为IP，或 *为间隔的通配符地址
     */
    private static boolean isIpWildCard(String ip) {
        return StringUtils.isNotBlank(ip) && ip.matches(REGX_IP_WILDCARD);
    }

    /**
     * 检测参数是否在ip通配符里
     */
    private static boolean ipIsInWildCardNoCheck(String ipWildCard, String ip) {
        String[] s1 = ipWildCard.split("\\.");
        String[] s2 = ip.split("\\.");
        boolean isMatchedSeg = true;
        for (int i = 0; i < s1.length && !SymbolConstants.ASTERISK.equals(s1[i]); i++) {
            if (!s1[i].equals(s2[i])) {
                isMatchedSeg = false;
                break;
            }
        }
        return isMatchedSeg;
    }

    /**
     * 是否为特定格式如:“10.10.10.1-10.10.10.99”的ip段字符串
     */
    private static boolean isIpSegment(String ipSeg) {
        return StringUtils.isNotBlank(ipSeg) && ipSeg.matches(REGX_IP_SEG);
    }

    /**
     * 判断ip是否在指定网段中
     */
    private static boolean ipIsInNetNoCheck(String iparea, String ip) {
        int idx = iparea.indexOf('-');
        String[] sips = iparea.substring(0, idx).split("\\.");
        String[] sipe = iparea.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < NumberConstants.FOUR; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    private static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || Constants.UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }


    /**
     * 检查是否为内部IP地址
     *
     * @param addr byte地址
     * @return 结果
     */
    private static boolean internalIp(byte[] addr) {
        if(addr == null){
            return false;
        }
        if (StringUtils.isNull(addr) || addr.length < NumberConstants.TWO) {
            return true;
        }
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        // 10.x.x.x/8
        final byte section1 = 0x0A;
        // 172.16.x.x/12
        final byte section2 = (byte) 0xAC;
        final byte section3 = (byte) 0x10;
        final byte section4 = (byte) 0x1F;
        // 192.168.x.x/16
        final byte section5 = (byte) 0xC0;
        final byte section6 = (byte) 0xA8;
        switch (b0) {
            case section1:
                return true;
            case section2:
                if (b1 >= section3 && b1 <= section4) {
                    return true;
                }
            case section5:
                switch (b1) {
                    case section6:
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    private static byte[] textToNumericFormatV4(String text) {
        if (text.length() == 0) {
            return null;
        }
        byte[] bytes = new byte[4];
        String[] elements = text.split("\\.", -1);
        try {
            long l;
            int i;
            switch (elements.length) {
                case 1:
                    l = Long.parseLong(elements[0]);
                    long number1 = 4294967295L;
                    if ((l < 0L) || (l > number1)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l >> 24 & 0xFF);
                    bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 2:
                    l = Integer.parseInt(elements[0]);
                    long number2 = 255L;
                    if ((l < 0L) || (l > number2)) {
                        return null;
                    }
                    bytes[0] = (byte) (int) (l & 0xFF);
                    l = Integer.parseInt(elements[1]);
                    long number3 = 16777215L;
                    if ((l < 0L) || (l > number3)) {
                        return null;
                    }
                    bytes[1] = (byte) (int) (l >> 16 & 0xFF);
                    bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 3:
                    for (i = 0; i < NumberConstants.TWO; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    l = Integer.parseInt(elements[2]);
                    long number4 = 65535L;
                    if ((l < 0L) || (l > number4)) {
                        return null;
                    }
                    bytes[2] = (byte) (int) (l >> 8 & 0xFF);
                    bytes[3] = (byte) (int) (l & 0xFF);
                    break;
                case 4:
                    for (i = 0; i < NumberConstants.FOUR; ++i) {
                        l = Integer.parseInt(elements[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        bytes[i] = (byte) (int) (l & 0xFF);
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            log.error("textToNumericFormatV4 exception: {}", e);
            return null;
        }
        return bytes;
    }
}
