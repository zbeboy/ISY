package top.zbeboy.isy.service;

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
import top.zbeboy.isy.domain.tables.daos.GraduationPracticeCompanyDao;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCompany;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.Optional;

import static top.zbeboy.isy.domain.Tables.GRADUATION_PRACTICE_COMPANY;

/**
 * Created by lenovo on 2016-11-27.
 */
@Service("graduationPracticeCompanyService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GraduationPracticeCompanyServiceImpl extends DataTablesPlugin<GraduationPracticeCompany> implements GraduationPracticeCompanyService {

    private final Logger log = LoggerFactory.getLogger(GraduationPracticeCompanyServiceImpl.class);

    private final DSLContext create;

    private GraduationPracticeCompanyDao graduationPracticeCompanyDao;

    @Autowired
    public GraduationPracticeCompanyServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.graduationPracticeCompanyDao = new GraduationPracticeCompanyDao(configuration);
    }

    @Override
    public GraduationPracticeCompany findById(String id) {
        return graduationPracticeCompanyDao.findById(id);
    }

    @Override
    public Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        return create.select()
                .from(GRADUATION_PRACTICE_COMPANY)
                .where(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COMPANY.STUDENT_ID.eq(studentId)))
                .fetchOptional();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(GraduationPracticeCompany graduationPracticeCompany) {
        graduationPracticeCompanyDao.insert(graduationPracticeCompany);
    }

    @Override
    public void update(GraduationPracticeCompany graduationPracticeCompany) {
        graduationPracticeCompanyDao.update(graduationPracticeCompany);
    }

    @Override
    public void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId) {
        create.deleteFrom(GRADUATION_PRACTICE_COMPANY)
                .where(GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(internshipReleaseId).and(GRADUATION_PRACTICE_COMPANY.STUDENT_ID.eq(studentId)))
                .execute();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, GraduationPracticeCompany graduationPracticeCompany) {
        return dataPagingQueryAllWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.getInternshipReleaseId()));
    }

    @Override
    public int countAll(GraduationPracticeCompany graduationPracticeCompany) {
        return statisticsAllWithCondition(create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.getInternshipReleaseId()));
    }

    @Override
    public int countByCondition(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, GraduationPracticeCompany graduationPracticeCompany) {
        return statisticsWithCondition(dataTablesUtils, create, GRADUATION_PRACTICE_COMPANY, GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID.eq(graduationPracticeCompany.getInternshipReleaseId()));
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    public Condition searchCondition(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils) {
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
                a = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
            }

            if (StringUtils.hasLength(studentNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber));
                } else {
                    a = a.and(GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)));
                }
            }

            if (StringUtils.hasLength(collegeClass)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass));
                } else {
                    a = a.and(GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.like(SQLQueryUtils.likeAllParam(collegeClass)));
                }
            }

            if (StringUtils.hasLength(phoneNumber)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber));
                } else {
                    a = a.and(GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.like(SQLQueryUtils.likeAllParam(phoneNumber)));
                }
            }

            if (StringUtils.hasLength(headmaster)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_COMPANY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster));
                } else {
                    a = a.and(GRADUATION_PRACTICE_COMPANY.HEADMASTER.like(SQLQueryUtils.likeAllParam(headmaster)));
                }
            }

            if (StringUtils.hasLength(schoolGuidanceTeacher)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher));
                } else {
                    a = a.and(GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.like(SQLQueryUtils.likeAllParam(schoolGuidanceTeacher)));
                }
            }
        }
        return a;
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_NAME.desc();
                }
            }

            if ("student_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER.desc();
                }
            }

            if ("college_class".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.COLLEGE_CLASS.desc();
                }
            }

            if ("student_sex".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_SEX.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.STUDENT_SEX.desc();
                }
            }

            if ("phone_number".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.PHONE_NUMBER.desc();
                }
            }

            if ("qq_mailbox".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.QQ_MAILBOX.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.QQ_MAILBOX.desc();
                }
            }

            if ("parental_contact".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONTACT.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONTACT.desc();
                }
            }

            if ("headmaster".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.HEADMASTER.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.HEADMASTER.desc();
                }
            }

            if ("headmaster_contact".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.HEADMASTER_CONTACT.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.HEADMASTER_CONTACT.desc();
                }
            }

            if ("graduation_practice_company_name".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_NAME.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_NAME.desc();
                }
            }

            if ("graduation_practice_company_address".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ADDRESS.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ADDRESS.desc();
                }
            }

            if ("graduation_practice_company_contacts".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_CONTACTS.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_CONTACTS.desc();
                }
            }

            if ("graduation_practice_company_tel".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_TEL.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_TEL.desc();
                }
            }

            if ("school_guidance_teacher".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER.desc();
                }
            }

            if ("school_guidance_teacher_tel".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER_TEL.asc();
                } else {
                    sortString = GRADUATION_PRACTICE_COMPANY.SCHOOL_GUIDANCE_TEACHER_TEL.desc();
                }
            }

            if ("start_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortDate = GRADUATION_PRACTICE_COMPANY.START_TIME.asc();
                } else {
                    sortDate = GRADUATION_PRACTICE_COMPANY.START_TIME.desc();
                }
            }

            if ("end_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortDate = GRADUATION_PRACTICE_COMPANY.END_TIME.asc();
                } else {
                    sortDate = GRADUATION_PRACTICE_COMPANY.END_TIME.desc();
                }
            }

            if ("commitment_book".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.COMMITMENT_BOOK.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.COMMITMENT_BOOK.desc();
                }
            }

            if ("safety_responsibility_book".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.SAFETY_RESPONSIBILITY_BOOK.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.SAFETY_RESPONSIBILITY_BOOK.desc();
                }
            }

            if ("practice_agreement".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PRACTICE_AGREEMENT.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PRACTICE_AGREEMENT.desc();
                }
            }

            if ("internship_application".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.INTERNSHIP_APPLICATION.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.INTERNSHIP_APPLICATION.desc();
                }
            }

            if ("practice_receiving".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PRACTICE_RECEIVING.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PRACTICE_RECEIVING.desc();
                }
            }

            if ("security_education_agreement".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.SECURITY_EDUCATION_AGREEMENT.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.SECURITY_EDUCATION_AGREEMENT.desc();
                }
            }

            if ("parental_consent".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONSENT.asc();
                } else {
                    sortByte = GRADUATION_PRACTICE_COMPANY.PARENTAL_CONSENT.desc();
                }
            }

        }
        sortToFinish(selectConditionStep, selectJoinStep, type, GRADUATION_PRACTICE_COMPANY.STUDENT_NUMBER);
    }
}
