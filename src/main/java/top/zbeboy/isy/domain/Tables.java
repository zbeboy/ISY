/*
 * This file is generated by jOOQ.
*/
package top.zbeboy.isy.domain;


import javax.annotation.Generated;

import top.zbeboy.isy.domain.tables.AcademicTitle;
import top.zbeboy.isy.domain.tables.Application;
import top.zbeboy.isy.domain.tables.Authorities;
import top.zbeboy.isy.domain.tables.Building;
import top.zbeboy.isy.domain.tables.College;
import top.zbeboy.isy.domain.tables.CollegeApplication;
import top.zbeboy.isy.domain.tables.CollegeRole;
import top.zbeboy.isy.domain.tables.DefenseArrangement;
import top.zbeboy.isy.domain.tables.DefenseGroup;
import top.zbeboy.isy.domain.tables.DefenseGroupMember;
import top.zbeboy.isy.domain.tables.DefenseOrder;
import top.zbeboy.isy.domain.tables.DefenseRate;
import top.zbeboy.isy.domain.tables.DefenseTime;
import top.zbeboy.isy.domain.tables.Department;
import top.zbeboy.isy.domain.tables.Files;
import top.zbeboy.isy.domain.tables.GraduateArchives;
import top.zbeboy.isy.domain.tables.GraduateArchivesCode;
import top.zbeboy.isy.domain.tables.GraduationDesignDatum;
import top.zbeboy.isy.domain.tables.GraduationDesignDatumType;
import top.zbeboy.isy.domain.tables.GraduationDesignDeclare;
import top.zbeboy.isy.domain.tables.GraduationDesignDeclareData;
import top.zbeboy.isy.domain.tables.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.GraduationDesignPlan;
import top.zbeboy.isy.domain.tables.GraduationDesignPresubject;
import top.zbeboy.isy.domain.tables.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.GraduationDesignReleaseFile;
import top.zbeboy.isy.domain.tables.GraduationDesignSubjectOriginType;
import top.zbeboy.isy.domain.tables.GraduationDesignSubjectType;
import top.zbeboy.isy.domain.tables.GraduationDesignTeacher;
import top.zbeboy.isy.domain.tables.GraduationDesignTutor;
import top.zbeboy.isy.domain.tables.GraduationPracticeCollege;
import top.zbeboy.isy.domain.tables.GraduationPracticeCompany;
import top.zbeboy.isy.domain.tables.GraduationPracticeUnify;
import top.zbeboy.isy.domain.tables.InternshipApply;
import top.zbeboy.isy.domain.tables.InternshipChangeCompanyHistory;
import top.zbeboy.isy.domain.tables.InternshipChangeHistory;
import top.zbeboy.isy.domain.tables.InternshipCollege;
import top.zbeboy.isy.domain.tables.InternshipCompany;
import top.zbeboy.isy.domain.tables.InternshipFile;
import top.zbeboy.isy.domain.tables.InternshipJournal;
import top.zbeboy.isy.domain.tables.InternshipRegulate;
import top.zbeboy.isy.domain.tables.InternshipRelease;
import top.zbeboy.isy.domain.tables.InternshipReleaseScience;
import top.zbeboy.isy.domain.tables.InternshipTeacherDistribution;
import top.zbeboy.isy.domain.tables.InternshipType;
import top.zbeboy.isy.domain.tables.Nation;
import top.zbeboy.isy.domain.tables.OauthAccessToken;
import top.zbeboy.isy.domain.tables.OauthClientDetails;
import top.zbeboy.isy.domain.tables.OauthClientToken;
import top.zbeboy.isy.domain.tables.OauthCode;
import top.zbeboy.isy.domain.tables.OauthRefreshToken;
import top.zbeboy.isy.domain.tables.Organize;
import top.zbeboy.isy.domain.tables.PersistentLogins;
import top.zbeboy.isy.domain.tables.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.Role;
import top.zbeboy.isy.domain.tables.RoleApplication;
import top.zbeboy.isy.domain.tables.SchemaVersion;
import top.zbeboy.isy.domain.tables.School;
import top.zbeboy.isy.domain.tables.Schoolroom;
import top.zbeboy.isy.domain.tables.Science;
import top.zbeboy.isy.domain.tables.ScoreType;
import top.zbeboy.isy.domain.tables.Staff;
import top.zbeboy.isy.domain.tables.Student;
import top.zbeboy.isy.domain.tables.SyncElastic;
import top.zbeboy.isy.domain.tables.SystemAlert;
import top.zbeboy.isy.domain.tables.SystemAlertType;
import top.zbeboy.isy.domain.tables.SystemMessage;
import top.zbeboy.isy.domain.tables.Users;
import top.zbeboy.isy.domain.tables.UsersType;


