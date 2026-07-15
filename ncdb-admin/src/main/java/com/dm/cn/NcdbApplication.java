package com.dm.cn;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.dm.cn.common.core.utils.bean.DbTypeBean;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * 启动程序
 *
 * @author dameng.cn
 */

/**
 * 指定要扫描的Mapper类的包的路径
 *
 * @author DAMENG
 */
@EnableEncryptableProperties
@MapperScan("com.dm.cn.**.mapper")
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class NcdbApplication {
    private static final Logger log = LoggerFactory.getLogger(NcdbApplication.class);

    @Resource
    private DbTypeBean dbTypeBean;

    public static void main(String[] args) {
        SpringApplication.run(NcdbApplication.class, args);
        log.warn("(♥◠‿◠)ﾉﾞ  NCDB服务模块启动成功   ლ(´ڡ`ლ)ﾞ");
        log.warn("\n   _____  __  __        _   _  _____ _____  ____        _____ ______ _______      ________ _____  \n" +
                "  |  __ \\|  \\/  |      | \\ | |/ ____|  __ \\|  _ \\      / ____|  ____|  __ \\ \\    / /  ____|  __ \\ \n" +
                "  | |  | | \\  / |______|  \\| | |    | |  | | |_) |____| (___ | |__  | |__) \\ \\  / /| |__  | |__) |\n" +
                "  | |  | | |\\/| |______| . ` | |    | |  | |  _ <______\\___ \\|  __| |  _  / \\ \\/ / |  __| |  _  / \n" +
                "  | |__| | |  | |      | |\\  | |____| |__| | |_) |     ____) | |____| | \\ \\  \\  /  | |____| | \\ \\ \n" +
                "  |_____/|_|  |_|      |_| \\_|\\_____|_____/|____/     |_____/|______|_|  \\_\\  \\/   |______|_|  \\_\\");
    }

    @Bean
    public MybatisPlusInterceptor interceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(dbTypeBean.isH2() ? DbType.H2 : DbType.DM));
        return interceptor;
    }
}
