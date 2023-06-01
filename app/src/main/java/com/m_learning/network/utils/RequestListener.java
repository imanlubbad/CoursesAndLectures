package com.m_learning.network.utils;


public interface RequestListener<T> {

    void onSuccess(T data);

    void onFail(String message);
}
