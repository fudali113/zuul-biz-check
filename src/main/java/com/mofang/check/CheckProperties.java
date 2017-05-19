package com.mofang.check;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CheckProperties
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/4/24
 */
@Data
@ConfigurationProperties("zuul")
public class CheckProperties {

    /**
     * 检查规则映射
     */
    public Map<String, Check> routes = new HashMap<>();

    @Data
    public static class Check {
        private CheckRule check;
    }

    @Data
    public static class CheckRule {

        /**
         * 默认进行的检查策略
         * 当两者不存在时执行该策略
         */
        private List<String> Default = new ArrayList<>();

        /**
         * 需要进行检查的路由，value以`,`分割checker的type成数组
         * 需要排除一个checker使用`-{checkerType}`,排除所有使用`-*`
         * 添加一个checker使用`+{checkerType}`获取`{checkerType}`
         * 所有操作按逗号分割并按先后顺序执行
         */
        private Map<String, String> rules = new HashMap<>();

    }

    public boolean isEmpty() {
        return routes.isEmpty();
    }

}
