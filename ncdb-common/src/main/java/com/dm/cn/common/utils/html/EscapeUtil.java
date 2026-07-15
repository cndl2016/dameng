package com.dm.cn.common.utils.html;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.utils.StringUtils;

/**
 * 转义和反转义工具类
 *
 * @author dameng.cn
 */
public class EscapeUtil {

    private static final char[][] TEXT = new char[64][];

    static {
        for (int i = 0; i < NumberConstants.SIXTY_FOUR; i++) {
            TEXT[i] = new char[]{(char) i};
        }
        // special HTML characters
        // 单引号
        TEXT['\''] = "&#039;".toCharArray();
        // 双引号
        TEXT['"'] = "&#34;".toCharArray();
        // &符
        TEXT['&'] = "&#38;".toCharArray();
        // 小于号
        TEXT['<'] = "&#60;".toCharArray();
        // 大于号
        TEXT['>'] = "&#62;".toCharArray();
    }

    /**
     * 清除所有HTML标签，但是不删除标签内的内容
     *
     * @param content 文本
     * @return 清除标签后的文本
     */
    public static String clean(String content) {
        return new HtmlFilter().filter(content);
    }

    /**
     * Escape解码
     *
     * @param content 被转义的内容
     * @return 解码后的字符串
     */
    public static String decode(String content) {
        if (StringUtils.isEmpty(content)) {
            return content;
        }

        StringBuilder tmp = new StringBuilder(content.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < content.length()) {
            pos = content.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (content.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(content.substring(lastPos));
                    lastPos = content.length();
                } else {
                    tmp.append(content.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

}
