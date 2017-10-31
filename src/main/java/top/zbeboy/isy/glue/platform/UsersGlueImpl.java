package top.zbeboy.isy.glue.platform;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.elastic.config.ElasticBook;
import top.zbeboy.isy.elastic.pojo.UsersElastic;
import top.zbeboy.isy.elastic.repository.UsersElasticRepository;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.platform.users.UsersBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-04-11.
 */
@Slf4j
@Repository("usersGlue")
public class UsersGlueImpl implements UsersGlue {

    @Resource
    private UsersElasticRepository usersElasticRepository;

    @Resource
    private RoleService roleService;

    @Override
    public ResultUtils<List<UsersBean>> findAllByPageExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<UsersBean>> resultUtils = new ResultUtils<>();
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        boolqueryBuilder.must(searchCondition(search));
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.SYSTEM_AUTHORITIES));
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES));
        } else {
            boolqueryBuilder.must(QueryBuilders.matchQuery("authorities", ElasticBook.HAS_AUTHORITIES));
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolqueryBuilder);
        Page<UsersElastic> usersElasticPage = usersElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(usersElasticPage)).totalElements(usersElasticPage.getTotalElements());
    }

    @Override
    public ResultUtils<List<UsersBean>> findAllByPageNotExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<UsersBean>> resultUtils = new ResultUtils<>();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(prepositionCondition(search, ElasticBook.NO_AUTHORITIES));
        Page<UsersElastic> usersElasticPage = usersElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(usersElasticPage)).totalElements(usersElasticPage.getTotalElements());
    }

    @Override
    public long countAllExistsAuthorities() {
        long count;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            List<Integer> list = new ArrayList<>();
            list.add(ElasticBook.SYSTEM_AUTHORITIES);
            list.add(ElasticBook.NO_AUTHORITIES);
            count = usersElasticRepository.countByAuthoritiesNotIn(list);
        } else {
            count = usersElasticRepository.countByAuthorities(ElasticBook.HAS_AUTHORITIES);
        }
        return count;
    }

    @Override
    public long countAllNotExistsAuthorities() {
        return usersElasticRepository.countByAuthorities(ElasticBook.NO_AUTHORITIES);
    }

    /**
     * 若有其它条件
     *
     * @param search      条件内容
     * @param authorities 详见：ElasticBook
     * @return 其它条件
     */
    private QueryBuilder prepositionCondition(JSONObject search, int authorities) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        boolqueryBuilder.must(searchCondition(search));
        boolqueryBuilder.must(QueryBuilders.termQuery("authorities", authorities));
        return boolqueryBuilder;
    }

    /**
     * 构建新数据
     *
     * @param usersElasticPage 分页数据
     * @return 新数据
     */
    private List<UsersBean> dataBuilder(Page<UsersElastic> usersElasticPage) {
        List<UsersBean> userses = new ArrayList<>();
        for (UsersElastic usersElastic : usersElasticPage.getContent()) {
            UsersBean usersBean = new UsersBean();
            usersBean.setRealName(usersElastic.getRealName());
            usersBean.setUsername(usersElastic.getUsername());
            usersBean.setMobile(usersElastic.getMobile());
            usersBean.setRoleName(usersElastic.getRoleName());
            usersBean.setUsersTypeName(usersElastic.getUsersTypeName());
            usersBean.setEnabled(usersElastic.getEnabled());
            usersBean.setVerifyMailbox(usersElastic.getVerifyMailbox());
            usersBean.setLangKey(usersElastic.getLangKey());
            usersBean.setJoinDate(usersElastic.getJoinDate());
            userses.add(usersBean);
        }
        return userses;
    }

    /**
     * 查询条件
     *
     * @param search 搜索条件
     * @return 查询条件
     */
    public QueryBuilder searchCondition(JSONObject search) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        if (!ObjectUtils.isEmpty(search)) {
            String realName = StringUtils.trimWhitespace(search.getString("realName"));
            String username = StringUtils.trimWhitespace(search.getString("username"));
            String mobile = StringUtils.trimWhitespace(search.getString("mobile"));
            String usersType = StringUtils.trimWhitespace(search.getString("usersType"));
            if (StringUtils.hasLength(realName)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("realName", SQLQueryUtils.elasticLikeAllParam(realName));
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(username)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("username", SQLQueryUtils.elasticLikeAllParam(username));
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(mobile)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("mobile", SQLQueryUtils.elasticLikeAllParam(mobile));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(usersType)) {
                int usersTypeId = NumberUtils.toInt(usersType);
                if (usersTypeId > 0) {
                    MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("usersTypeId", usersTypeId);
                    boolqueryBuilder.must(matchQueryBuilder);
                }
            }
        }
        return boolqueryBuilder;
    }

    /**
     * 排序方式
     *
     * @param dataTablesUtils          datatables工具类
     * @param nativeSearchQueryBuilder builer
     */
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<UsersBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("mobile".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("mobile").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("realName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("role_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("roleName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("roleName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("users_type_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("usersTypeName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("usersTypeName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("enabled".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.ASC).unmappedType("byte"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("enabled").order(SortOrder.DESC).unmappedType("byte"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("lang_key".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("langKey").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("join_date".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.ASC).unmappedType("date"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("joinDate").order(SortOrder.DESC).unmappedType("date"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }
        }
        return nativeSearchQueryBuilder;
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils datatables工具类
     */
    public PageRequest pagination(DataTablesUtils<UsersBean> dataTablesUtils) {
        return new PageRequest(dataTablesUtils.getExtraPage(), dataTablesUtils.getLength());
    }
}
