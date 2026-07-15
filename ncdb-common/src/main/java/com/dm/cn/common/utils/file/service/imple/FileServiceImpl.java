package com.dm.cn.common.utils.file.service.imple;

import com.dm.cn.common.core.utils.file.FileUtils;
import com.dm.cn.common.utils.file.service.ISysFileService;
import com.dm.cn.common.utils.file.util.FileUploadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author DAMENG
 * @date 2022/08/11
 */
@Service("fileService")
public class FileServiceImpl implements ISysFileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return FileUploadUtils.upload(file);
    }

    @Override
    public boolean removeFile(List<String> fileNames) {
        boolean result = true;
        try {
            for (String path : fileNames) {
                result = result && FileUtils.deleteDir(path);
            }
        } catch (Exception e) {
            log.error("removeFile {}", e);
            return false;
        }
        return result;
    }

    @Override
    public boolean removeFile(String path) {
        return removeFile(Collections.singletonList(path));
    }

    @Override
    public boolean fileIsExist(String path) {
        File file = new File(path);
        return file.exists();
    }

}