package com.example.application.netwotk

import android.text.TextUtils
import android.util.Log
import com.example.application.netwotk.interfaces.BaseInterface
import okhttp3.*
import java.io.IOException

class BaseOkhttp {
    private var mUrl: String? = null
    private var mBaseInterface: BaseInterface? = null
    fun loadUrl(url: String?) {
        mUrl = url
    }

    fun loadData(map: Map<String, Any>?) {
        loadGet(map)
    }

    fun loadData(requestBody: RequestBody?) {
        loadPost(requestBody)
    }

    private fun loadGet(query: Map<String, Any>?) {
        if (TextUtils.isEmpty(mUrl)) throw NullPointerException("发送网络请求，请调用loadUrl函数加载URL")
        val builder = Request.Builder()
        val request = builder.url(mUrl!!).build()
        if (query != null) {
            val urlBuilder = request.url.newBuilder()
            for ((key, value) in query) {
                urlBuilder.addQueryParameter(key, value as String)
            }
        }
        val call = client.newCall(request)
        call.enqueue(callback)
    }

    private fun loadPost(requestBody: RequestBody?) {
        if (TextUtils.isEmpty(mUrl)) throw NullPointerException("发送网络请求，请调用loadUrl函数加载URL")
        val builder = Request.Builder()
        if (null != requestBody) {
            builder.post(requestBody)
        }
        val request = builder.url(mUrl!!).build()
        val call = client.newCall(request)
        call.enqueue(callback)
    }

    private val callback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            mBaseInterface!!.BaseErrorListener(e.message)
        }

        @Throws(IOException::class)
        override fun onResponse(
            call: Call,
            response: Response
        ) {
            val data = response.body!!.string()
            Log.i("onResponse", data)
            if (response.isSuccessful) {
                mBaseInterface!!.BaseSuccessListener(data)
                //                    if (TextUtils.isEmpty(data)){
//                        mBaseInterface.BaseSuccessListener(data);
//                    }else {
//                        mBaseInterface.BaseErrorListener();
//                    }
            } else {
                val code = response.code
                mBaseInterface!!.BaseErrorListener(data)
            }
        }
    }

    fun setBaseInterfaceListener(baseInterfaceListener: BaseInterface?) {
        mBaseInterface = baseInterfaceListener
    }

    companion object {
        private var mBaseOkhttp: BaseOkhttp? = null
        private val client = OkHttpClient()
        private const val X_RESPONSE_CODE = 200
        @JvmStatic
        val instance: BaseOkhttp?
            get() {
                if (mBaseOkhttp == null) {
                    synchronized(BaseOkhttp::class.java) {
                        if (mBaseOkhttp == null) {
                            mBaseOkhttp = BaseOkhttp()
                        }
                    }
                }
                return mBaseOkhttp
            }
    }
}