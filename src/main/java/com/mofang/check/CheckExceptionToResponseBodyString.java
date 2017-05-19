package com.mofang.check;

/**
 * CheckExceptionToResponseBodyString
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/5/20
 */
@FunctionalInterface
public interface CheckExceptionToResponseBodyString {
    String getResponseObject(CheckException ex);
}
