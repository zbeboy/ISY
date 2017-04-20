package top.zbeboy.isy.glue.system;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic;
import top.zbeboy.isy.elastic.repository.SystemSmsElasticRepository;
import top.zbeboy.isy.glue.plugin.ElasticPlugin;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.glue.util.SearchUtils;
import top.zbeboy.isy.service.system.SystemSmsService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017-04-08.
 */
@Repository("systemSmsGlue")
public class SystemSmsGlueImpl extends ElasticPlugin<SystemSmsBean> implements SystemSmsGlue {

    private final Logger log = LoggerFactory.getLogger(SystemLogGlueImpl.class);

    @Resource
    private SystemSmsService systemSmsService;

    @Resource
    private SystemSmsElasticRepository systemSmsElasticRepository;

    @Override
    public ResultUtils<List<SystemSmsBean>> findAllByPage(DataTablesUtils<SystemSmsBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<SystemSmsBean>> resultUtils = new ResultUtils<>();
        Page<SystemSmsElastic> systemSmsElasticPage = systemSmsElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false));
        return resultUtils.data(dataBuilder(systemSmsElasticPage)).totalElements(systemSmsElasticPage.getTotalElements());
    }

    @Override
    public long countAll() {
        return systemSmsElasticRepository.count();
    }

    /**
     * 构建新数据
     *
     * @param systemSmsElasticPage 分页数据
     * @return 新数据
     */
    private List<SystemSmsBean> dataBuilder(Page<SystemSmsElastic> systemSmsElasticPage) {
        List<SystemSmsBean> systemSmses = new ArrayList<>();
        for (SystemSmsElastic systemSmsElastic : systemSmsElasticPage.getContent()) {
            SystemSmsBean systemSmsBean = new SystemSmsBean();
            systemSmsBean.setSystemSmsId(systemSmsElastic.getSystemSmsId());
            systemSmsBean.setSendTime(systemSmsElastic.getSendTime());
            systemSmsBean.setAcceptPhone(systemSmsElastic.getAcceptPhone());
            systemSmsBean.setSendCondition(systemSmsElastic.getSendCondition());
            Date date = DateTimeUtils.timestampToDate(systemSmsElastic.getSendTime());
            systemSmsBean.setSendTimeNew(DateTimeUtils.formatDate(date));
            systemSmses.add(systemSmsBean);
        }
        return systemSmses;
    }

    /**
     * 系统短信全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    @Override
    public QueryBuilder searchCondition(JSONObject search) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        if (!ObjectUtils.isEmpty(search)) {
            String acceptPhone = StringUtils.trimWhitespace(search.getString("acceptPhone"));
            if (StringUtils.hasLength(acceptPhone)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("acceptPhone", SQLQueryUtils.elasticLikeAllParam(acceptPhone));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }
        }
        return boolqueryBuilder;
    }

    /**
     * 系统短信排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    @Override
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_sms_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("send_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.ASC).unmappedType("long"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.DESC).unmappedType("long"));
                }
            }

            if ("accept_phone".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptPhone").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("send_condition".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemSmsId").order(SortOrder.DESC).unmappedType("string"));
                }
            }
        }
        return nativeSearchQueryBuilder;
    }
}