/**
 * Convenience access to all tables in isy
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>isy.academic_title</code>.
     */
    public static final AcademicTitle ACADEMIC_TITLE = top.zbeboy.isy.domain.tables.AcademicTitle.ACADEMIC_TITLE;

    /**
     * The table <code>isy.application</code>.
     */
    public static final Application APPLICATION = top.zbeboy.isy.domain.tables.Application.APPLICATION;

    /**
     * The table <code>isy.authorities</code>.
     */
    public static final Authorities AUTHORITIES = top.zbeboy.isy.domain.tables.Authorities.AUTHORITIES;

    /**
     * The table <code>isy.building</code>.
     */
    public static final Building BUILDING = top.zbeboy.isy.domain.tables.Building.BUILDING;

    /**
     * The table <code>isy.college</code>.
     */
    public static final College COLLEGE = top.zbeboy.isy.domain.tables.College.COLLEGE;

    /**
     * The table <code>isy.college_application</code>.
     */
    public static final CollegeApplication COLLEGE_APPLICATION = top.zbeboy.isy.domain.tables.CollegeApplication.COLLEGE_APPLICATION;

    /**
     * The table <code>isy.college_role</code>.
     */
    public static final CollegeRole COLLEGE_ROLE = top.zbeboy.isy.domain.tables.CollegeRole.COLLEGE_ROLE;

    /**
     * The table <code>isy.defense_arrangement</code>.
     */
    public static final DefenseArrangement DEFENSE_ARRANGEMENT = top.zbeboy.isy.domain.tables.DefenseArrangement.DEFENSE_ARRANGEMENT;

    /**
     * The table <code>isy.defense_group</code>.
     */
    public static final DefenseGroup DEFENSE_GROUP = top.zbeboy.isy.domain.tables.DefenseGroup.DEFENSE_GROUP;

    /**
     * The table <code>isy.defense_group_member</code>.
     */
    public static final DefenseGroupMember DEFENSE_GROUP_MEMBER = top.zbeboy.isy.domain.tables.DefenseGroupMember.DEFENSE_GROUP_MEMBER;

    /**
     * The table <code>isy.defense_order</code>.
     */
    public static final DefenseOrder DEFENSE_ORDER = top.zbeboy.isy.domain.tables.DefenseOrder.DEFENSE_ORDER;

    /**
     * The table <code>isy.defense_rate</code>.
     */
    public static final DefenseRate DEFENSE_RATE = top.zbeboy.isy.domain.tables.DefenseRate.DEFENSE_RATE;

    /**
     * The table <code>isy.defense_time</code>.
     */
    public static final DefenseTime DEFENSE_TIME = top.zbeboy.isy.domain.tables.DefenseTime.DEFENSE_TIME;

    /**
     * The table <code>isy.department</code>.
     */
    public static final Department DEPARTMENT = top.zbeboy.isy.domain.tables.Department.DEPARTMENT;

    /**
     * The table <code>isy.files</code>.
     */
    public static final Files FILES = top.zbeboy.isy.domain.tables.Files.FILES;

    /**
     * The table <code>isy.graduate_archives</code>.
     */
    public static final GraduateArchives GRADUATE_ARCHIVES = top.zbeboy.isy.domain.tables.GraduateArchives.GRADUATE_ARCHIVES;

    /**
     * The table <code>isy.graduate_archives_code</code>.
     */
    public static final GraduateArchivesCode GRADUATE_ARCHIVES_CODE = top.zbeboy.isy.domain.tables.GraduateArchivesCode.GRADUATE_ARCHIVES_CODE;

    /**
     * The table <code>isy.graduation_design_datum</code>.
     */
    public static final GraduationDesignDatum GRADUATION_DESIGN_DATUM = top.zbeboy.isy.domain.tables.GraduationDesignDatum.GRADUATION_DESIGN_DATUM;

    /**
     * The table <code>isy.graduation_design_datum_type</code>.
     */
    public static final GraduationDesignDatumType GRADUATION_DESIGN_DATUM_TYPE = top.zbeboy.isy.domain.tables.GraduationDesignDatumType.GRADUATION_DESIGN_DATUM_TYPE;

    /**
     * The table <code>isy.graduation_design_declare</code>.
     */
    public static final GraduationDesignDeclare GRADUATION_DESIGN_DECLARE = top.zbeboy.isy.domain.tables.GraduationDesignDeclare.GRADUATION_DESIGN_DECLARE;

    /**
     * The table <code>isy.graduation_design_declare_data</code>.
     */
    public static final GraduationDesignDeclareData GRADUATION_DESIGN_DECLARE_DATA = top.zbeboy.isy.domain.tables.GraduationDesignDeclareData.GRADUATION_DESIGN_DECLARE_DATA;

    /**
     * The table <code>isy.graduation_design_hope_tutor</code>.
     */
    public static final GraduationDesignHopeTutor GRADUATION_DESIGN_HOPE_TUTOR = top.zbeboy.isy.domain.tables.GraduationDesignHopeTutor.GRADUATION_DESIGN_HOPE_TUTOR;

    /**
     * The table <code>isy.graduation_design_plan</code>.
     */
    public static final GraduationDesignPlan GRADUATION_DESIGN_PLAN = top.zbeboy.isy.domain.tables.GraduationDesignPlan.GRADUATION_DESIGN_PLAN;

    /**
     * The table <code>isy.graduation_design_presubject</code>.
     */
    public static final GraduationDesignPresubject GRADUATION_DESIGN_PRESUBJECT = top.zbeboy.isy.domain.tables.GraduationDesignPresubject.GRADUATION_DESIGN_PRESUBJECT;

    /**
     * The table <code>isy.graduation_design_release</code>.
     */
    public static final GraduationDesignRelease GRADUATION_DESIGN_RELEASE = top.zbeboy.isy.domain.tables.GraduationDesignRelease.GRADUATION_DESIGN_RELEASE;

    /**
     * The table <code>isy.graduation_design_release_file</code>.
     */
    public static final GraduationDesignReleaseFile GRADUATION_DESIGN_RELEASE_FILE = top.zbeboy.isy.domain.tables.GraduationDesignReleaseFile.GRADUATION_DESIGN_RELEASE_FILE;

    /**
     * The table <code>isy.graduation_design_subject_origin_type</code>.
     */
    public static final GraduationDesignSubjectOriginType GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE = top.zbeboy.isy.domain.tables.GraduationDesignSubjectOriginType.GRADUATION_DESIGN_SUBJECT_ORIGIN_TYPE;

    /**
     * The table <code>isy.graduation_design_subject_type</code>.
     */
    public static final GraduationDesignSubjectType GRADUATION_DESIGN_SUBJECT_TYPE = top.zbeboy.isy.domain.tables.GraduationDesignSubjectType.GRADUATION_DESIGN_SUBJECT_TYPE;

    /**
     * The table <code>isy.graduation_design_teacher</code>.
     */
    public static final GraduationDesignTeacher GRADUATION_DESIGN_TEACHER = top.zbeboy.isy.domain.tables.GraduationDesignTeacher.GRADUATION_DESIGN_TEACHER;

    /**
     * The table <code>isy.graduation_design_tutor</code>.
     */
    public static final GraduationDesignTutor GRADUATION_DESIGN_TUTOR = top.zbeboy.isy.domain.tables.GraduationDesignTutor.GRADUATION_DESIGN_TUTOR;

    /**
     * The table <code>isy.graduation_practice_college</code>.
     */
    public static final GraduationPracticeCollege GRADUATION_PRACTICE_COLLEGE = top.zbeboy.isy.domain.tables.GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE;

    /**
     * The table <code>isy.graduation_practice_company</code>.
     */
    public static final GraduationPracticeCompany GRADUATION_PRACTICE_COMPANY = top.zbeboy.isy.domain.tables.GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY;

    /**
     * The table <code>isy.graduation_practice_unify</code>.
     */
    public static final GraduationPracticeUnify GRADUATION_PRACTICE_UNIFY = top.zbeboy.isy.domain.tables.GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY;

    /**
     * The table <code>isy.internship_apply</code>.
     */
    public static final InternshipApply INTERNSHIP_APPLY = top.zbeboy.isy.domain.tables.InternshipApply.INTERNSHIP_APPLY;

    /**
     * The table <code>isy.internship_change_company_history</code>.
     */
    public static final InternshipChangeCompanyHistory INTERNSHIP_CHANGE_COMPANY_HISTORY = top.zbeboy.isy.domain.tables.InternshipChangeCompanyHistory.INTERNSHIP_CHANGE_COMPANY_HISTORY;

    /**
     * The table <code>isy.internship_change_history</code>.
     */
    public static final InternshipChangeHistory INTERNSHIP_CHANGE_HISTORY = top.zbeboy.isy.domain.tables.InternshipChangeHistory.INTERNSHIP_CHANGE_HISTORY;

    /**
     * The table <code>isy.internship_college</code>.
     */
    public static final InternshipCollege INTERNSHIP_COLLEGE = top.zbeboy.isy.domain.tables.InternshipCollege.INTERNSHIP_COLLEGE;

    /**
     * The table <code>isy.internship_company</code>.
     */
    public static final InternshipCompany INTERNSHIP_COMPANY = top.zbeboy.isy.domain.tables.InternshipCompany.INTERNSHIP_COMPANY;

    /**
     * The table <code>isy.internship_file</code>.
     */
    public static final InternshipFile INTERNSHIP_FILE = top.zbeboy.isy.domain.tables.InternshipFile.INTERNSHIP_FILE;

    /**
     * The table <code>isy.internship_journal</code>.
     */
    public static final InternshipJournal INTERNSHIP_JOURNAL = top.zbeboy.isy.domain.tables.InternshipJournal.INTERNSHIP_JOURNAL;

    /**
     * The table <code>isy.internship_regulate</code>.
     */
    public static final InternshipRegulate INTERNSHIP_REGULATE = top.zbeboy.isy.domain.tables.InternshipRegulate.INTERNSHIP_REGULATE;

    /**
     * The table <code>isy.internship_release</code>.
     */
    public static final InternshipRelease INTERNSHIP_RELEASE = top.zbeboy.isy.domain.tables.InternshipRelease.INTERNSHIP_RELEASE;

    /**
     * The table <code>isy.internship_release_science</code>.
     */
    public static final InternshipReleaseScience INTERNSHIP_RELEASE_SCIENCE = top.zbeboy.isy.domain.tables.InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE;

    /**
     * The table <code>isy.internship_teacher_distribution</code>.
     */
    public static final InternshipTeacherDistribution INTERNSHIP_TEACHER_DISTRIBUTION = top.zbeboy.isy.domain.tables.InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION;

    /**
     * The table <code>isy.internship_type</code>.
     */
    public static final InternshipType INTERNSHIP_TYPE = top.zbeboy.isy.domain.tables.InternshipType.INTERNSHIP_TYPE;

    /**
     * The table <code>isy.nation</code>.
     */
    public static final Nation NATION = top.zbeboy.isy.domain.tables.Nation.NATION;

    /**
     * The table <code>isy.oauth_access_token</code>.
     */
    public static final OauthAccessToken OAUTH_ACCESS_TOKEN = top.zbeboy.isy.domain.tables.OauthAccessToken.OAUTH_ACCESS_TOKEN;

    /**
     * The table <code>isy.oauth_client_details</code>.
     */
    public static final OauthClientDetails OAUTH_CLIENT_DETAILS = top.zbeboy.isy.domain.tables.OauthClientDetails.OAUTH_CLIENT_DETAILS;

    /**
     * The table <code>isy.oauth_client_token</code>.
     */
    public static final OauthClientToken OAUTH_CLIENT_TOKEN = top.zbeboy.isy.domain.tables.OauthClientToken.OAUTH_CLIENT_TOKEN;

    /**
     * The table <code>isy.oauth_code</code>.
     */
    public static final OauthCode OAUTH_CODE = top.zbeboy.isy.domain.tables.OauthCode.OAUTH_CODE;

    /**
     * The table <code>isy.oauth_refresh_token</code>.
     */
    public static final OauthRefreshToken OAUTH_REFRESH_TOKEN = top.zbeboy.isy.domain.tables.OauthRefreshToken.OAUTH_REFRESH_TOKEN;

    /**
     * The table <code>isy.organize</code>.
     */
    public static final Organize ORGANIZE = top.zbeboy.isy.domain.tables.Organize.ORGANIZE;

    /**
     * The table <code>isy.persistent_logins</code>.
     */
    public static final PersistentLogins PERSISTENT_LOGINS = top.zbeboy.isy.domain.tables.PersistentLogins.PERSISTENT_LOGINS;

    /**
     * The table <code>isy.political_landscape</code>.
     */
    public static final PoliticalLandscape POLITICAL_LANDSCAPE = top.zbeboy.isy.domain.tables.PoliticalLandscape.POLITICAL_LANDSCAPE;

    /**
     * The table <code>isy.role</code>.
     */
    public static final Role ROLE = top.zbeboy.isy.domain.tables.Role.ROLE;

    /**
     * The table <code>isy.role_application</code>.
     */
    public static final RoleApplication ROLE_APPLICATION = top.zbeboy.isy.domain.tables.RoleApplication.ROLE_APPLICATION;

    /**
     * The table <code>isy.schema_version</code>.
     */
    public static final SchemaVersion SCHEMA_VERSION = top.zbeboy.isy.domain.tables.SchemaVersion.SCHEMA_VERSION;

    /**
     * The table <code>isy.school</code>.
     */
    public static final School SCHOOL = top.zbeboy.isy.domain.tables.School.SCHOOL;

    /**
     * The table <code>isy.schoolroom</code>.
     */
    public static final Schoolroom SCHOOLROOM = top.zbeboy.isy.domain.tables.Schoolroom.SCHOOLROOM;

    /**
     * The table <code>isy.science</code>.
     */
    public static final Science SCIENCE = top.zbeboy.isy.domain.tables.Science.SCIENCE;

    /**
     * The table <code>isy.score_type</code>.
     */
    public static final ScoreType SCORE_TYPE = top.zbeboy.isy.domain.tables.ScoreType.SCORE_TYPE;

    /**
     * The table <code>isy.staff</code>.
     */
    public static final Staff STAFF = top.zbeboy.isy.domain.tables.Staff.STAFF;

    /**
     * The table <code>isy.student</code>.
     */
    public static final Student STUDENT = top.zbeboy.isy.domain.tables.Student.STUDENT;

    /**
     * The table <code>isy.sync_elastic</code>.
     */
    public static final SyncElastic SYNC_ELASTIC = top.zbeboy.isy.domain.tables.SyncElastic.SYNC_ELASTIC;

    /**
     * The table <code>isy.system_alert</code>.
     */
    public static final SystemAlert SYSTEM_ALERT = top.zbeboy.isy.domain.tables.SystemAlert.SYSTEM_ALERT;

    /**
     * The table <code>isy.system_alert_type</code>.
     */
    public static final SystemAlertType SYSTEM_ALERT_TYPE = top.zbeboy.isy.domain.tables.SystemAlertType.SYSTEM_ALERT_TYPE;

    /**
     * The table <code>isy.system_message</code>.
     */
    public static final SystemMessage SYSTEM_MESSAGE = top.zbeboy.isy.domain.tables.SystemMessage.SYSTEM_MESSAGE;

    /**
     * The table <code>isy.users</code>.
     */
    public static final Users USERS = top.zbeboy.isy.domain.tables.Users.USERS;

    /**
     * The table <code>isy.users_type</code>.
     */
    public static final UsersType USERS_TYPE = top.zbeboy.isy.domain.tables.UsersType.USERS_TYPE;
}
