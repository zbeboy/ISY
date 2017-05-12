package top.zbeboy.isy.service.graduate.design;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.GraduationDesignTeacherDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by zbeboy on 2017/5/8.
 */
@Slf4j
@Service("graduationDesignTeacherService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignTeacherServiceImpl extends DataTablesPlugin<GraduationDesignTeacherBean> implements GraduationDesignTeacherService {

    private final DSLContext create;

    @Resource
    private GraduationDesignTeacherDao graduationDesignTeacherDao;

    @Autowired
    public GraduationDesignTeacherServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignTeacherBean> findAllByPage(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils, GraduationDesignTeacherBean graduationDesignTeacherBean) {
        List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS.as("U"))
                    .on(GRADUATION_DESIGN_TEACHER.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS.as("U"))
                    .on(GRADUATION_DESIGN_TEACHER.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()).and(a));
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }

        for (Record r : records) {
            GraduationDesignTeacherBean temp = new GraduationDesignTeacherBean();
            temp.setGraduationDesignTeacherId(r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID));
            temp.setGraduationDesignReleaseId(r.getValue(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID));
            temp.setStaffId(r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_ID));
            temp.setStudentCount(r.getValue(GRADUATION_DESIGN_TEACHER.STUDENT_COUNT));
            temp.setUsername(r.getValue(GRADUATION_DESIGN_TEACHER.USERNAME));
            temp.setRealName(r.getValue(USERS.as("S").REAL_NAME));
            temp.setStaffNumber(r.getValue(STAFF.STAFF_NUMBER));
            temp.setStaffUsername(r.getValue(STAFF.USERNAME));
            temp.setAssignerName(r.getValue(USERS.as("U").REAL_NAME));
            graduationDesignTeacherBeens.add(temp);
        }

        return graduationDesignTeacherBeens;
    }

    @Override
    public List<GraduationDesignTeacher> findByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        return graduationDesignTeacherDao.fetchByGraduationDesignReleaseId(graduationDesignReleaseId);
    }

    @Override
    public void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId) {
        create.deleteFrom(GRADUATION_DESIGN_TEACHER).where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .execute();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationDesignTeacher graduationDesignTeacher) {
        graduationDesignTeacherDao.insert(graduationDesignTeacher);
    }

    @Override
    public int countAll(GraduationDesignTeacherBean graduationDesignTeacherBean) {
        Record1<Integer> count = create.selectCount()
                .from(GRADUATION_DESIGN_TEACHER)
                .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()))
                .fetchOne();
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils, GraduationDesignTeacherBean graduationDesignTeacherBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        if (ObjectUtils.isEmpty(a)) {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()));
            count = selectConditionStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(STAFF.join(USERS.as("S")).on(STAFF.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(USERS.as("U"))
                    .on(GRADUATION_DESIGN_TEACHER.USERNAME.eq(USERS.as("U").USERNAME))
                    .where(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignTeacherBean.getGraduationDesignReleaseId()).and(a));
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String realName = StringUtils.trimWhitespace(search.getString("realName"));
            String staffUsername = StringUtils.trimWhitespace(search.getString("staffUsername"));
            String staffNumber = StringUtils.trimWhitespace(search.getString("staffNumber"));
            if (StringUtils.hasLength(realName)) {
                a = USERS.as("S").REAL_NAME.like(SQLQueryUtils.likeAllParam(realName));
            }

            if (StringUtils.hasLength(staffUsername)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername));
                } else {
                    a = a.and(STAFF.USERNAME.like(SQLQueryUtils.likeAllParam(staffUsername)));
                }
            }

            if (StringUtils.hasLength(staffNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber));
                } else {
                    a = a.and(STAFF.STAFF_NUMBER.like(SQLQueryUtils.likeAllParam(staffNumber)));
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
    public void sortCondition(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("real_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("S").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = USERS.as("S").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("staff_number".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STAFF.STAFF_NUMBER.asc();
                } else {
                    sortField[0] = STAFF.STAFF_NUMBER.desc();
                }
            }

            if ("staff_username".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = STAFF.USERNAME.asc();
                } else {
                    sortField[0] = STAFF.USERNAME.desc();
                }
            }

            if ("student_count".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STUDENT_COUNT.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = GRADUATION_DESIGN_TEACHER.STUDENT_COUNT.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }

            if ("assigner_name".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = USERS.as("U").REAL_NAME.asc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.asc();
                } else {
                    sortField[0] = USERS.as("U").REAL_NAME.desc();
                    sortField[1] = GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.desc();
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
