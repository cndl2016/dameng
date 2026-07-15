package com.dm.cn.common.core.utils.file;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 文件类型工具类
 *
 * @author dameng
 */
public class FileTypeUtils {
    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(Objects.requireNonNull(file.getContentType()));
        }
        return extension;
    }

    /**
     * 获取文件类型
     *
     * @param photoByte 文件字节码
     * @return 后缀（不含".")
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "JPG";
        boolean gif = (photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97);
        boolean jpg = (photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70);
        boolean bmp = (photoByte[0] == 66) && (photoByte[1] == 77);
        boolean png = (photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71);
        if (gif) {
            strFileExtendName = "GIF";
        } else if (jpg) {
            strFileExtendName = "JPG";
        } else if (bmp) {
            strFileExtendName = "BMP";
        } else if (png) {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }
}
