package cn.kiroe.lifegraf.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.common.*",
})
public class CommonAutoConfiguration {
}
