package top.zbeboy.isy.web.bean.error

/**
 * Created by zbeboy 2017-12-19 .
 **/
open class ErrorBean<T> {
    var hasError: Boolean = false
    var errorMsg: String? = null
    var data: T? = null
    var mapData: Map<String, Any>? = null
    var listData: List<T>? = null

    companion object {
        @JvmStatic
        fun <T> of(): ErrorBean<T> {
            return ErrorBean()
        }
    }

    fun isHasError(): Boolean {
        return this.hasError
    }
}