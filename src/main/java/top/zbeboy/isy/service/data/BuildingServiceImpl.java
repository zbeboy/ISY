package top.zbeboy.isy.service.data;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.daos.BuildingDao;
import top.zbeboy.isy.domain.tables.pojos.Building;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.domain.tables.records.BuildingRecord;
import top.zbeboy.isy.service.platform.RoleService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.data.building.BuildingBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/5/27.
 */
@Slf4j
@Service("buildingService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class BuildingServiceImpl extends DataTablesPlugin<BuildingBean> implements BuildingService {

    private final DSLContext create;

    @Resource
    private BuildingDao buildingDao;

    @Resource
    private RoleService roleService;

    @Resource
    private UsersService usersService;

    @Autowired
    public BuildingServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Building findById(int id) {
        return buildingDao.findById(id);
    }

    @Override
    public Optional<Record> findByIdRelation(int id) {
        return create.select()
                .from(BUILDING)
                .join(COLLEGE)
                .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(BUILDING.BUILDING_ID.eq(id))
                .fetchOptional();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<BuildingBean> dataTablesUtils) {
        Result<Record> records = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record> selectJoinStep = create.select()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
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
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
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
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
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
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
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
    public Result<BuildingRecord> findByBuildingNameAndCollegeId(String buildingName, int collegeId) {
        return create.selectFrom(BUILDING)
                .where(BUILDING.BUILDING_NAME.eq(buildingName).and(BUILDING.COLLEGE_ID.eq(collegeId)))
                .fetch();
    }

    @Override
    public Result<BuildingRecord> findByBuildingNameAndCollegeIdNeBuildingId(String buildingName, int collegeId, int buildingId) {
        return create.selectFrom(BUILDING)
                .where(BUILDING.BUILDING_NAME.eq(buildingName).and(BUILDING.COLLEGE_ID.eq(collegeId)).and(BUILDING.BUILDING_ID.ne(buildingId)))
                .fetch();
    }

    @Override
    public int countAll() {
        // 分权限显示用户数据
        if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
            return statisticsAll(create, BUILDING);
        } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = roleService.getRoleCollegeId(record);
            Record1<Integer> count = create.selectCount()
                    .from(BUILDING)
                    .join(COLLEGE)
                    .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(COLLEGE.COLLEGE_ID.eq(collegeId))
                    .fetchOne();
            return count.value1();
        }
        return 0;
    }

    @Override
    public int countByCondition(DataTablesUtils<BuildingBean> dataTablesUtils) {
        Record1<Integer> count = null;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                        .from(BUILDING);
                count = selectJoinStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(COLLEGE.COLLEGE_ID.eq(collegeId));
                count = selectConditionStep.fetchOne();
            }
        } else {
            // 分权限显示用户数据
            if (roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 系统
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a);
                count = selectConditionStep.fetchOne();
            } else if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) { // 管理员
                Users users = usersService.getUserFromSession();
                Optional<Record> record = usersService.findUserSchoolInfo(users);
                int collegeId = roleService.getRoleCollegeId(record);
                SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
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

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(Building building) {
        buildingDao.insert(building);
    }

    @Override
    public void update(Building building) {
        buildingDao.update(building);
    }

    @Override
    public void updateIsDel(List<Integer> ids, Byte isDel) {
        for (int id : ids) {
            create.update(BUILDING).set(BUILDING.BUILDING_IS_DEL, isDel).where(BUILDING.BUILDING_ID.eq(id)).execute();
        }
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<BuildingBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String schoolName = StringUtils.trimWhitespace(search.getString("schoolName"));
            String collegeName = StringUtils.trimWhitespace(search.getString("collegeName"));
            String buildingName = StringUtils.trimWhitespace(search.getString("buildingName"));
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

            if (StringUtils.hasLength(buildingName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName));
                } else {
                    a = a.and(BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName)));
                }
            }
        }
        return a;
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<BuildingBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("building_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_ID.asc();
                } else {
                    sortField[0] = BUILDING.BUILDING_ID.desc();
                }
            }

            if ("school_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc();
                    sortField[1] = BUILDING.BUILDING_ID.asc();
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc();
                    sortField[1] = BUILDING.BUILDING_ID.desc();
                }
            }

            if ("college_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc();
                    sortField[1] = BUILDING.BUILDING_ID.asc();
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc();
                    sortField[1] = BUILDING.BUILDING_ID.desc();
                }
            }

            if ("building_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_NAME.asc();
                    sortField[1] = BUILDING.BUILDING_ID.asc();
                } else {
                    sortField[0] = BUILDING.BUILDING_NAME.desc();
                    sortField[1] = BUILDING.BUILDING_ID.desc();
                }
            }

            if ("building_is_del".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_IS_DEL.asc();
                    sortField[1] = BUILDING.BUILDING_ID.asc();
                } else {
                    sortField[0] = BUILDING.BUILDING_IS_DEL.desc();
                    sortField[1] = BUILDING.BUILDING_ID.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
