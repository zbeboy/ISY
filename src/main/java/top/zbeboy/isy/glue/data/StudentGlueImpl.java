package top.zbeboy.isy.glue.data;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
import top.zbeboy.isy.elastic.pojo.StudentElastic;
import top.zbeboy.isy.elastic.repository.StudentElasticRepository;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017-04-16.
 */
@Slf4j
@Repository("studentGlue")
public class StudentGlueImpl implements StudentGlue {

    @Resource
    private StudentElasticRepository studentElasticRepository;

    @Resource
    private RoleService roleService;


    @Override
    public ResultUtils<List<StudentBean>> findAllByPageExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<StudentBean>> resultUtils = ResultUtils.of();
        BoolQueryBuilder boolqueryBuilder = QueryBuilders.boolQuery();
        boolqueryBuilder.must(searchCondition(search));
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.SYSTEM_AUTHORITIES));
            boolqueryBuilder.mustNot(QueryBuilders.termQuery("authorities", ElasticBook.NO_AUTHORITIES));
        } else {
            boolqueryBuilder.must(QueryBuilders.matchQuery("authorities", ElasticBook.HAS_AUTHORITIES));
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(boolqueryBuilder);
        Page<StudentElastic> studentElasticPage = studentElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(studentElasticPage)).totalElements(studentElasticPage.getTotalElements());
    }

    @Override
    public ResultUtils<List<StudentBean>> findAllByPageNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils) {
        JSONObject search = dataTablesUtils.getSearch();
        ResultUtils<List<StudentBean>> resultUtils = ResultUtils.of();
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(prepositionCondition(search, ElasticBook.NO_AUTHORITIES));
        Page<StudentElastic> studentElasticPage = studentElasticRepository.search(sortCondition(dataTablesUtils, nativeSearchQueryBuilder).withPageable(pagination(dataTablesUtils)).build());
        return resultUtils.data(dataBuilder(studentElasticPage)).totalElements(studentElasticPage.getTotalElements());
    }

    @Override
    public long countAllExistsAuthorities() {
        long count;
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            List<Integer> list = new ArrayList<>();
            list.add(ElasticBook.SYSTEM_AUTHORITIES);
            list.add(ElasticBook.NO_AUTHORITIES);
            count = studentElasticRepository.countByAuthoritiesNotIn(list);
        } else {
            count = studentElasticRepository.countByAuthorities(ElasticBook.HAS_AUTHORITIES);
        }
        return count;
    }

    @Override
    public long countAllNotExistsAuthorities() {
        return studentElasticRepository.countByAuthorities(ElasticBook.NO_AUTHORITIES);
    }

    /**
     * 若有其它条件
     *
     * @param search      条件内容
     * @param authorities 详见：ElasticBook
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
     * @param studentElasticPage 分页数据
     * @return 新数据
     */
    private List<StudentBean> dataBuilder(Page<StudentElastic> studentElasticPage) {
        List<StudentBean> students = new ArrayList<>();
        for (StudentElastic studentElastic : studentElasticPage.getContent()) {
            StudentBean studentBean = new StudentBean();
            studentBean.setStudentId(studentElastic.getStudentId());
            studentBean.setStudentNumber(studentElastic.getStudentNumber());
            studentBean.setBirthday(studentElastic.getBirthday());
            studentBean.setSex(studentElastic.getSex());
            studentBean.setIdCard(studentElastic.getIdCard());
            studentBean.setFamilyResidence(studentElastic.getFamilyResidence());
            studentBean.setPoliticalLandscapeId(studentElastic.getPoliticalLandscapeId());
            studentBean.setPoliticalLandscapeName(studentElastic.getPoliticalLandscapeName());
            studentBean.setNationId(studentElastic.getNationId());
            studentBean.setNationName(studentElastic.getNationName());
            studentBean.setDormitoryNumber(studentElastic.getDormitoryNumber());
            studentBean.setParentName(studentElastic.getParentName());
            studentBean.setParentContactPhone(studentElastic.getParentContactPhone());
            studentBean.setPlaceOrigin(studentElastic.getPlaceOrigin());
            studentBean.setSchoolId(studentElastic.getSchoolId());
            studentBean.setSchoolName(studentElastic.getSchoolName());
            studentBean.setCollegeId(studentElastic.getCollegeId());
            studentBean.setCollegeName(studentElastic.getCollegeName());
            studentBean.setDepartmentId(studentElastic.getDepartmentId());
            studentBean.setDepartmentName(studentElastic.getDepartmentName());
            studentBean.setScienceId(studentElastic.getScienceId());
            studentBean.setScienceName(studentElastic.getScienceName());
            studentBean.setGrade(studentElastic.getGrade());
            studentBean.setOrganizeId(studentElastic.getOrganizeId());
            studentBean.setOrganizeName(studentElastic.getOrganizeName());
            studentBean.setUsername(studentElastic.getUsername());
            studentBean.setEnabled(studentElastic.getEnabled());
            studentBean.setRealName(studentElastic.getRealName());
            studentBean.setMobile(studentElastic.getMobile());
            studentBean.setAvatar(studentElastic.getAvatar());
            studentBean.setVerifyMailbox(studentElastic.getVerifyMailbox());
            studentBean.setLangKey(studentElastic.getLangKey());
            studentBean.setJoinDate(studentElastic.getJoinDate());
            studentBean.setRoleName(studentElastic.getRoleName());
            students.add(studentBean);
        }
        return students;
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
            String science = StringUtils.trimWhitespace(search.getString("science"));
            String grade = StringUtils.trimWhitespace(search.getString("grade"));
            String organize = StringUtils.trimWhitespace(search.getString("organize"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
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

            if (StringUtils.hasLength(science)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("scienceName", science);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(grade)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("grade", SQLQueryUtils.elasticLikeAllParam(grade));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(organize)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("organizeName", organize);
                boolqueryBuilder.must(matchQueryBuilder);
            }

            if (StringUtils.hasLength(studentNumber)) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("studentNumber", SQLQueryUtils.elasticLikeAllParam(studentNumber));
                boolqueryBuilder.must(wildcardQueryBuilder);
            }

            if (StringUtils.hasLength(username)) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery("username", SQLQueryUtils.elasticLikeAllParam(username));
                boolqueryBuilder.must(matchQueryBuilder);
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
    public NativeSearchQueryBuilder sortCondition(DataTablesUtils<StudentBean> dataTablesUtils, NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("studentNumber").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("studentNumber").order(SortOrder.DESC).unmappedType("string"));
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

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("scienceName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("grade".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("grade").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("organizeName").order(SortOrder.DESC).unmappedType("string"));
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

            if ("dormitory_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("dormitoryNumber").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("dormitoryNumber").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("place_origin".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("placeOrigin").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("placeOrigin").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("parent_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentName").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentName").order(SortOrder.DESC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.DESC).unmappedType("string"));
                }
            }

            if ("parent_contact_phone".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentContactPhone").order(SortOrder.ASC).unmappedType("string"));
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("username").order(SortOrder.ASC).unmappedType("string"));
                } else {
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("parentContactPhone").order(SortOrder.DESC).unmappedType("string"));
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
    public PageRequest pagination(DataTablesUtils<StudentBean> dataTablesUtils) {
        return new PageRequest(dataTablesUtils.getExtraPage(), dataTablesUtils.getLength());
    }
}
