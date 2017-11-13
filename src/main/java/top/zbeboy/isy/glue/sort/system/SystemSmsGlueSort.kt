package top.zbeboy.isy.glue.sort.system

import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

/**
 * Created by zbeboy 2017-11-13 .
 **/
open class SystemSmsGlueSort {

    companion object {
        fun sortSystemSmsId(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("system_sms_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortSendTime(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("send_time".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.ASC).unmappedType("long"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.DESC).unmappedType("long"))
                }
            }
        }

        fun sortAcceptPhone(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("accept_phone".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortSendCondition(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("send_condition".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

    }
}