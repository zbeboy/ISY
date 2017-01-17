package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSONObject;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeUnifyDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeUnify;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.internship.review.GraduationPracticeUnifyBean;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeUnifyVo;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.*;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeUnifyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeUnifyServiceImpl extends DataTablesPlugin<GraduationPracticeUnify> implements GraduationPracticeUnifyService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeUnifyServiceImpl.class);

    private final DSLContext create;

    @Resource
    private GraduationPracticeUnifyDao graduationPracticeUnifyDao;

    @Autowired
    public GraduationPracticeUnifyServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public GraduationPracticeUnify findById(String id) {
        return graduationPracticeUnifyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.insert(graduationPracticeUnify);
    }

    @Override
    public void saveWithTransaction(GraduationPracticeUnifyVo graduationPracticeUnifyVo) {
        create.transaction(configuration -> {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            int state = 0;
            DSL.using(configuration)
                    .insertInto(INTERNSHIP_APPLY)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.getInternshipReleaseId())
                    .set(INTERNSHIP_APPLY.STUDENT_ID, graduationPracticeUnifyVo.getStudentId())
                    .set(INTERNSHIP_APPLY.APPLY_TIME, now)
                    .set(INTERNSHIP_APPLY.INTERNSHIP_APPLY_STATE, state)
                    .execute();

            String[] headmasterArr = graduationPracticeUnifyVo.getHeadmaster().split(" ");
            if (headmasterArr.length >= 2) {
                graduationPracticeUnifyVo.setHeadmaster(headmasterArr[0]);
                graduationPracticeUnifyVo.setHeadmasterContact(headmasterArr[1]);
            }
            String[] schoolGuidanceTeacherArr = graduationPracticeUnifyVo.getSchoolGuidanceTeacher().split(" ");
            if (schoolGuidanceTeacherArr.length >= 2) {
                graduationPracticeUnifyVo.setSchoolGuidanceTeacher(schoolGuidanceTeacherArr[0]);
                graduationPracticeUnifyVo.setSchoolGuidanceTeacherTel(schoolGuidanceTeacherArr[1]);
            }

            DSL.using(configuration)
                    .insertInto(GRADUATION_PRACTICE_UNIFY)
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID, UUIDUtils.getUUID())
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_ID, graduationPracticeUnifyVo.getStudentId())
                    .set(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.getInternshipReleaseId())
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_NAME, graduationPracticeUnifyVo.getStudentName())
                    .set(GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS, graduationPracticeUnifyVo.getCollegeClass())
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_SEX, graduationPracticeUnifyVo.getStudentSex())
                    .set(GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER, graduationPracticeUnifyVo.getStudentNumber())
                    .set(GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER, graduationPracticeUnifyVo.getPhoneNumber())
                    .set(GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX, graduationPracticeUnifyVo.getQqMailbox())
                    .set(GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT, graduationPracticeUnifyVo.getParentalContact())
                    .set(GRADUATION_PRACTICE_UNIFY.HEADMASTER, graduationPracticeUnifyVo.getHeadmaster())
                    .set(GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT, graduationPracticeUnifyVo.getHeadmasterContact())
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME, graduationPracticeUnifyVo.getGraduationPracticeUnifyName())
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS, graduationPracticeUnifyVo.getGraduationPracticeUnifyAddress())
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS, graduationPracticeUnifyVo.getGraduationPracticeUnifyContacts())
                    .set(GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL, graduationPracticeUnifyVo.getGraduationPracticeUnifyTel())
                    .set(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER, graduationPracticeUnifyVo.getSchoolGuidanceTeacher())
                    .set(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL, graduationPracticeUnifyVo.getSchoolGuidanceTeacherTel())
                    .set(GRADUATION_PRACTICE_UNIFY.START_TIME, DateTimeUtils.formatDate(graduationPracticeUnifyVo.getStartTime()))
                    .set(GRADUATION_PRACTICE_UNIFY.END_TIME, DateTimeUtils.formatDate(graduationPracticeUnifyVo.getEndTime()))
                    .execute();

            DSL.using(configuration)
                    .insertInto(INTERNSHIP_CHANGE_HISTORY)
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_CHANGE_HISTORY_ID, UUIDUtils.getUUID())
                    .set(INTERNSHIP_CHANGE_HISTORY.INTERNSHIP_RELEASE_ID, graduationPracticeUnifyVo.getInternshipReleaseId())
                    .set(INTERNSHIP_CHANGE_HISTORY.STUDENT_ID, graduationPracticeUnifyVo.getStudentId())
                    .set(INTERNSHIP_CHANGE_HISTORY.STATE, state)
                    .set(INTERNSHIP_CHANGE_HISTORY.APPLY_TIME, now)
                    .execute();
        });
    }

    @Override
    public void update(GraduationPracticeUnify graduationPracticeUnify) {
        graduationPracticeUnifyDao.update(graduationPracticeUnify);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(GRADUATION_PRACTICE_UNIFY)
                .where(GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_UNIFY.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationPracticeUnify> dataTablesUtils, GraduationPracticeUnify graduationPracticeUnify) {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.getInternshipReleaseId()));
    }

    @Override
    public int countAll(GraduationPracticeUnify graduationPracticeUnify) {
        return statisticsAllWithCondition(create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.getInternshipReleaseId()));
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationPracticeUnify> dataTablesUtils, GraduationPracticeUnify graduationPracticeUnify) {
        return statisticsWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.getInternshipReleaseId()));
    }

    @Override
    public Result<Record> exportData(DataTablesUtils<GraduationPracticeUnify> dataTablesUtils, GraduationPracticeUnify graduationPracticeUnify) {
        return dataPagingQueryAllWithConditionNoPage(dataTablesUtils, create, GRADUATION_PRACTICE_UNIFY, GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeUnify.getInternshipReleaseId()));
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<GraduationPracticeUnify> dataTablesUtils) {
        Condition a = null;
        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String studentName = StringUtils.trimWhitespace(search.getString("studentName"));
            String studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"));
            String collegeClass = StringUtils.trimWhitespace(search.getString("collegeClass"));
            String phoneNumber = StringUtils.trimWhitespace(search.getString("phoneNumber"));
            String headmaster = StringUtils.trimWhitespace(search.getString("headmaster"));
            String schoolGuidanceTeacher = StringUtils.trimWhitespace(search.getString("schoolGuidanceTeacher"));
            if (StringUtils.hasLength(studentName)) {
                a = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(collegeClass)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass));
                } else {
                    a = a.and(GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass)));
                }
            }

            if (StringUtils.hasLength(phoneNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber));
                } else {
                    a = a.and(GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber)));
                }
            }

            if (StringUtils.hasLength(headmaster)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_UNIFY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster));
                } else {
                    a = a.and(GRADUATION_PRACTICE_UNIFY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster)));
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher));
                } else {
                    a = a.and(GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)));
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
    public void sortCondition(DataTablesUtils<GraduationPracticeUnify> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_NUMBER.desc();
                }
            }

            if ("college_class".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.COLLEGE_CLASS.desc();
                }
            }

            if ("student_sex".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_SEX.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.STUDENT_SEX.desc();
                }
            }

            if ("phone_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.PHONE_NUMBER.desc();
                }
            }

            if ("qq_mailbox".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.QQ_MAILBOX.desc();
                }
            }

            if ("parental_contact".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONTACT.desc();
                }
            }

            if ("headmaster".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.HEADMASTER.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.HEADMASTER.desc();
                }
            }

            if ("headmaster_contact".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.HEADMASTER_CONTACT.desc();
                }
            }

            if ("graduation_practice_unify_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_NAME.desc();
                }
            }

            if ("graduation_practice_unify_address".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ADDRESS.desc();
                }
            }

            if ("graduation_practice_unify_contacts".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_CONTACTS.desc();
                }
            }

            if ("graduation_practice_unify_tel".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_TEL.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER.desc();
                }
            }

            if ("school_guidance_teacher_tel".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.SCHOOL_GUIDANCE_TEACHER_TEL.desc();
                }
            }

            if ("start_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.START_TIME.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.START_TIME.desc();
                }
            }

            if ("end_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.END_TIME.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.END_TIME.desc();
                }
            }

            if ("commitment_book".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.COMMITMENT_BOOK.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.COMMITMENT_BOOK.desc();
                }
            }

            if ("safety_responsibility_book".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.SAFETY_RESPONSIBILITY_BOOK.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.SAFETY_RESPONSIBILITY_BOOK.desc();
                }
            }

            if ("practice_agreement".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.PRACTICE_AGREEMENT.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.PRACTICE_AGREEMENT.desc();
                }
            }

            if ("internship_application".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.INTERNSHIP_APPLICATION.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.INTERNSHIP_APPLICATION.desc();
                }
            }

            if ("practice_receiving".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.PRACTICE_RECEIVING.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.PRACTICE_RECEIVING.desc();
                }
            }

            if ("security_education_agreement".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.SECURITY_EDUCATION_AGREEMENT.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.SECURITY_EDUCATION_AGREEMENT.desc();
                }
            }

            if ("parental_consent".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortField = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONSENT.asc();
                } else {
                    sortField = GRADUATION_PRACTICE_UNIFY.PARENTAL_CONSENT.desc();
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
