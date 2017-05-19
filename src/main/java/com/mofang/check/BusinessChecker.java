package com.mofang.check;

/**
 * BusinessChecker
 * <p>
 * 业务相关过滤器处理器
 *
 * @author doob[fudali113@gmail.com]
 * @date 17-4-21上午10:55
 */
public interface BusinessChecker extends Comparable<BusinessChecker> {

    /**
     * 判断一个请求是否通过业务验证
     *
     * @param checkContext
     * @return
     */
    boolean check(CheckContext checkContext) throws CheckException;

    /**
     * 排序
     *
     * @return
     */
    int order();

    /**
     * 该检查的type，用于指定规则是指定
     * 不同checker尽量不要相同，否则可能导致bug
     *
     * @return
     */
    String type();

    default int compareTo(BusinessChecker checker) {
        return this.order() - checker.order();
    }

}
