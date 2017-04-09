package top.zbeboy.isy.service.data;

import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.OrganizeDao;
import top.zbeboy.isy.domain.tables.pojos.Organize;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.elastic.pojo.OrganizeElastic;
import top.zbeboy.isy.elastic.repository.OrganizeElasticRepository;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-08-21.
 */
@Service("organizeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OrganizeServiceImpl extends DataTablesPlugin<OrganizeBean> implements OrganizeService {

    private final Logger log = LoggerFactory.getLogger(OrganizeServiceImpl.class);

    private final DSLContext create;

    @Resource
    private OrganizeDao organizeDao;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    @Resource
    private OrganizeElasticRepository organizeElasticRepository;

    @Autowired
    public OrganizeServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record1<String>> findByScienceIdAndDistinctGradeAndIsDel(int scienceId, Byte b) {
        return create.selectDistinct(ORGANIZE.GRADE)
                .from(ORGANIZE)
                .where(ORGANIZE.SCIENCE_ID.eq(scienceId).and(ORGANIZE.ORGANIZE_IS_DEL.eq(b)))
                .orderBy(ORGANIZE.ORGANIZE_ID.desc())
                .limit(0, 6)
                .fetch();
    }

    @Override
    public Result<OrganizeRecord> findInScienceIdsAndGradeAndIsDel(List<Integer> scienceIds, String grade, Byte b) {
        return create.selectFrom(ORGANIZE)
                .where(ORGANIZE.SCIENCE_ID.in(scienceIds).and(ORGANIZE.GRADE.eq(grade)).and(ORGANIZE.ORGANIZE_IS_DEL.eq(b)))
                .fetch();
    }

    @Override
    public List<Organize> findByScienceId(int scienceId) {
        return organizeDao.fetchByScienceId(scienceId);
    }

    @Override
    public Result<Record1<String>> findByDepartmentIdAndDistinctGrade(int departmentId) {
        Byte isDel = 0;
        return create.selectDistinct(ORGANIZE.GRADE)
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(DEPARTMENT.DEPARTMENT_ID.eq(departmentId).and(ORGANIZE.ORGANIZE_IS_DEL.eq(isDel)))
                .orderBy(ORGANIZE.GRADE.desc())
                .limit(0, 6)
                .fetch();
    }

    @Override
    public Result<OrganizeRecord> findByOrganizeNameAndScienceIdNeOrganizeId(String organizeName, int organizeId, int scienceId) {
        return create.selectFrom(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_NAME.eq(organizeName).and(ORGANIZE.SCIENCE_ID.eq(scienceId)).and(ORGANIZE.ORGANIZE_ID.ne(organizeId)))
                .fetch();
    }

    @Override
    public Result<OrganizeRecord> findByGradeAndScienceId(String grade, int scienceId) {
        Byte isDel = 0;
        return create.selectFrom(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_IS_DEL.eq(isDel).and(ORGANIZE.GRADE.eq(grade)).and(ORGANIZE.SCIENCE_ID.eq(scienceId)))
                .fetch();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(OrganizeElastic organizeElastic) {
        OrganizeRecord record = create.insertInto(ORGANIZE)
                .set(ORGANIZE.ORGANIZE_NAME, organizeElastic.getOrganizeName())
                .set(ORGANIZE.ORGANIZE_IS_DEL, organizeElastic.getOrganizeIsDel())
                .set(ORGANIZE.SCIENCE_ID, organizeElastic.getScienceId())
                .set(ORGANIZE.GRADE, organizeElastic.getGrade())
                .returning(ORGANIZE.ORGANIZE_ID)
                .fetchOne();
        organizeElastic.setOrganizeId(record.getOrganizeId());
        organizeElasticRepository.save(organizeElastic);
    }

    @Override
    public void update(Organize organize) {
        organizeDao.update(organize);
        OrganizeElastic organizeElastic = organizeElasticRepository.findOne(organize.getOrganizeId() + "");
        organizeElastic.setOrganizeId(organize.getOrganizeId());
        organizeElastic.setOrganizeIsDel(organize.getOrganizeIsDel());
        organizeElastic.setOrganizeName(organize.getOrganizeName());
        organizeElastic.setScienceId(organize.getScienceId());
        organizeElastic.setGrade(organize.getGrade());
        organizeElasticRepository.delete(organizeElastic);
        organizeElasticRepository.save(organizeElastic);
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(ORGANIZE).set(ORGANIZE.ORGANIZE_IS_DEL, isDel).where(ORGANIZE.ORGANIZE_ID.eq(id)).execute();
            OrganizeElastic organizeElastic = organizeElasticRepository.findOne(id + "");
            organizeElastic.setOrganizeIsDel(isDel);
            organizeElasticRepository.delete(organizeElastic);
            organizeElasticRepository.save(organizeElastic);
        }
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(ORGANIZE)
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(ORGANIZE.ORGANIZE_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Organize findById(int id) {
        return organizeDao.findById(id);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<OrganizeBean> dataTablesUtils) {
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record> selectJoinStep = create.select()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID));
                sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
                pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
                records = selectJoinStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a);
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record> selectConditionStep = create.select()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a));
                sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
                records = selectConditionStep.fetch();
            }
        }
        return records;
    }

    @Override
    public int countAll() {
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            return statisticsAll(create, ORGANIZE);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(ORGANIZE)
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<OrganizeBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                        .from(ORGANIZE);
                count = selectJoinStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                count = selectConditionStep.fetchOne();
            }

        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(ORGANIZE)
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId).and(a));
                count = selectConditionStep.fetchOne();
            }

        }
        if (!ObjectUtils.isEmpty(count)) {
            return count.value1();
        }
        return 0;
    }

    @Override
    public Result<OrganizeRecord> findByOrganizeNameAndScienceId(String organizeName, int scienceId) {
        return create.selectFrom(ORGANIZE)
                .where(ORGANIZE.ORGANIZE_NAME.eq(organizeName).and(ORGANIZE.SCIENCE_ID.eq(scienceId)))
                .fetch();
    }

    /**
     * 班级数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<OrganizeBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            String departmentName = StringUtils.trimWhitespace(search.getString("departmentName"));
            String scienceName = StringUtils.trimWhitespace(search.getString("scienceName"));
            String grade = StringUtils.trimWhitespace(search.getString("grade"));
            String organizeName = StringUtils.trimWhitespace(search.getString("organizeName"));
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName));
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName));
                } else {
                    a = a.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)));
                }
            }

            if (StringUtils.hasLength(departmentName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName));
                } else {
                    a = a.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(departmentName)));
                }
            }

            if (StringUtils.hasLength(scienceName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName));
                } else {
                    a = a.and(SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(scienceName)));
                }
            }

            if (StringUtils.hasLength(grade)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade));
                } else {
                    a = a.and(ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade)));
                }
            }

            if (StringUtils.hasLength(organizeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organizeName));
                } else {
                    a = a.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organizeName)));
                }
            }

        }
        return a;
    }

    /**
     * 班级数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<OrganizeBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("organize_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("department_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("science_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("grade".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.GRADE.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.GRADE.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

            if ("organize_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc();
                }
            }

            if ("organize_is_del".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_IS_DEL.asc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.asc();
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_IS_DEL.desc();
                    sortField[1] = ORGANIZE.ORGANIZE_ID.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
