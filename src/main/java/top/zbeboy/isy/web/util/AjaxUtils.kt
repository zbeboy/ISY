package top.zbeboy.isy.web.util

/**
 * Created by zbeboy 2017-11-03 .
 * ajax消息以及数据封装
 **/
open class AjaxUtils<T> {

    var state: Boolean = false//消息状态
    var msg: String? = null//消息
    var mapResult: Map<String, Any>? = null//map数据
    var listResult: List<T>? = null//list数据
    var objectResult: Any? = null//单个对象数据
    var paginationUtils: PaginationUtils? = null//分页数据

    companion object {
        @JvmStatic
        fun <T> of(): AjaxUtils<T> {
            return AjaxUtils()
        }
    }

    fun success(): AjaxUtils<T> {
        this.state = true
        return this
    }

    fun fail(): AjaxUtils<T> {
        this.state = false
        return this
    }

    fun msg(msg: String): AjaxUtils<T> {
        this.msg = msg
        return this
    }

    fun obj(obj: Any): AjaxUtils<T> {
        this.objectResult = obj
        return this
    }

    fun mapData(map: Map<String, Any>): AjaxUtils<T> {
        this.mapResult = map
        return this
    }

    fun listData(list: List<T>): AjaxUtils<T> {
        this.listResult = list
        return this
    }

    fun paginationUtils(paginationUtils: PaginationUtils): AjaxUtils<T> {
        this.paginationUtils = paginationUtils
        return this
    }
}