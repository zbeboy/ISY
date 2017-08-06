package top.zbeboy.isy.service.graduate.design;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.*;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_DECLARE;
import static top.zbeboy.isy.domain.Tables.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE;

/**
 * Created by lenovo on 2017-08-06.
 */
@Slf4j
@Service("graduateArchivesService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationDesignArchivesServiceImpl extends DataTablesPlugin<GraduationDesignArchivesBean> implements GraduationDesignArchivesService {

    private final DSLContext create;

    @Autowired
    public GraduationDesignArchivesServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public List<GraduationDesignArchivesBean> findAllByPage(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean) {
        List<GraduationDesignArchivesBean> graduateArchivesBeans = new ArrayList<>();
        Result<Record> records;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record> selectJoinStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID));
            sortCondition(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            pagination(dataTablesUtils, null, selectJoinStep, JOIN_TYPE);
            records = selectJoinStep.fetch();
        } else {
            SelectConditionStep<Record> selectConditionStep = create.select()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a);
            sortCondition(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            pagination(dataTablesUtils, selectConditionStep, null, CONDITION_TYPE);
            records = selectConditionStep.fetch();
        }
        buildData(records, graduateArchivesBeans);
        return graduateArchivesBeans;
    }

    @Override
    public int countAll(GraduationDesignArchivesBean graduateArchivesBean) {
        Record1<Integer> count;
        Condition a = otherCondition(null, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .fetchOne();
        } else {
            count = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a)
                    .fetchOne();
        }
        return count.value1();
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean) {
        Record1<Integer> count;
        Condition a = searchCondition(dataTablesUtils);
        a = otherCondition(a, graduateArchivesBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(GRADUATION_DESIGN_TEACHER)
                    .join(GRADUATION_DESIGN_TUTOR)
                    .on(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID.eq(GRADUATION_DESIGN_TUTOR.GRADUATION_DESIGN_TEACHER_ID))
                    .join(GRADUATION_DESIGN_PRESUBJECT)
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(GRADUATION_DESIGN_PRESUBJECT.STUDENT_ID))
                    .join(STUDENT.join(USERS.as("S")).on(STUDENT.USERNAME.eq(USERS.as("S").USERNAME)))
                    .on(GRADUATION_DESIGN_TUTOR.STUDENT_ID.eq(STUDENT.STUDENT_ID))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(STAFF.join(USERS.as("T")).on(STAFF.USERNAME.eq(USERS.as("T").USERNAME)))
                    .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                    .join(ACADEMIC_TITLE)
                    .on(STAFF.STAFF_ID.eq(ACADEMIC_TITLE.ACADEMIC_TITLE_ID))
                    .leftJoin(GRADUATION_DESIGN_DECLARE)
                    .on(GRADUATION_DESIGN_PRESUBJECT.GRADUATION_DESIGN_PRESUBJECT_ID.eq(GRADUATION_DESIGN_DECLARE.GRADUATION_DESIGN_PRESUBJECT_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.SUBJECT_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_TYPE.SUBJECT_TYPE_ID))
                    .leftJoin(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE)
                    .on(GRADUATION_DESIGN_DECLARE.ORIGIN_TYPE_ID.eq(GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE.ORIGIN_TYPE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    /**
     * 其它条件
     *
     * @param graduateArchivesBean 条件
     * @return 条件
     */
    public Condition otherCondition(Condition a, GraduationDesignArchivesBean graduateArchivesBean) {
        if (!ObjectUtils.isEmpty(graduateArchivesBean)) {

        }

        return a;
    }

    private void buildData(Result<Record> records, List<GraduationDesignArchivesBean> graduateArchivesBeans) {
        for (Record r : records) {
            GraduationDesignArchivesBean graduateArchivesBean = new GraduationDesignArchivesBean();

            graduateArchivesBeans.add(graduateArchivesBean);
        }
    }

    /**
     * 数据全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {

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
    public void sortCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {

    }
}
