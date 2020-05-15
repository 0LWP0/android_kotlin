package com.example.application.netwotk;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.RequestBody;

public abstract class BasePresenter<T> {

    private BaseOkhttp baseOkhttp;

    private Class<T> def;

    public BasePresenter() {
        baseOkhttp = BaseOkhttp.getInstance();
        baseOkhttp.setBaseInterfaceListener(baseInterface);
        Class c = getClass();
        Type type = c.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            this.def = (Class<T>) types[0];
        }
    }

    /**
     * @param url 请求URL
     * @deprecated
     */
    public void setUrl(String url) {
        baseOkhttp.loadUrl(url);
    }

    /**
     * @deprecated 发送get请求
     */
    public void sendResponse() {
        baseOkhttp.loadData();
    }

    /**
     * @deprecated 发送post请求
     */
    public void sendResponse(RequestBody requestBody) {
        baseOkhttp.loadData(requestBody);
    }

    /**
     * @deprecated 网络请求数据统一回调处理
     */
    private BaseInterface baseInterface = new BaseInterface() {
        @Override
        public void BaseSuccessListener(String data) {
            //请求成功
            loadSuccessData(JSON.parseObject(data, def));
        }

        @Override
        public void BaseErrorListener(String info) {
            //请求失败
            loadErrorData();
        }
    };

    //数据请求成功
    abstract void loadSuccessData(T t);

    //数据请求失败
    abstract void loadErrorData();

}
