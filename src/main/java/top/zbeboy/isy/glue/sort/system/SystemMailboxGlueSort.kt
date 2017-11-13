package top.zbeboy.isy.glue.sort.system

import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

/**
 * Created by zbeboy 2017-11-13 .
 **/
open class SystemMailboxGlueSort {

    companion object {
        fun sortSystemMailboxId(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("system_mailbox_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
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

        fun sortAcceptMail(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("accept_mail".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortSendCondition(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("send_condition".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }
    }
}