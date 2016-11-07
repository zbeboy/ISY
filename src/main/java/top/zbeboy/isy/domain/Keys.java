/**
 * This class is generated by jOOQ
 */
package top.zbeboy.isy.domain;


import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import top.zbeboy.isy.domain.tables.Application;
import top.zbeboy.isy.domain.tables.Authorities;
import top.zbeboy.isy.domain.tables.College;
import top.zbeboy.isy.domain.tables.CollegeApplication;
import top.zbeboy.isy.domain.tables.CollegeRole;
import top.zbeboy.isy.domain.tables.Department;
import top.zbeboy.isy.domain.tables.GraduationPracticeCollege;
import top.zbeboy.isy.domain.tables.GraduationPracticeCompany;
import top.zbeboy.isy.domain.tables.GraduationPracticeUnify;
import top.zbeboy.isy.domain.tables.InternshipApply;
import top.zbeboy.isy.domain.tables.InternshipCollege;
import top.zbeboy.isy.domain.tables.InternshipCompany;
import top.zbeboy.isy.domain.tables.InternshipCompanyHistory;
import top.zbeboy.isy.domain.tables.InternshipJournal;
import top.zbeboy.isy.domain.tables.InternshipRegulate;
import top.zbeboy.isy.domain.tables.InternshipRelease;
import top.zbeboy.isy.domain.tables.InternshipReleaseScience;
import top.zbeboy.isy.domain.tables.InternshipTeacherDistribution;
import top.zbeboy.isy.domain.tables.InternshipType;
import top.zbeboy.isy.domain.tables.Nation;
import top.zbeboy.isy.domain.tables.Organize;
import top.zbeboy.isy.domain.tables.PersistentLogins;
import top.zbeboy.isy.domain.tables.PoliticalLandscape;
import top.zbeboy.isy.domain.tables.Role;
import top.zbeboy.isy.domain.tables.RoleApplication;
import top.zbeboy.isy.domain.tables.SchemaVersion;
import top.zbeboy.isy.domain.tables.School;
import top.zbeboy.isy.domain.tables.Science;
import top.zbeboy.isy.domain.tables.Staff;
import top.zbeboy.isy.domain.tables.Student;
import top.zbeboy.isy.domain.tables.SystemLog;
import top.zbeboy.isy.domain.tables.SystemMailbox;
import top.zbeboy.isy.domain.tables.SystemSms;
import top.zbeboy.isy.domain.tables.Users;
import top.zbeboy.isy.domain.tables.UsersType;
import top.zbeboy.isy.domain.tables.records.ApplicationRecord;
import top.zbeboy.isy.domain.tables.records.AuthoritiesRecord;
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord;
import top.zbeboy.isy.domain.tables.records.CollegeRecord;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;
import top.zbeboy.isy.domain.tables.records.DepartmentRecord;
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCollegeRecord;
import top.zbeboy.isy.domain.tables.records.GraduationPracticeCompanyRecord;
import top.zbeboy.isy.domain.tables.records.GraduationPracticeUnifyRecord;
import top.zbeboy.isy.domain.tables.records.InternshipApplyRecord;
import top.zbeboy.isy.domain.tables.records.InternshipCollegeRecord;
import top.zbeboy.isy.domain.tables.records.InternshipCompanyHistoryRecord;
import top.zbeboy.isy.domain.tables.records.InternshipCompanyRecord;
import top.zbeboy.isy.domain.tables.records.InternshipJournalRecord;
import top.zbeboy.isy.domain.tables.records.InternshipRegulateRecord;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseScienceRecord;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.domain.tables.records.InternshipTypeRecord;
import top.zbeboy.isy.domain.tables.records.NationRecord;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.domain.tables.records.PersistentLoginsRecord;
import top.zbeboy.isy.domain.tables.records.PoliticalLandscapeRecord;
import top.zbeboy.isy.domain.tables.records.RoleApplicationRecord;
import top.zbeboy.isy.domain.tables.records.RoleRecord;
import top.zbeboy.isy.domain.tables.records.SchemaVersionRecord;
import top.zbeboy.isy.domain.tables.records.SchoolRecord;
import top.zbeboy.isy.domain.tables.records.ScienceRecord;
import top.zbeboy.isy.domain.tables.records.StaffRecord;
import top.zbeboy.isy.domain.tables.records.StudentRecord;
import top.zbeboy.isy.domain.tables.records.SystemLogRecord;
import top.zbeboy.isy.domain.tables.records.SystemMailboxRecord;
import top.zbeboy.isy.domain.tables.records.SystemSmsRecord;
import top.zbeboy.isy.domain.tables.records.UsersRecord;
import top.zbeboy.isy.domain.tables.records.UsersTypeRecord;


