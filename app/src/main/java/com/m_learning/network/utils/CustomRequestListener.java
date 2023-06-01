package com.m_learning.network.utils;


public interface CustomRequestListener<T> {

    void onSuccess( T data);
//    void onSuccess(int totalPages, T data, String msg);

    void onFail(String message, int code);
}
