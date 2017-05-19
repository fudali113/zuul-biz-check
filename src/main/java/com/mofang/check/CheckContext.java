package com.mofang.check;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CheckContext
 *
 * @author doob[fudali113@gmail.com]
 * @date 17-4-24下午6:13
 */
public class CheckContext extends ConcurrentHashMap<String, Object> {
    private static ThreadLocal<CheckContext> checkContextThreadLocal = new ThreadLocal() {
        protected CheckContext initialValue() {
            return new CheckContext();
        }
    };

    public static CheckContext initCheckContext(Map map) {
        CheckContext checkContext = checkContextThreadLocal.get();
        checkContext.putAll(map);
        return checkContext;
    }

    public static void removeCCTL() {
        checkContextThreadLocal.remove();
    }

    public static CheckContext getCurrentContext() {
        return checkContextThreadLocal.get();
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) getCurrentContext().get("request");
    }

    public void set(String k, Object v) {
        this.put(k, v);
    }
}
