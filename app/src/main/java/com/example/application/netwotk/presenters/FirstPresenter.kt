package com.example.application.netwotk.presenters

import com.example.application.netwotk.beans.FirstBean

class FirstPresenter : BasePresenter<FirstBean?>() {
    override fun loadSuccessData(t: FirstBean?) {}
    override fun loadErrorData() {}
}