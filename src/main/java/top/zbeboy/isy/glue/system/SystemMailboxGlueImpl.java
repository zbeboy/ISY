package top.zbeboy.isy.glue.system;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.elastic.pojo.SystemMailboxElastic;
import top.zbeboy.isy.elastic.repository.SystemMailboxElasticRepository;
import top.zbeboy.isy.glue.plugin.ElasticPlugin;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2017-04-08.
 */
@Slf4j
@Repository("systemMailboxGlue")
public class SystemMailboxGlueImpl extends ElasticPlugin<SystemMailboxBean> implements SystemMailboxGlue {

    @Resource
    private SystemMailboxElasticRepository systemMailboxElasticRepository;

    @Override
    public ResultUtils<List<SystemMailboxBean>> findAllByPage(DataTablesUtils<SystemMailboxBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<SystemMailboxBean>> resultUtils = ResultUtils.of();
        Page<SystemMailboxElastic> systemMailboxElasticPage = systemMailboxElasticRepository.search(buildSearchQuery(search, dataTablesUtils, false));
        return resultUtils.data(dataBuilder(systemMailboxElasticPage)).totalElements(systemMailboxElasticPage.getTotalElements());
    }

    @Override
    public long countAll() {
        return systemMailboxElasticRepository.count();
    }

    /**
     * 构建新数据
     *
     * @param systemMailboxElasticPage 分页数据
     * @return 新数据
     */
    private List<SystemMailboxBean> dataBuilder(Page<SystemMailboxElastic> systemMailboxElasticPage) {
        List<SystemMailboxBean> systemMailboxes = new ArrayList<>();
        for (SystemMailboxElastic systemMailboxElastic : systemMailboxElasticPage.getContent()) {
            SystemMailboxBean systemMailboxBean = new SystemMailboxBean();
            systemMailboxBean.setSystemMailboxId(systemMailboxElastic.getSystemMailboxId());
            systemMailboxBean.setSendTime(systemMailboxElastic.getSendTime());
            systemMailboxBean.setAcceptMail(systemMailboxElastic.getAcceptMail());
            systemMailboxBean.setSendCondition(systemMailboxElastic.getSendCondition());
            Date date = DateTimeUtils.timestampToDate(systemMailboxElastic.getSendTime());
            systemMailboxBean.setSendTimeNew(DateTimeUtils.formatDate(date));
            systemMailboxes.add(systemMailboxBean);
        }
        return systemMailboxes;
    }

    /**
     * 系统邮件全局搜索条件
     *
     * @param search 搜索参数
     * @return 搜索条件
     */
    @Override
    public QueryBuilder searchCondition(JSONObject search) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        if (!ObjectUtils.isEmpty(search)) {
            String acceptMail = StringUtils.trimWhitespace(search.getString("acceptMail"));
            if (StringUtils.hasLength(acceptMail)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("acceptMail", SQLQueryUtils.elasticLikeAllParam(acceptMail));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }
        }
        return boolqueryBuilder;
    }

    /**
     * 系统邮件排序
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder 查询器
     */
    @Override
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_mailbox_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("send_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.ASC).unmappedType("long"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendTime").order(SortOrder.DESC).unmappedType("long"));
                }
            }

            if ("accept_mail".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("acceptMail").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("send_condition".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sendCondition").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("systemMailboxId").order(SortOrder.DESC).unmappedType("string"));
                }
            }

        }
        return nativeSearchQueryBuilder;
    }
}
