package top.zbeboy.isy.glue.data;

import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.elastic.pojo.StaffElastic;
import top.zbeboy.isy.elastic.repository.StaffElasticRepository;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-04-16.
 */
@Repository("staffGlue")
public class StaffGlueImpl implements StaffGlue {

    private final Logger log = LoggerFactory.getLogger(StaffGlueImpl.class);

    @Resource
    private StaffElasticRepository staffElasticRepository;

    @Resource
    private RoleService roleService;

    @Override
    public ResultUtils<List<StaffBean>> findAllByPageExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<StaffBean>> resultUtils = new ResultUtils<>();
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        boolqueryBuilder.must(searchCondition(search));
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", 1));
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", -1));
        } else {
            boolqueryBuilder.must(QueryBuilders.matchQuery("authorities", 0));
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolqueryBuilder);
        Page<StaffElastic> staffElasticPage = staffElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(staffElasticPage)).totalElements(staffElasticPage.getTotalElements());
    }

    @Override
    public ResultUtils<List<StaffBean>> findAllByPageNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<StaffBean>> resultUtils = new ResultUtils<>();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(prepositionCondition(search, -1));
        Page<StaffElastic> staffElasticPage = staffElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(staffElasticPage)).totalElements(staffElasticPage.getTotalElements());
    }

    @Override
    public long countAllExistsAuthorities() {
        long count;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            List<Integer> list = new ArrayList<>();
            list.add(1);
            list.add(-1);
            count = staffElasticRepository.countByAuthoritiesNotIn(list);
        } else {
            count = staffElasticRepository.countByAuthorities(0);
        }
        return count;
    }

    @Override
    public long countAllNotExistsAuthorities() {
        return staffElasticRepository.countByAuthorities(-1);
    }

    /**
     * 若有其它条件
     *
     * @param search      条件内容
     * @param authorities -1 : 无权限 0 :  有权限 1 : 系统 2 : 管理员
     * @return 其它条件
     */
    public QueryBuilder prepositionCondition(JSONObject search, int authorities) {
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        boolqueryBuilder.must(searchCondition(search));
        boolqueryBuilder.must(QueryBuilders.termQuery("authorities", authorities));
        return boolqueryBuilder;
    }

    /**
     * 构建新数据
     *
     * @param staffElasticPage 分页数据
     * @return 新数据
     */
    private List<StaffBean> dataBuilder(Page<StaffElastic> staffElasticPage) {
        List<StaffBean> staffs = new ArrayList<>();
        for (StaffElastic staffElastic : staffElasticPage.getContent()) {
            StaffBean staffBean = new StaffBean();
            staffBean.setStaffId(staffElastic.getStaffId());
            staffBean.setStaffNumber(staffElastic.getStaffNumber());
            staffBean.setBirthday(staffElastic.getBirthday());
            staffBean.setSex(staffElastic.getSex());
            staffBean.setIdCard(staffElastic.getIdCard());
            staffBean.setFamilyResidence(staffElastic.getFamilyResidence());
            staffBean.setPoliticalLandscapeId(staffElastic.getPoliticalLandscapeId());
            staffBean.setPoliticalLandscapeName(staffElastic.getPoliticalLandscapeName());
            staffBean.setNationId(staffElastic.getNationId());
            staffBean.setNationName(staffElastic.getNationName());
            staffBean.setAcademicTitleId(staffElastic.getAcademicTitleId());
            staffBean.setAcademicTitleName(staffElastic.getAcademicTitleName());
            staffBean.setPost(staffElastic.getPost());
            staffBean.setSchoolId(staffElastic.getSchoolId());
            staffBean.setSchoolName(staffElastic.getSchoolName());
            staffBean.setCollegeId(staffElastic.getCollegeId());
            staffBean.setCollegeName(staffElastic.getCollegeName());
            staffBean.setDepartmentId(staffElastic.getDepartmentId());
            staffBean.setDepartmentName(staffElastic.getDepartmentName());
            staffBean.setUsername(staffElastic.getUsername());
            staffBean.setEnabled(staffElastic.getEnabled());
            staffBean.setRealName(staffElastic.getRealName());
            staffBean.setMobile(staffElastic.getMobile());
            staffBean.setAvatar(staffElastic.getAvatar());
            staffBean.setLangKey(staffElastic.getLangKey());
            staffBean.setJoinDate(staffElastic.getJoinDate());
            staffBean.setRoleName(staffElastic.getRoleName());
            staffs.add(staffBean);
        }
        return staffs;
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
            String school = StringUtils.trimWhitespace(search.getString("school"));
            String college = StringUtils.trimWhitespace(search.getString("college"));
            String department = StringUtils.trimWhitespace(search.getString("department"));
            String post = StringUtils.trimWhitespace(search.getString("post"));
            String staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"));
            String username = StringUtils.trimWhitespace(search.getString("username"));
            String mobile = StringUtils.trimWhitespace(search.getString("mobile"));
            String idCard = StringUtils.trimWhitespace(search.getString("idCard"));
            String realName = StringUtils.trimWhitespace(search.getString("realName"));
            String sex = StringUtils.trimWhitespace(search.getString("sex"));

            if (StringUtils.hasLength(school)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("schoolName", school);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(college)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("collegeName", college);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(department)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("departmentName", department);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(post)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("post", department);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(staffNumber)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("staffNumber", SQLQueryUtils.elasticLikeAllParam(staffNumber));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(username)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("username", SQLQueryUtils.elasticLikeAllParam(username));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(mobile)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("mobile", SQLQueryUtils.elasticLikeAllParam(mobile));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(idCard)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("idCard", SQLQueryUtils.elasticLikeAllParam(idCard));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(realName)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("realName", realName);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(sex)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("sex", sex);
                boolqueryBuilder.must(matchQueryBuilder);
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
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<StaffBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("staff_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("staffNumber").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("staffNumber").order(SortOrder.DESC).unmappedType("string"));
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

            if ("id_card".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("idCard").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("idCard").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("schoolName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("collegeName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("departmentName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("academic_title_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("academicTitleName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("academicTitleName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("post".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("post").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("post").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("sex".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sex").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sex").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("birthday".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("birthday").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("birthday").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("nation_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("nationName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("nationName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("politicalLandscape_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("politicalLandscapeName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("politicalLandscapeName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("family_residence".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("familyResidence").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("familyResidence").order(SortOrder.DESC).unmappedType("string"));
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
    public PageRequest pagination(DataTablesUtils<StaffBean> dataTablesUtils) {
        return new PageRequest(dataTablesUtils.getExtraPage(), dataTablesUtils.getLength());
    }

}
