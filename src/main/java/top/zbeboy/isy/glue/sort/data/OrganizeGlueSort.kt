package top.zbeboy.isy.glue.sort.data

import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

/**
 * Created by zbeboy 2017-12-03 .
 **/
open class OrganizeGlueSort {
    companion object {
        fun sortOrganizeId(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("organize_id".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortSchoolName(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortCollegeName(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortDepartmentName(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortScienceName(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortGrade(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("grade".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortOrganizeName(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.ASC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.DESC).unmappedType("string"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

        fun sortOrganizeIsDel(nativeSearchQueryBuilder: NativeSearchQueryBuilder, isAsc: Boolean, orderColumnName: String) {
            if ("organize_is_del".equals(orderColumnName, ignoreCase = true)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeIsDel").order(SortOrder.ASC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.ASC).unmappedType("string"))
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeIsDel").order(SortOrder.DESC).unmappedType("byte"))
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeId").order(SortOrder.DESC).unmappedType("string"))
                }
            }
        }

    }
}