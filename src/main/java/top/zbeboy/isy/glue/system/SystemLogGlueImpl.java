package top.zbeboy.isy.glue.system;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository;
import top.zbeboy.isy.glue.plugin.ElasticPlugin;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.system.SystemLogService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017-03-27.
 */
@Slf4j
@Repository("systemLogGlue")
public class SystemLogGlueImpl extends ElasticPlugin<SystemLogBean> implements SystemLogGlue {

    @Resource
    private SystemLogService systemLogService;

    @Resource
    private SystemLogElasticRepository systemLogElasticRepository;

    @Override
    public ResultUtils<List<SystemLogBean>> findAllByPage(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<SystemLogBean>> resultUtils = ResultUtils.of();
        Page<SystemLogElastic> systemLogElasticPage = systemLogElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false));
        return resultUtils.data(dataBuilder(systemLogElasticPage)).totalElements(systemLogElasticPage.getTotalElements());
    }

    @Override
    public long countAll() {
        return systemLogElasticRepository.count();
    }

    /**
     * 构建新数据
     *
     * @param systemLogElasticPage 分页数据
     * @return 新数据
     */
    private List<SystemLogBean> dataBuilder(Page<SystemLogElastic> systemLogElasticPage) {
        List<SystemLogBean> systemLogs = new ArrayList<>();
        for (SystemLogElastic systemLogElastic : systemLogElasticPage.getContent()) {
            SystemLogBean systemLogBean = new SystemLogBean();
            systemLogBean.setSystemLogId(systemLogElastic.getSystemLogId());
            systemLogBean.setBehavior(systemLogElastic.getBehavior());
            systemLogBean.setOperatingTime(systemLogElastic.getOperatingTime());
            systemLogBean.setUsername(systemLogElastic.getUsername());
            systemLogBean.setIpAddress(systemLogElastic.getIpAddress());
            Date date = DateTimeUtils.timestampToDate(systemLogElastic.getOperatingTime());
            systemLogBean.setOperatingTimeNew(DateTimeUtils.formatDate(date));
            systemLogs.add(systemLogBean);
        }
        return systemLogs;
    }

    /**
     * 系统日志全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    @Override
    public QueryBuilder searchCondition(JSONObject search) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        if (!ObjectUtils.isEmpty(search)) {
            String username = StringUtils.trimWhitespace(search.getString("username"));
            String behavior = StringUtils.trimWhitespace(search.getString("behavior"));
            String ipAddress = StringUtils.trimWhitespace(search.getString("ipAddress"));
            if (StringUtils.hasLength(username)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("username", SQLQueryUtils.elasticLikeAllParam(username));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(behavior)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("behavior", behavior);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(ipAddress)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("ipAddress", SQLQueryUtils.elasticLikeAllParam(ipAddress));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }
        }
        return boolqueryBuilder;
    }

    /**
     * 系统日志排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    @Override
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<SystemLogBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_log_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("behavior".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("operating_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.ASC).unmappedType("long"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.DESC).unmappedType("long"));
                }
            }

            if ("ip_address".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC).unmappedType("string"));
                }
            }
        }
        return nativeSearchQueryBuilder;
    }
}
