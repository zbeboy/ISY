package top.zbeboy.isy.web.bean.tree

/**
 * Created by zbeboy 2017-11-17 .
 **/
open class TreeBean {
    var text: String? = null
    var nodes: List<TreeBean>? = null
    var dataId: String? = null

    constructor()

    constructor(text: String?, nodes: List<TreeBean>?, dataId: String?) {
        this.text = text
        this.nodes = nodes
        this.dataId = dataId
    }
}