/**
 * A class modelling foreign key relationships between tables of the <code>isy</code> 
 * schema
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

	// -------------------------------------------------------------------------
	// IDENTITY definitions
	// -------------------------------------------------------------------------

	public static final Identity<ApplicationRecord, Integer> IDENTITY_APPLICATION = Identities0.IDENTITY_APPLICATION;
	public static final Identity<CollegeRecord, Integer> IDENTITY_COLLEGE = Identities0.IDENTITY_COLLEGE;
	public static final Identity<DepartmentRecord, Integer> IDENTITY_DEPARTMENT = Identities0.IDENTITY_DEPARTMENT;
	public static final Identity<InternshipCompanyHistoryRecord, Integer> IDENTITY_INTERNSHIP_COMPANY_HISTORY = Identities0.IDENTITY_INTERNSHIP_COMPANY_HISTORY;
	public static final Identity<InternshipTypeRecord, Integer> IDENTITY_INTERNSHIP_TYPE = Identities0.IDENTITY_INTERNSHIP_TYPE;
	public static final Identity<NationRecord, Integer> IDENTITY_NATION = Identities0.IDENTITY_NATION;
	public static final Identity<OrganizeRecord, Integer> IDENTITY_ORGANIZE = Identities0.IDENTITY_ORGANIZE;
	public static final Identity<PoliticalLandscapeRecord, Integer> IDENTITY_POLITICAL_LANDSCAPE = Identities0.IDENTITY_POLITICAL_LANDSCAPE;
	public static final Identity<RoleRecord, Integer> IDENTITY_ROLE = Identities0.IDENTITY_ROLE;
	public static final Identity<SchoolRecord, Integer> IDENTITY_SCHOOL = Identities0.IDENTITY_SCHOOL;
	public static final Identity<ScienceRecord, Integer> IDENTITY_SCIENCE = Identities0.IDENTITY_SCIENCE;
	public static final Identity<StaffRecord, Integer> IDENTITY_STAFF = Identities0.IDENTITY_STAFF;
	public static final Identity<StudentRecord, Integer> IDENTITY_STUDENT = Identities0.IDENTITY_STUDENT;
	public static final Identity<UsersTypeRecord, Integer> IDENTITY_USERS_TYPE = Identities0.IDENTITY_USERS_TYPE;

	// -------------------------------------------------------------------------
	// UNIQUE and PRIMARY KEY definitions
	// -------------------------------------------------------------------------

	public static final UniqueKey<ApplicationRecord> KEY_APPLICATION_PRIMARY = UniqueKeys0.KEY_APPLICATION_PRIMARY;
	public static final UniqueKey<CollegeRecord> KEY_COLLEGE_PRIMARY = UniqueKeys0.KEY_COLLEGE_PRIMARY;
	public static final UniqueKey<DepartmentRecord> KEY_DEPARTMENT_PRIMARY = UniqueKeys0.KEY_DEPARTMENT_PRIMARY;
	public static final UniqueKey<GraduationPracticeCollegeRecord> KEY_GRADUATION_PRACTICE_COLLEGE_PRIMARY = UniqueKeys0.KEY_GRADUATION_PRACTICE_COLLEGE_PRIMARY;
	public static final UniqueKey<GraduationPracticeCompanyRecord> KEY_GRADUATION_PRACTICE_COMPANY_PRIMARY = UniqueKeys0.KEY_GRADUATION_PRACTICE_COMPANY_PRIMARY;
	public static final UniqueKey<GraduationPracticeUnifyRecord> KEY_GRADUATION_PRACTICE_UNIFY_PRIMARY = UniqueKeys0.KEY_GRADUATION_PRACTICE_UNIFY_PRIMARY;
	public static final UniqueKey<InternshipApplyRecord> KEY_INTERNSHIP_APPLY_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_APPLY_PRIMARY;
	public static final UniqueKey<InternshipCollegeRecord> KEY_INTERNSHIP_COLLEGE_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_COLLEGE_PRIMARY;
	public static final UniqueKey<InternshipCompanyRecord> KEY_INTERNSHIP_COMPANY_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_COMPANY_PRIMARY;
	public static final UniqueKey<InternshipCompanyHistoryRecord> KEY_INTERNSHIP_COMPANY_HISTORY_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_COMPANY_HISTORY_PRIMARY;
	public static final UniqueKey<InternshipJournalRecord> KEY_INTERNSHIP_JOURNAL_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_JOURNAL_PRIMARY;
	public static final UniqueKey<InternshipRegulateRecord> KEY_INTERNSHIP_REGULATE_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_REGULATE_PRIMARY;
	public static final UniqueKey<InternshipReleaseRecord> KEY_INTERNSHIP_RELEASE_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_RELEASE_PRIMARY;
	public static final UniqueKey<InternshipTeacherDistributionRecord> KEY_INTERNSHIP_TEACHER_DISTRIBUTION_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_TEACHER_DISTRIBUTION_PRIMARY;
	public static final UniqueKey<InternshipTypeRecord> KEY_INTERNSHIP_TYPE_PRIMARY = UniqueKeys0.KEY_INTERNSHIP_TYPE_PRIMARY;
	public static final UniqueKey<NationRecord> KEY_NATION_PRIMARY = UniqueKeys0.KEY_NATION_PRIMARY;
	public static final UniqueKey<NationRecord> KEY_NATION_NATION_NAME = UniqueKeys0.KEY_NATION_NATION_NAME;
	public static final UniqueKey<OrganizeRecord> KEY_ORGANIZE_PRIMARY = UniqueKeys0.KEY_ORGANIZE_PRIMARY;
	public static final UniqueKey<PersistentLoginsRecord> KEY_PERSISTENT_LOGINS_PRIMARY = UniqueKeys0.KEY_PERSISTENT_LOGINS_PRIMARY;
	public static final UniqueKey<PoliticalLandscapeRecord> KEY_POLITICAL_LANDSCAPE_PRIMARY = UniqueKeys0.KEY_POLITICAL_LANDSCAPE_PRIMARY;
	public static final UniqueKey<RoleRecord> KEY_ROLE_PRIMARY = UniqueKeys0.KEY_ROLE_PRIMARY;
	public static final UniqueKey<RoleRecord> KEY_ROLE_ROLE_EN_NAME = UniqueKeys0.KEY_ROLE_ROLE_EN_NAME;
	public static final UniqueKey<SchemaVersionRecord> KEY_SCHEMA_VERSION_PRIMARY = UniqueKeys0.KEY_SCHEMA_VERSION_PRIMARY;
	public static final UniqueKey<SchoolRecord> KEY_SCHOOL_PRIMARY = UniqueKeys0.KEY_SCHOOL_PRIMARY;
	public static final UniqueKey<ScienceRecord> KEY_SCIENCE_PRIMARY = UniqueKeys0.KEY_SCIENCE_PRIMARY;
	public static final UniqueKey<StaffRecord> KEY_STAFF_PRIMARY = UniqueKeys0.KEY_STAFF_PRIMARY;
	public static final UniqueKey<StaffRecord> KEY_STAFF_STAFF_NUMBER = UniqueKeys0.KEY_STAFF_STAFF_NUMBER;
	public static final UniqueKey<StaffRecord> KEY_STAFF_ID_CARD = UniqueKeys0.KEY_STAFF_ID_CARD;
	public static final UniqueKey<StudentRecord> KEY_STUDENT_PRIMARY = UniqueKeys0.KEY_STUDENT_PRIMARY;
	public static final UniqueKey<StudentRecord> KEY_STUDENT_STUDENT_NUMBER = UniqueKeys0.KEY_STUDENT_STUDENT_NUMBER;
	public static final UniqueKey<StudentRecord> KEY_STUDENT_ID_CARD = UniqueKeys0.KEY_STUDENT_ID_CARD;
	public static final UniqueKey<SystemLogRecord> KEY_SYSTEM_LOG_PRIMARY = UniqueKeys0.KEY_SYSTEM_LOG_PRIMARY;
	public static final UniqueKey<SystemMailboxRecord> KEY_SYSTEM_MAILBOX_PRIMARY = UniqueKeys0.KEY_SYSTEM_MAILBOX_PRIMARY;
	public static final UniqueKey<SystemSmsRecord> KEY_SYSTEM_SMS_PRIMARY = UniqueKeys0.KEY_SYSTEM_SMS_PRIMARY;
	public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = UniqueKeys0.KEY_USERS_PRIMARY;
	public static final UniqueKey<UsersRecord> KEY_USERS_MOBILE = UniqueKeys0.KEY_USERS_MOBILE;
	public static final UniqueKey<UsersTypeRecord> KEY_USERS_TYPE_PRIMARY = UniqueKeys0.KEY_USERS_TYPE_PRIMARY;

	// -------------------------------------------------------------------------
	// FOREIGN KEY definitions
	// -------------------------------------------------------------------------

	public static final ForeignKey<AuthoritiesRecord, UsersRecord> AUTHORITIES_IBFK_1 = ForeignKeys0.AUTHORITIES_IBFK_1;
	public static final ForeignKey<CollegeRecord, SchoolRecord> COLLEGE_IBFK_1 = ForeignKeys0.COLLEGE_IBFK_1;
	public static final ForeignKey<CollegeApplicationRecord, ApplicationRecord> COLLEGE_APPLICATION_IBFK_1 = ForeignKeys0.COLLEGE_APPLICATION_IBFK_1;
	public static final ForeignKey<CollegeApplicationRecord, CollegeRecord> COLLEGE_APPLICATION_IBFK_2 = ForeignKeys0.COLLEGE_APPLICATION_IBFK_2;
	public static final ForeignKey<CollegeRoleRecord, RoleRecord> COLLEGE_ROLE_IBFK_1 = ForeignKeys0.COLLEGE_ROLE_IBFK_1;
	public static final ForeignKey<CollegeRoleRecord, CollegeRecord> COLLEGE_ROLE_IBFK_2 = ForeignKeys0.COLLEGE_ROLE_IBFK_2;
	public static final ForeignKey<DepartmentRecord, CollegeRecord> DEPARTMENT_IBFK_1 = ForeignKeys0.DEPARTMENT_IBFK_1;
	public static final ForeignKey<GraduationPracticeCollegeRecord, StudentRecord> GRADUATION_PRACTICE_COLLEGE_IBFK_1 = ForeignKeys0.GRADUATION_PRACTICE_COLLEGE_IBFK_1;
	public static final ForeignKey<GraduationPracticeCollegeRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_COLLEGE_IBFK_2 = ForeignKeys0.GRADUATION_PRACTICE_COLLEGE_IBFK_2;
	public static final ForeignKey<GraduationPracticeCompanyRecord, StudentRecord> GRADUATION_PRACTICE_COMPANY_IBFK_1 = ForeignKeys0.GRADUATION_PRACTICE_COMPANY_IBFK_1;
	public static final ForeignKey<GraduationPracticeCompanyRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_COMPANY_IBFK_2 = ForeignKeys0.GRADUATION_PRACTICE_COMPANY_IBFK_2;
	public static final ForeignKey<GraduationPracticeUnifyRecord, StudentRecord> GRADUATION_PRACTICE_UNIFY_IBFK_1 = ForeignKeys0.GRADUATION_PRACTICE_UNIFY_IBFK_1;
	public static final ForeignKey<GraduationPracticeUnifyRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_UNIFY_IBFK_2 = ForeignKeys0.GRADUATION_PRACTICE_UNIFY_IBFK_2;
	public static final ForeignKey<InternshipApplyRecord, StudentRecord> INTERNSHIP_APPLY_IBFK_1 = ForeignKeys0.INTERNSHIP_APPLY_IBFK_1;
	public static final ForeignKey<InternshipApplyRecord, InternshipReleaseRecord> INTERNSHIP_APPLY_IBFK_2 = ForeignKeys0.INTERNSHIP_APPLY_IBFK_2;
	public static final ForeignKey<InternshipCollegeRecord, StudentRecord> INTERNSHIP_COLLEGE_IBFK_1 = ForeignKeys0.INTERNSHIP_COLLEGE_IBFK_1;
	public static final ForeignKey<InternshipCollegeRecord, InternshipReleaseRecord> INTERNSHIP_COLLEGE_IBFK_2 = ForeignKeys0.INTERNSHIP_COLLEGE_IBFK_2;
	public static final ForeignKey<InternshipCompanyRecord, StudentRecord> INTERNSHIP_COMPANY_IBFK_1 = ForeignKeys0.INTERNSHIP_COMPANY_IBFK_1;
	public static final ForeignKey<InternshipCompanyRecord, InternshipReleaseRecord> INTERNSHIP_COMPANY_IBFK_2 = ForeignKeys0.INTERNSHIP_COMPANY_IBFK_2;
	public static final ForeignKey<InternshipCompanyHistoryRecord, StudentRecord> INTERNSHIP_COMPANY_HISTORY_IBFK_1 = ForeignKeys0.INTERNSHIP_COMPANY_HISTORY_IBFK_1;
	public static final ForeignKey<InternshipCompanyHistoryRecord, InternshipReleaseRecord> INTERNSHIP_COMPANY_HISTORY_IBFK_2 = ForeignKeys0.INTERNSHIP_COMPANY_HISTORY_IBFK_2;
	public static final ForeignKey<InternshipJournalRecord, StudentRecord> INTERNSHIP_JOURNAL_IBFK_1 = ForeignKeys0.INTERNSHIP_JOURNAL_IBFK_1;
	public static final ForeignKey<InternshipJournalRecord, InternshipReleaseRecord> INTERNSHIP_JOURNAL_IBFK_2 = ForeignKeys0.INTERNSHIP_JOURNAL_IBFK_2;
	public static final ForeignKey<InternshipRegulateRecord, StudentRecord> INTERNSHIP_REGULATE_IBFK_1 = ForeignKeys0.INTERNSHIP_REGULATE_IBFK_1;
	public static final ForeignKey<InternshipRegulateRecord, InternshipReleaseRecord> INTERNSHIP_REGULATE_IBFK_2 = ForeignKeys0.INTERNSHIP_REGULATE_IBFK_2;
	public static final ForeignKey<InternshipRegulateRecord, StaffRecord> INTERNSHIP_REGULATE_IBFK_3 = ForeignKeys0.INTERNSHIP_REGULATE_IBFK_3;
	public static final ForeignKey<InternshipReleaseRecord, UsersRecord> INTERNSHIP_RELEASE_IBFK_1 = ForeignKeys0.INTERNSHIP_RELEASE_IBFK_1;
	public static final ForeignKey<InternshipReleaseRecord, DepartmentRecord> INTERNSHIP_RELEASE_IBFK_2 = ForeignKeys0.INTERNSHIP_RELEASE_IBFK_2;
	public static final ForeignKey<InternshipReleaseRecord, InternshipTypeRecord> INTERNSHIP_RELEASE_IBFK_3 = ForeignKeys0.INTERNSHIP_RELEASE_IBFK_3;
	public static final ForeignKey<InternshipReleaseScienceRecord, InternshipReleaseRecord> INTERNSHIP_RELEASE_SCIENCE_IBFK_1 = ForeignKeys0.INTERNSHIP_RELEASE_SCIENCE_IBFK_1;
	public static final ForeignKey<InternshipReleaseScienceRecord, ScienceRecord> INTERNSHIP_RELEASE_SCIENCE_IBFK_2 = ForeignKeys0.INTERNSHIP_RELEASE_SCIENCE_IBFK_2;
	public static final ForeignKey<InternshipTeacherDistributionRecord, StaffRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_1 = ForeignKeys0.INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_1;
	public static final ForeignKey<InternshipTeacherDistributionRecord, StudentRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_2 = ForeignKeys0.INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_2;
	public static final ForeignKey<InternshipTeacherDistributionRecord, InternshipReleaseRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_3 = ForeignKeys0.INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_3;
	public static final ForeignKey<OrganizeRecord, ScienceRecord> ORGANIZE_IBFK_1 = ForeignKeys0.ORGANIZE_IBFK_1;
	public static final ForeignKey<RoleApplicationRecord, RoleRecord> ROLE_APPLICATION_IBFK_1 = ForeignKeys0.ROLE_APPLICATION_IBFK_1;
	public static final ForeignKey<RoleApplicationRecord, ApplicationRecord> ROLE_APPLICATION_IBFK_2 = ForeignKeys0.ROLE_APPLICATION_IBFK_2;
	public static final ForeignKey<ScienceRecord, DepartmentRecord> SCIENCE_IBFK_1 = ForeignKeys0.SCIENCE_IBFK_1;
	public static final ForeignKey<StaffRecord, DepartmentRecord> STAFF_IBFK_1 = ForeignKeys0.STAFF_IBFK_1;
	public static final ForeignKey<StaffRecord, UsersRecord> STAFF_IBFK_2 = ForeignKeys0.STAFF_IBFK_2;
	public static final ForeignKey<StudentRecord, OrganizeRecord> STUDENT_IBFK_1 = ForeignKeys0.STUDENT_IBFK_1;
	public static final ForeignKey<StudentRecord, UsersRecord> STUDENT_IBFK_2 = ForeignKeys0.STUDENT_IBFK_2;
	public static final ForeignKey<SystemLogRecord, UsersRecord> SYSTEM_LOG_IBFK_1 = ForeignKeys0.SYSTEM_LOG_IBFK_1;
	public static final ForeignKey<UsersRecord, UsersTypeRecord> USERS_IBFK_1 = ForeignKeys0.USERS_IBFK_1;

	// -------------------------------------------------------------------------
	// [#1459] distribute members to avoid static initialisers > 64kb
	// -------------------------------------------------------------------------

	private static class Identities0 extends AbstractKeys {
		public static Identity<ApplicationRecord, Integer> IDENTITY_APPLICATION = createIdentity(Application.APPLICATION, Application.APPLICATION.APPLICATION_ID);
		public static Identity<CollegeRecord, Integer> IDENTITY_COLLEGE = createIdentity(College.COLLEGE, College.COLLEGE.COLLEGE_ID);
		public static Identity<DepartmentRecord, Integer> IDENTITY_DEPARTMENT = createIdentity(Department.DEPARTMENT, Department.DEPARTMENT.DEPARTMENT_ID);
		public static Identity<InternshipCompanyHistoryRecord, Integer> IDENTITY_INTERNSHIP_COMPANY_HISTORY = createIdentity(InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY.INTERNSHIP_COMPANY_HISTORY_ID);
		public static Identity<InternshipTypeRecord, Integer> IDENTITY_INTERNSHIP_TYPE = createIdentity(InternshipType.INTERNSHIP_TYPE, InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID);
		public static Identity<NationRecord, Integer> IDENTITY_NATION = createIdentity(Nation.NATION, Nation.NATION.NATION_ID);
		public static Identity<OrganizeRecord, Integer> IDENTITY_ORGANIZE = createIdentity(Organize.ORGANIZE, Organize.ORGANIZE.ORGANIZE_ID);
		public static Identity<PoliticalLandscapeRecord, Integer> IDENTITY_POLITICAL_LANDSCAPE = createIdentity(PoliticalLandscape.POLITICAL_LANDSCAPE, PoliticalLandscape.POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID);
		public static Identity<RoleRecord, Integer> IDENTITY_ROLE = createIdentity(Role.ROLE, Role.ROLE.ROLE_ID);
		public static Identity<SchoolRecord, Integer> IDENTITY_SCHOOL = createIdentity(School.SCHOOL, School.SCHOOL.SCHOOL_ID);
		public static Identity<ScienceRecord, Integer> IDENTITY_SCIENCE = createIdentity(Science.SCIENCE, Science.SCIENCE.SCIENCE_ID);
		public static Identity<StaffRecord, Integer> IDENTITY_STAFF = createIdentity(Staff.STAFF, Staff.STAFF.STAFF_ID);
		public static Identity<StudentRecord, Integer> IDENTITY_STUDENT = createIdentity(Student.STUDENT, Student.STUDENT.STUDENT_ID);
		public static Identity<UsersTypeRecord, Integer> IDENTITY_USERS_TYPE = createIdentity(UsersType.USERS_TYPE, UsersType.USERS_TYPE.USERS_TYPE_ID);
	}

	private static class UniqueKeys0 extends AbstractKeys {
		public static final UniqueKey<ApplicationRecord> KEY_APPLICATION_PRIMARY = createUniqueKey(Application.APPLICATION, Application.APPLICATION.APPLICATION_ID);
		public static final UniqueKey<CollegeRecord> KEY_COLLEGE_PRIMARY = createUniqueKey(College.COLLEGE, College.COLLEGE.COLLEGE_ID);
		public static final UniqueKey<DepartmentRecord> KEY_DEPARTMENT_PRIMARY = createUniqueKey(Department.DEPARTMENT, Department.DEPARTMENT.DEPARTMENT_ID);
		public static final UniqueKey<GraduationPracticeCollegeRecord> KEY_GRADUATION_PRACTICE_COLLEGE_PRIMARY = createUniqueKey(GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE, GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.GRADUATION_PRACTICE_COLLEGE_ID);
		public static final UniqueKey<GraduationPracticeCompanyRecord> KEY_GRADUATION_PRACTICE_COMPANY_PRIMARY = createUniqueKey(GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY, GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY.GRADUATION_PRACTICE_COMPANY_ID);
		public static final UniqueKey<GraduationPracticeUnifyRecord> KEY_GRADUATION_PRACTICE_UNIFY_PRIMARY = createUniqueKey(GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY, GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY.GRADUATION_PRACTICE_UNIFY_ID);
		public static final UniqueKey<InternshipApplyRecord> KEY_INTERNSHIP_APPLY_PRIMARY = createUniqueKey(InternshipApply.INTERNSHIP_APPLY, InternshipApply.INTERNSHIP_APPLY.INTERNSHIP_APPLY_ID);
		public static final UniqueKey<InternshipCollegeRecord> KEY_INTERNSHIP_COLLEGE_PRIMARY = createUniqueKey(InternshipCollege.INTERNSHIP_COLLEGE, InternshipCollege.INTERNSHIP_COLLEGE.INTERNSHIP_COLLEGE_ID);
		public static final UniqueKey<InternshipCompanyRecord> KEY_INTERNSHIP_COMPANY_PRIMARY = createUniqueKey(InternshipCompany.INTERNSHIP_COMPANY, InternshipCompany.INTERNSHIP_COMPANY.INTERNSHIP_COMPANY_ID);
		public static final UniqueKey<InternshipCompanyHistoryRecord> KEY_INTERNSHIP_COMPANY_HISTORY_PRIMARY = createUniqueKey(InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY.INTERNSHIP_COMPANY_HISTORY_ID);
		public static final UniqueKey<InternshipJournalRecord> KEY_INTERNSHIP_JOURNAL_PRIMARY = createUniqueKey(InternshipJournal.INTERNSHIP_JOURNAL, InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_JOURNAL_ID);
		public static final UniqueKey<InternshipRegulateRecord> KEY_INTERNSHIP_REGULATE_PRIMARY = createUniqueKey(InternshipRegulate.INTERNSHIP_REGULATE, InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_REGULATE_ID);
		public static final UniqueKey<InternshipReleaseRecord> KEY_INTERNSHIP_RELEASE_PRIMARY = createUniqueKey(InternshipRelease.INTERNSHIP_RELEASE, InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_RELEASE_ID);
		public static final UniqueKey<InternshipTeacherDistributionRecord> KEY_INTERNSHIP_TEACHER_DISTRIBUTION_PRIMARY = createUniqueKey(InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_TEACHER_DISTRIBUTION_ID);
		public static final UniqueKey<InternshipTypeRecord> KEY_INTERNSHIP_TYPE_PRIMARY = createUniqueKey(InternshipType.INTERNSHIP_TYPE, InternshipType.INTERNSHIP_TYPE.INTERNSHIP_TYPE_ID);
		public static final UniqueKey<NationRecord> KEY_NATION_PRIMARY = createUniqueKey(Nation.NATION, Nation.NATION.NATION_ID);
		public static final UniqueKey<NationRecord> KEY_NATION_NATION_NAME = createUniqueKey(Nation.NATION, Nation.NATION.NATION_NAME);
		public static final UniqueKey<OrganizeRecord> KEY_ORGANIZE_PRIMARY = createUniqueKey(Organize.ORGANIZE, Organize.ORGANIZE.ORGANIZE_ID);
		public static final UniqueKey<PersistentLoginsRecord> KEY_PERSISTENT_LOGINS_PRIMARY = createUniqueKey(PersistentLogins.PERSISTENT_LOGINS, PersistentLogins.PERSISTENT_LOGINS.SERIES);
		public static final UniqueKey<PoliticalLandscapeRecord> KEY_POLITICAL_LANDSCAPE_PRIMARY = createUniqueKey(PoliticalLandscape.POLITICAL_LANDSCAPE, PoliticalLandscape.POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID);
		public static final UniqueKey<RoleRecord> KEY_ROLE_PRIMARY = createUniqueKey(Role.ROLE, Role.ROLE.ROLE_ID);
		public static final UniqueKey<RoleRecord> KEY_ROLE_ROLE_EN_NAME = createUniqueKey(Role.ROLE, Role.ROLE.ROLE_EN_NAME);
		public static final UniqueKey<SchemaVersionRecord> KEY_SCHEMA_VERSION_PRIMARY = createUniqueKey(SchemaVersion.SCHEMA_VERSION, SchemaVersion.SCHEMA_VERSION.VERSION);
		public static final UniqueKey<SchoolRecord> KEY_SCHOOL_PRIMARY = createUniqueKey(School.SCHOOL, School.SCHOOL.SCHOOL_ID);
		public static final UniqueKey<ScienceRecord> KEY_SCIENCE_PRIMARY = createUniqueKey(Science.SCIENCE, Science.SCIENCE.SCIENCE_ID);
		public static final UniqueKey<StaffRecord> KEY_STAFF_PRIMARY = createUniqueKey(Staff.STAFF, Staff.STAFF.STAFF_ID);
		public static final UniqueKey<StaffRecord> KEY_STAFF_STAFF_NUMBER = createUniqueKey(Staff.STAFF, Staff.STAFF.STAFF_NUMBER);
		public static final UniqueKey<StaffRecord> KEY_STAFF_ID_CARD = createUniqueKey(Staff.STAFF, Staff.STAFF.ID_CARD);
		public static final UniqueKey<StudentRecord> KEY_STUDENT_PRIMARY = createUniqueKey(Student.STUDENT, Student.STUDENT.STUDENT_ID);
		public static final UniqueKey<StudentRecord> KEY_STUDENT_STUDENT_NUMBER = createUniqueKey(Student.STUDENT, Student.STUDENT.STUDENT_NUMBER);
		public static final UniqueKey<StudentRecord> KEY_STUDENT_ID_CARD = createUniqueKey(Student.STUDENT, Student.STUDENT.ID_CARD);
		public static final UniqueKey<SystemLogRecord> KEY_SYSTEM_LOG_PRIMARY = createUniqueKey(SystemLog.SYSTEM_LOG, SystemLog.SYSTEM_LOG.SYSTEM_LOG_ID);
		public static final UniqueKey<SystemMailboxRecord> KEY_SYSTEM_MAILBOX_PRIMARY = createUniqueKey(SystemMailbox.SYSTEM_MAILBOX, SystemMailbox.SYSTEM_MAILBOX.SYSTEM_MAILBOX_ID);
		public static final UniqueKey<SystemSmsRecord> KEY_SYSTEM_SMS_PRIMARY = createUniqueKey(SystemSms.SYSTEM_SMS, SystemSms.SYSTEM_SMS.SYSTEM_SMS_ID);
		public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = createUniqueKey(Users.USERS, Users.USERS.USERNAME);
		public static final UniqueKey<UsersRecord> KEY_USERS_MOBILE = createUniqueKey(Users.USERS, Users.USERS.MOBILE);
		public static final UniqueKey<UsersTypeRecord> KEY_USERS_TYPE_PRIMARY = createUniqueKey(UsersType.USERS_TYPE, UsersType.USERS_TYPE.USERS_TYPE_ID);
	}

	private static class ForeignKeys0 extends AbstractKeys {
		public static final ForeignKey<AuthoritiesRecord, UsersRecord> AUTHORITIES_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_PRIMARY, Authorities.AUTHORITIES, Authorities.AUTHORITIES.USERNAME);
		public static final ForeignKey<CollegeRecord, SchoolRecord> COLLEGE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_SCHOOL_PRIMARY, College.COLLEGE, College.COLLEGE.SCHOOL_ID);
		public static final ForeignKey<CollegeApplicationRecord, ApplicationRecord> COLLEGE_APPLICATION_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_APPLICATION_PRIMARY, CollegeApplication.COLLEGE_APPLICATION, CollegeApplication.COLLEGE_APPLICATION.APPLICATION_ID);
		public static final ForeignKey<CollegeApplicationRecord, CollegeRecord> COLLEGE_APPLICATION_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_COLLEGE_PRIMARY, CollegeApplication.COLLEGE_APPLICATION, CollegeApplication.COLLEGE_APPLICATION.COLLEGE_ID);
		public static final ForeignKey<CollegeRoleRecord, RoleRecord> COLLEGE_ROLE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_ROLE_PRIMARY, CollegeRole.COLLEGE_ROLE, CollegeRole.COLLEGE_ROLE.ROLE_ID);
		public static final ForeignKey<CollegeRoleRecord, CollegeRecord> COLLEGE_ROLE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_COLLEGE_PRIMARY, CollegeRole.COLLEGE_ROLE, CollegeRole.COLLEGE_ROLE.COLLEGE_ID);
		public static final ForeignKey<DepartmentRecord, CollegeRecord> DEPARTMENT_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_COLLEGE_PRIMARY, Department.DEPARTMENT, Department.DEPARTMENT.COLLEGE_ID);
		public static final ForeignKey<GraduationPracticeCollegeRecord, StudentRecord> GRADUATION_PRACTICE_COLLEGE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE, GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.STUDENT_ID);
		public static final ForeignKey<GraduationPracticeCollegeRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_COLLEGE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE, GraduationPracticeCollege.GRADUATION_PRACTICE_COLLEGE.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<GraduationPracticeCompanyRecord, StudentRecord> GRADUATION_PRACTICE_COMPANY_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY, GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY.STUDENT_ID);
		public static final ForeignKey<GraduationPracticeCompanyRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_COMPANY_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY, GraduationPracticeCompany.GRADUATION_PRACTICE_COMPANY.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<GraduationPracticeUnifyRecord, StudentRecord> GRADUATION_PRACTICE_UNIFY_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY, GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY.STUDENT_ID);
		public static final ForeignKey<GraduationPracticeUnifyRecord, InternshipReleaseRecord> GRADUATION_PRACTICE_UNIFY_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY, GraduationPracticeUnify.GRADUATION_PRACTICE_UNIFY.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipApplyRecord, StudentRecord> INTERNSHIP_APPLY_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipApply.INTERNSHIP_APPLY, InternshipApply.INTERNSHIP_APPLY.STUDENT_ID);
		public static final ForeignKey<InternshipApplyRecord, InternshipReleaseRecord> INTERNSHIP_APPLY_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipApply.INTERNSHIP_APPLY, InternshipApply.INTERNSHIP_APPLY.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipCollegeRecord, StudentRecord> INTERNSHIP_COLLEGE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipCollege.INTERNSHIP_COLLEGE, InternshipCollege.INTERNSHIP_COLLEGE.STUDENT_ID);
		public static final ForeignKey<InternshipCollegeRecord, InternshipReleaseRecord> INTERNSHIP_COLLEGE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipCollege.INTERNSHIP_COLLEGE, InternshipCollege.INTERNSHIP_COLLEGE.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipCompanyRecord, StudentRecord> INTERNSHIP_COMPANY_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipCompany.INTERNSHIP_COMPANY, InternshipCompany.INTERNSHIP_COMPANY.STUDENT_ID);
		public static final ForeignKey<InternshipCompanyRecord, InternshipReleaseRecord> INTERNSHIP_COMPANY_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipCompany.INTERNSHIP_COMPANY, InternshipCompany.INTERNSHIP_COMPANY.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipCompanyHistoryRecord, StudentRecord> INTERNSHIP_COMPANY_HISTORY_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY.STUDENT_ID);
		public static final ForeignKey<InternshipCompanyHistoryRecord, InternshipReleaseRecord> INTERNSHIP_COMPANY_HISTORY_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY, InternshipCompanyHistory.INTERNSHIP_COMPANY_HISTORY.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipJournalRecord, StudentRecord> INTERNSHIP_JOURNAL_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipJournal.INTERNSHIP_JOURNAL, InternshipJournal.INTERNSHIP_JOURNAL.STUDENT_ID);
		public static final ForeignKey<InternshipJournalRecord, InternshipReleaseRecord> INTERNSHIP_JOURNAL_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipJournal.INTERNSHIP_JOURNAL, InternshipJournal.INTERNSHIP_JOURNAL.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipRegulateRecord, StudentRecord> INTERNSHIP_REGULATE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipRegulate.INTERNSHIP_REGULATE, InternshipRegulate.INTERNSHIP_REGULATE.STUDENT_ID);
		public static final ForeignKey<InternshipRegulateRecord, InternshipReleaseRecord> INTERNSHIP_REGULATE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipRegulate.INTERNSHIP_REGULATE, InternshipRegulate.INTERNSHIP_REGULATE.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipRegulateRecord, StaffRecord> INTERNSHIP_REGULATE_IBFK_3 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STAFF_PRIMARY, InternshipRegulate.INTERNSHIP_REGULATE, InternshipRegulate.INTERNSHIP_REGULATE.STAFF_ID);
		public static final ForeignKey<InternshipReleaseRecord, UsersRecord> INTERNSHIP_RELEASE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_PRIMARY, InternshipRelease.INTERNSHIP_RELEASE, InternshipRelease.INTERNSHIP_RELEASE.USERNAME);
		public static final ForeignKey<InternshipReleaseRecord, DepartmentRecord> INTERNSHIP_RELEASE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_DEPARTMENT_PRIMARY, InternshipRelease.INTERNSHIP_RELEASE, InternshipRelease.INTERNSHIP_RELEASE.DEPARTMENT_ID);
		public static final ForeignKey<InternshipReleaseRecord, InternshipTypeRecord> INTERNSHIP_RELEASE_IBFK_3 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_TYPE_PRIMARY, InternshipRelease.INTERNSHIP_RELEASE, InternshipRelease.INTERNSHIP_RELEASE.INTERNSHIP_TYPE_ID);
		public static final ForeignKey<InternshipReleaseScienceRecord, InternshipReleaseRecord> INTERNSHIP_RELEASE_SCIENCE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE, InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<InternshipReleaseScienceRecord, ScienceRecord> INTERNSHIP_RELEASE_SCIENCE_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_SCIENCE_PRIMARY, InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE, InternshipReleaseScience.INTERNSHIP_RELEASE_SCIENCE.SCIENCE_ID);
		public static final ForeignKey<InternshipTeacherDistributionRecord, StaffRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STAFF_PRIMARY, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STAFF_ID);
		public static final ForeignKey<InternshipTeacherDistributionRecord, StudentRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_STUDENT_PRIMARY, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.STUDENT_ID);
		public static final ForeignKey<InternshipTeacherDistributionRecord, InternshipReleaseRecord> INTERNSHIP_TEACHER_DISTRIBUTION_IBFK_3 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_INTERNSHIP_RELEASE_PRIMARY, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION, InternshipTeacherDistribution.INTERNSHIP_TEACHER_DISTRIBUTION.INTERNSHIP_RELEASE_ID);
		public static final ForeignKey<OrganizeRecord, ScienceRecord> ORGANIZE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_SCIENCE_PRIMARY, Organize.ORGANIZE, Organize.ORGANIZE.SCIENCE_ID);
		public static final ForeignKey<RoleApplicationRecord, RoleRecord> ROLE_APPLICATION_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_ROLE_PRIMARY, RoleApplication.ROLE_APPLICATION, RoleApplication.ROLE_APPLICATION.ROLE_ID);
		public static final ForeignKey<RoleApplicationRecord, ApplicationRecord> ROLE_APPLICATION_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_APPLICATION_PRIMARY, RoleApplication.ROLE_APPLICATION, RoleApplication.ROLE_APPLICATION.APPLICATION_ID);
		public static final ForeignKey<ScienceRecord, DepartmentRecord> SCIENCE_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_DEPARTMENT_PRIMARY, Science.SCIENCE, Science.SCIENCE.DEPARTMENT_ID);
		public static final ForeignKey<StaffRecord, DepartmentRecord> STAFF_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_DEPARTMENT_PRIMARY, Staff.STAFF, Staff.STAFF.DEPARTMENT_ID);
		public static final ForeignKey<StaffRecord, UsersRecord> STAFF_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_PRIMARY, Staff.STAFF, Staff.STAFF.USERNAME);
		public static final ForeignKey<StudentRecord, OrganizeRecord> STUDENT_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_ORGANIZE_PRIMARY, Student.STUDENT, Student.STUDENT.ORGANIZE_ID);
		public static final ForeignKey<StudentRecord, UsersRecord> STUDENT_IBFK_2 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_PRIMARY, Student.STUDENT, Student.STUDENT.USERNAME);
		public static final ForeignKey<SystemLogRecord, UsersRecord> SYSTEM_LOG_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_PRIMARY, SystemLog.SYSTEM_LOG, SystemLog.SYSTEM_LOG.USERNAME);
		public static final ForeignKey<UsersRecord, UsersTypeRecord> USERS_IBFK_1 = createForeignKey(top.zbeboy.isy.domain.Keys.KEY_USERS_TYPE_PRIMARY, Users.USERS, Users.USERS.USERS_TYPE_ID);
	}
}
