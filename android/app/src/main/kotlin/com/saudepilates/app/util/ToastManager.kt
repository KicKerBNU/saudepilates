package com.saudepilates.app.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ToastType { SUCCESS, ERROR, WARNING, INFO }

data class ToastMessage(val message: String, val type: ToastType = ToastType.SUCCESS)

object ToastManager {
    private val _toast = MutableStateFlow<ToastMessage?>(null)
    val toast: StateFlow<ToastMessage?> = _toast.asStateFlow()

    fun success(message: String) { show(message, ToastType.SUCCESS) }
    fun error(message: String) { show(message, ToastType.ERROR) }
    fun warning(message: String) { show(message, ToastType.WARNING) }
    fun info(message: String) { show(message, ToastType.INFO) }
    fun clear() { _toast.value = null }

    private fun show(message: String, type: ToastType) {
        _toast.value = ToastMessage(message, type)
    }
}
