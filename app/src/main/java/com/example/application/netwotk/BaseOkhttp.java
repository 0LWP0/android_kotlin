package com.example.application.netwotk;

import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaseOkhttp {
    private static BaseOkhttp mBaseOkhttp;
    private final OkHttpClient client = new OkHttpClient();

    private static final int X_RESPONSE_CODE = 200;

    private String mUrl;

    private BaseInterface mBaseInterface;

    public static BaseOkhttp getInstance() {
        if (mBaseOkhttp == null) {
            synchronized (BaseOkhttp.class) {
                if (mBaseOkhttp == null) {
                    mBaseOkhttp = new BaseOkhttp();
                }
            }
        }
        return mBaseOkhttp;
    }

    public void loadUrl(String url) {
        this.mUrl = url;
    }

    public void loadData() {
        load(null);
    }

    public void loadData(RequestBody requestBody) {
        load(requestBody);
    }


    private void load(RequestBody requestBody) {
        if (TextUtils.isEmpty(mUrl)) throw new NullPointerException("发送网络请求，请调用loadUrl函数加载URL");
        final Request.Builder builder = new Request.Builder();
        if (null == requestBody) {
            builder.get();
        } else {
            builder.post(requestBody);
        }
        Request request = builder.url(mUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mBaseInterface.BaseErrorListener(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                String data = response.body().string();
                if (code == X_RESPONSE_CODE) {
                    mBaseInterface.BaseSuccessListener(data);
//                    if (TextUtils.isEmpty(data)){
//                        mBaseInterface.BaseSuccessListener(data);
//                    }else {
//                        mBaseInterface.BaseErrorListener();
//                    }

                } else {
                    mBaseInterface.BaseErrorListener(data);
                }
            }
        });
    }

    public void setBaseInterfaceListener(BaseInterface baseInterfaceListener) {
        this.mBaseInterface = baseInterfaceListener;
    }
}
