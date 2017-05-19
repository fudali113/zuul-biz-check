package com.mofang.check;

import com.netflix.zuul.context.RequestContext;

/**
 * CheckExToSimpleBodyString
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/5/20
 */
public class CheckExToSimpleBodyString implements CheckExceptionToResponseBodyString {
    @Override
    public String getResponseObject(CheckException ex) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulResponseHeader("Content-Type", "application/json;charset=UTF-8");
        return String.format("{\"code\":%d, \"errMsg\":\"%s\"}", ex.getBizCode(), ex.getMessage());
    }
}
