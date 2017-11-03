package top.zbeboy.isy.web.util

/**
 * Created by zbeboy 2017-11-03 .
 * ajax消息以及数据封装
 **/
class AjaxUtils<T> {

    private var state: Boolean = false//消息状态
    private var msg: String? = null//消息
    private var mapResult: Map<String, Any>? = null//map数据
    private var listResult: List<T>? = null//list数据
    private var objectResult: Any? = null//单个对象数据
    private var paginationUtils: PaginationUtils? = null//分页数据

    companion object {
        @JvmStatic
        fun <T> of(): AjaxUtils<T> {
            return AjaxUtils<T>()
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

    fun isState(): Boolean {
        return state
    }

    fun setState(state: Boolean) {
        this.state = state
    }

    fun getPaginationUtils(): PaginationUtils? {
        return paginationUtils
    }

    fun setPaginationUtils(paginationUtils: PaginationUtils) {
        this.paginationUtils = paginationUtils
    }

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String) {
        this.msg = msg
    }

    fun getObjectResult(): Any? {
        return objectResult
    }

    fun setObjectResult(obj: Any) {
        this.objectResult = obj
    }

    fun getMapResult(): Map<String, Any>? {
        return mapResult
    }

    fun setMapResult(mapResult: Map<String, Any>) {
        this.mapResult = mapResult
    }

    fun getListResult(): List<T>? {
        return listResult
    }

    fun setListResult(listResult: List<T>) {
        this.listResult = listResult
    }
}