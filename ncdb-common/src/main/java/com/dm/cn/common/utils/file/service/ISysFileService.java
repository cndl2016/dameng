package com.dm.cn.common.utils.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * 文件api
 *
 * @author DAMENG
 * @date 2022/10/25
 */
public interface ISysFileService {

    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 访问地址
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    String uploadFile(MultipartFile file) throws IOException, InvalidKeyException, NoSuchAlgorithmException;

    /**
     * 文件删除
     *
     * @param fileNames
     * @return boolean
     */
    boolean removeFile(List<String> fileNames);

    /**
     * 单个文件删除
     *
     * @param path 文件路径
     * @return boolean
     */
    boolean removeFile(String path);

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return boolean
     */
    boolean fileIsExist(String path);

}