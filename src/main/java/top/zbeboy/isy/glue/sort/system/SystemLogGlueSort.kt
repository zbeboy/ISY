package top.zbeboy.isy.glue.sort.system

import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

/**
 * Created by zbeboy 2017-11-13 .
 **/
open class SystemLogGlueSort {

    companion object {

        fun sortSystemLogId(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("system_log_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortUsername(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("username".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortBehavior(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("behavior".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortOperatingTime(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("operating_time".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.ASC).unmappedType("long"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.DESC).unmappedType("long"))
                }
            }
        }

        fun sortIpAddress(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("ip_address".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

    }
}