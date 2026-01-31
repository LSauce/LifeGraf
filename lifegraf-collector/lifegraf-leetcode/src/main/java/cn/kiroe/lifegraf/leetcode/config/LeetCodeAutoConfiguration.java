package cn.kiroe.lifegraf.leetcode.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnProperty(prefix = "app.leetcode", name = "enabled", havingValue = "true")
@ComponentScan(basePackages = {
    "cn.kiroe.lifegraf.leetcode.**"
})
public class LeetCodeAutoConfiguration {
}
