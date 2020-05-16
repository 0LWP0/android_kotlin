package com.example.application.netwotk.presenters

import com.alibaba.fastjson.JSON
import com.example.application.netwotk.BaseOkhttp
import com.example.application.netwotk.BaseOkhttp.Companion.instance
import com.example.application.netwotk.interfaces.BaseInterface
import okhttp3.RequestBody
import java.lang.reflect.ParameterizedType

abstract class BasePresenter<T> {
    private val baseOkhttp: BaseOkhttp = instance!!

    private var def: Class<T>? = null

    /**
     * @param url 请求URL
     */
    fun setUrl(url: String?) {
        baseOkhttp.loadUrl(url)
    }

    /**
     * @param  map 发送get请求
     */
    fun sendResponse(map: Map<String, Any>?) {
        baseOkhttp.loadData(map)
    }

    /**
     * 发送post请求
     */
    fun sendResponse(requestBody: RequestBody?) {
        baseOkhttp.loadData(requestBody)
    }

    /**
     * 网络请求数据统一回调处理
     */
    private val baseInterface: BaseInterface = object : BaseInterface {
        override fun BaseSuccessListener(data: String?) {
            //请求成功
            loadSuccessData(JSON.parseObject(data, def))
        }

        override fun BaseErrorListener(info: String?) {
            //请求失败
            loadErrorData()
        }
    }

    //数据请求成功
    abstract fun loadSuccessData(t: T)

    //数据请求失败
    abstract fun loadErrorData()

    init {
        baseOkhttp.setBaseInterfaceListener(baseInterface)
        val c: Class<*> = javaClass
        val type = c.genericSuperclass
        if (type is ParameterizedType) {
            val types =
                type.actualTypeArguments
            def = types[0] as Class<T>
        }
    }
}