package com.dm.cn.common.core.utils.file;

import com.dm.cn.common.core.constant.NumberConstants;
import com.dm.cn.common.core.constant.SymbolConstants;
import com.dm.cn.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件处理工具类
 *
 * @author dameng
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 字符常量：斜杠 {@code '/'}
     */
    public static final char SLASH = '/';

    /**
     * 字符常量：反斜杠 {@code '\\'}
     */
    public static final char BACKSLASH = '\\';

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            log.error("writeBytes exception: {}", e);
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    log.error("writeBytes exception: {}", e1);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    log.error("writeBytes exception: {}", e1);
                }
            }
        }
    }

    /**
     * 删除文件、目录及子级文件
     *
     * @param dir
     * @return
     */
    public static boolean deleteDir(String dir) {
        File file = new File(dir);
        boolean delete;
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children.length > 0) {
                /**递归删除目录中的子目录下*/
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(file.getPath() + "/" + children[i]);
                    if (!success) {
                        return false;
                    }
                }
            }
            delete = file.delete();
        } else {
            delete = file.delete();
        }
        return delete;
    }

    /**
     * 匹配模式删除文件
     *
     * @param dir         目录
     * @param filePattern 文件模式（*）
     * @return boolean
     */
    public static boolean deletePatternFiles(String dir, String filePattern) {
        File fileDir = new File(dir);
        boolean delete = true;
        // 校验目录是否有效
        if (StringUtils.isBlank(dir) || !fileDir.exists() || !fileDir.isDirectory()) {
            return false;
        }
        File[] files = fileDir.listFiles();
        if (files.length > NumberConstants.ZERO) {
            for (File file : files) {
                // 匹配格式，符合则删除
                if (!file.isDirectory() && file.getName().startsWith(filePattern.replace(SymbolConstants.ASTERISK, SymbolConstants.BLANK))) {
                    delete = file.delete() && delete;
                }
            }
        }

        return delete;
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }
}
