package top.zbeboy.isy.glue.util

/**
 * Created by zbeboy 2017-10-31 .
 **/
class ResultUtils<T> {
    private var data: T? = null
    private var totalElements: Long = 0

    constructor()

    constructor(data: T?, totalElements: Long) {
        this.data = data
        this.totalElements = totalElements
    }

    fun data(data: T): ResultUtils<T> {
        this.data = data
        return this
    }

    fun totalElements(totalElements: Long): ResultUtils<T> {
        this.totalElements = totalElements
        return this
    }

    fun getData(): T? {
        return this.data
    }

    fun getTotalElements(): Long {
        return this.totalElements
    }
}