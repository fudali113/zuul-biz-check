package com.mofang.check;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.web.util.UrlPathHelper;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * BusinessVerifyFilter
 * <p>
 * 处理业务相关的过滤器，所有业务相关过滤器实现在此类中运行并处理
 * 集中处理方便管理与错误处理
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/4/23
 */
public class BusinessVerifyFilter extends ZuulFilter {

    private CheckManager checkManager;
    private RouteLocator routeLocator;
    private CheckExceptionToResponseObject checkExceptionToResponseObject;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public BusinessVerifyFilter(CheckManager checkManager,
                                RouteLocator routeLocator,
                                ZuulProperties properties,
                                CheckExceptionToResponseObject checkExceptionToResponseObject) {
        this.checkManager = checkManager;
        this.routeLocator = routeLocator;
        this.checkExceptionToResponseObject = checkExceptionToResponseObject;
        this.urlPathHelper.setRemoveSemicolonContent(properties.isRemoveSemicolonContent());
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        CheckContext checkContext = CheckContext.initCheckContext(ctx);
        String uri = this.urlPathHelper.getRequestUri(ctx.getRequest());
        Route route = this.routeLocator.getMatchingRoute(uri);
        List<BusinessChecker> checkers = checkManager.matchCheckers(route.getId(), route.getPath());
        for (BusinessChecker checker : checkers) {
            try {
                checker.check(checkContext);
            } catch (CheckException e) {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(e.getHttpCode());
                ctx.setResponseBody(
                        JSON.toJSONString(checkExceptionToResponseObject.getResponseObject(e))
                );
                ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
                break;
            }
        }
        ctx.putAll(checkContext);
        CheckContext.removeCCTL();
        return null;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -100;
    }
}
