package com.m_learning.network.utils;


public interface StringRequestListener<T> {

    void onSuccess(String message, T data);

    void onFail(String message);
}
