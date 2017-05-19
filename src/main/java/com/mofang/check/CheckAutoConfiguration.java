package com.mofang.check;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * CheckAotuConfiguration
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/5/20
 */
@Configuration
public class CheckAutoConfiguration {

    @Bean
    public BusinessVerifyFilter getProfessionalVerifyFilter(CheckManager checkManager,
                                                            RouteLocator routeLocator,
                                                            ZuulProperties properties,
                                                            CheckExceptionToResponseObject
                                                                        checkExceptionToResponseObject) {
        return new BusinessVerifyFilter(
                checkManager,
                routeLocator,
                properties,
                checkExceptionToResponseObject);
    }

    @Bean
    public CheckManager getCheckManager(CheckProperties checkProperties,
                                        CheckPropertiesFetcher fetcher,
                                        List<BusinessChecker> checkers) {
        return new CheckManager(checkProperties, fetcher, checkers);
    }

}
