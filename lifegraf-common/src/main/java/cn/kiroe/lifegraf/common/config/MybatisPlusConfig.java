package cn.kiroe.lifegraf.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus configuration for the application.
 * Scans all mapper interfaces across all modules.
 */
@Configuration
@MapperScan("cn.kiroe.lifegraf.**.mapper")
public class MybatisPlusConfig {
}
