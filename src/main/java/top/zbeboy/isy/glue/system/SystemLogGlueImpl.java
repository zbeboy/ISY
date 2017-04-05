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
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository;
import top.zbeboy.isy.glue.plugin.ElasticPlugin;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.glue.util.SearchUtils;
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
@Repository("systemLogGlue")
public class SystemLogGlueImpl extends ElasticPlugin<SystemLogBean> implements SystemLogGlue {

    private final Logger log = LoggerFactory.getLogger(SystemLogGlueImpl.class);

    @Resource
    private SystemLogService systemLogService;

    @Resource
    private SystemLogElasticRepository systemLogElasticRepository;

    @Override
    public ResultUtils<List<SystemLogBean>> findAllByPage(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<SystemLogBean>> resultUtils = new ResultUtils<>();
        if (SearchUtils.mapValueIsNotEmpty(search)) {
            Page<SystemLogElastic> systemLogElasticPage = systemLogElasticRepository.search(buildSearchQuery(search, dataTablesUtils));
            resultUtils.data(dataBuilder(systemLogElasticPage)).isSearch(true).totalElements(systemLogElasticPage.getTotalElements());
        } else {
            resultUtils.data(freestanding(dataTablesUtils)).isSearch(false);
        }
        return resultUtils;
    }

    @Override
    public long countAll(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        long count;
        if (SearchUtils.mapValueIsNotEmpty(search)) {
            count = systemLogElasticRepository.count();
        } else {
            count = systemLogService.countAll();
        }
        return count;
    }

    @Override
    public long countByCondition(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        long count = 0;
        if (!SearchUtils.mapValueIsNotEmpty(search)) {
            count = systemLogService.countByCondition(dataTablesUtils);
        }
        return count;
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
     * 数据原生实现方式
     *
     * @param dataTablesUtils datatables工具类
     * @return 原生数据
     */
    private List<SystemLogBean> freestanding(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        List<SystemLogBean> systemLogs = new ArrayList<>();
        Result<Record> records = systemLogService.findAllByPage(dataTablesUtils);
        if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
            systemLogs = records.into(SystemLogBean.class);
            systemLogs.forEach(s -> {
                Date date = DateTimeUtils.timestampToDate(s.getOperatingTime());
                s.setOperatingTimeNew(DateTimeUtils.formatDate(date));
            });
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

        return boolqueryBuilder;
    }

    /**
     * 系统日志排序
     *
     * @param dataTablesUtils datatables工具类
     */
    @Override
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<SystemLogBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_log_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC));
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC));
                }
            }

            if ("behavior".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.ASC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("behavior").order(SortOrder.DESC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC));
                }
            }

            if ("operating_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.ASC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("operatingTime").order(SortOrder.DESC));
                }
            }

            if ("ip_address".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.ASC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.ASC));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("ipAddress").order(SortOrder.DESC));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemLogId").order(SortOrder.DESC));
                }
            }
        }
        return nativeSearchQueryBuilder;
    }
}
