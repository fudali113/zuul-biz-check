package com.mofang.check;

/**
 * CheckExceptionToResponseObject
 *
 * @author doob  fudali113@gmail.com
 * @date 2017/5/20
 */
@FunctionalInterface
public interface CheckExceptionToResponseObject {
    Object getResponseObject(CheckException ex);
}
