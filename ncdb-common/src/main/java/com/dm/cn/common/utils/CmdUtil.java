package com.dm.cn.common.utils;

import com.dm.cn.common.core.constant.*;
import com.dm.cn.common.core.exception.ServiceException;
import com.dm.cn.common.core.utils.StringUtils;
import com.dm.cn.common.enums.EncodeModeEnum;
import com.dm.cn.common.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

/**
 * 执行windows命令
 *
 * @author DAMENG
 */
public class CmdUtil {

    private static final Logger log = LoggerFactory.getLogger(CmdUtil.class);


    /**
     * 获取操作系统默认语言
     */
    public static String getSystemLanguage() {
        return null == System.getProperty("sun.jnu.encoding") ? "GBK"
                : System.getProperty("sun.jnu.encoding");
    }

    /**
     * 执行单条命令
     *
     * @param cmd
     * @return
     */
    public static String executeCmd(String cmd) {
        try {
            String cmdBin = "cmd /c ";
            Process process = Runtime.getRuntime().exec(cmdBin + cmd);
            String result = getExecResult(process);
            return result;
        } catch (Exception e) {
            log.error("executeCmd exception: {}", e);
            throw new RuntimeException(e);
        }
    }

    private static String getExecResult(Process process) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName(getSystemLanguage())));
        StringJoiner result = new StringJoiner(SymbolConstants.WRAP);
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        return result.toString();
    }

    private static String getExecErrResult(Process process) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.forName(getSystemLanguage())));
        StringJoiner result = new StringJoiner(SymbolConstants.WRAP);
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        return result.toString();
    }

    public static String execute(String cmd) {
        Process process;
        String result;
        log.info("execute:{}", handlePassCmd(cmd));
        try {
            if (SecurityUtils.isWin()) {
                process = Runtime.getRuntime().exec(cmd);
            } else {
                process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", cmd});
            }
            result = getExecResult(process);
            if (StringUtils.isEmpty(result)) {
                return getExecErrResult(process);
            }
            log.info("execute result:{}", result);
            return result;
        } catch (IOException e) {
            log.error("execute exception: {}", e);
            throw new RuntimeException(e);
        }
    }

    public static String callShellByExec(String shellPath, String[] params) {
        String exec = Constants.BASH_SPACE + shellPath + Constants.BLANK_SPACE + String.join(Constants.BLANK_SPACE, params);
        String result = execute(exec);
        log.info("@@@@@@CmdUtil callShellByExec Common {} result {}", exec, result);
        return result;
    }

    /**
     * 处理带密码命令
     * */
    public static String handlePassCmd(String cmd){
        // 命令中携带密码时对命令进行截取
        if (cmd.contains(CommandConstants.COMMAND_REDIS_CLI_PASS)){
            return cmd.substring(cmd.indexOf(SymbolConstants.AND_AND));
        }
        return cmd;
    }

    /**
     * 密码加解密
     * */
    public static String handleEncodePass(String pass, String mode){
        if (StringUtils.isBlank(pass)) {
            return pass;
        }
        if (EncodeModeEnum.ENCODE.name().equals(mode)) {
            return new String(Base64.getEncoder().encode(pass.getBytes()));
        } else {
            return new String(Base64.getDecoder().decode(pass));
        }
    }

    /**
     * 生成解压指令
     *
     * @param localPath  安装包路径
     * @param packageDir 指定的安装部署路径
     * @param versionNum 版本号
     * @return {@link String}
     */
    public static String getDecompressCmd(String localPath, String packageDir, String versionNum) {
        String decompressCommand;
        //根据安装包格式 执行不同的解压命令
        if (localPath.endsWith(FileConstants.TAR_GZ_FILE_TYPE)) {
            List<String> cmd = new ArrayList<>();
            // 解压tar包
            cmd.add(String.format(CommandConstants.TAR_ZXVF, packageDir, localPath));
            // 修改安装包所有者为root
            cmd.add(String.format(CommandConstants.CHOWN, packageDir + SymbolConstants.SLASH + versionNum));
            // 修改安装包文件权限
            cmd.add(String.format(CommandConstants.CHMOD_R_777, packageDir + SymbolConstants.SLASH + versionNum));
            decompressCommand = String.join(SymbolConstants.AND_AND, cmd);
        } else {
            throw new ServiceException(MgConstants.UNKNOWN_FILE_TYPE);
        }
        return decompressCommand;
    }

}
