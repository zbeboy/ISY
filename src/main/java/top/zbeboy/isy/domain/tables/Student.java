/**
 * This class is generated by jOOQ
 */
package top.zbeboy.isy.domain.tables;


import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import top.zbeboy.isy.domain.Isy;
import top.zbeboy.isy.domain.Keys;
import top.zbeboy.isy.domain.tables.records.StudentRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.7.4"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Student extends TableImpl<StudentRecord> {

	private static final long serialVersionUID = 412432346;

	/**
	 * The reference instance of <code>isy.student</code>
	 */
	public static final Student STUDENT = new Student();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<StudentRecord> getRecordType() {
		return StudentRecord.class;
	}

	/**
	 * The column <code>isy.student.student_id</code>.
	 */
	public final TableField<StudentRecord, Integer> STUDENT_ID = createField("student_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>isy.student.student_number</code>.
	 */
	public final TableField<StudentRecord, String> STUDENT_NUMBER = createField("student_number", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

	/**
	 * The column <code>isy.student.birthday</code>.
	 */
	public final TableField<StudentRecord, Date> BIRTHDAY = createField("birthday", org.jooq.impl.SQLDataType.DATE, this, "");

	/**
	 * The column <code>isy.student.sex</code>.
	 */
	public final TableField<StudentRecord, String> SEX = createField("sex", org.jooq.impl.SQLDataType.VARCHAR.length(2), this, "");

	/**
	 * The column <code>isy.student.id_card</code>.
	 */
	public final TableField<StudentRecord, String> ID_CARD = createField("id_card", org.jooq.impl.SQLDataType.VARCHAR.length(20), this, "");

	/**
	 * The column <code>isy.student.family_residence</code>.
	 */
	public final TableField<StudentRecord, String> FAMILY_RESIDENCE = createField("family_residence", org.jooq.impl.SQLDataType.VARCHAR.length(600), this, "");

	/**
	 * The column <code>isy.student.political_landscape_id</code>.
	 */
	public final TableField<StudentRecord, Integer> POLITICAL_LANDSCAPE_ID = createField("political_landscape_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>isy.student.nation_id</code>.
	 */
	public final TableField<StudentRecord, Integer> NATION_ID = createField("nation_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>isy.student.dormitory_number</code>.
	 */
	public final TableField<StudentRecord, String> DORMITORY_NUMBER = createField("dormitory_number", org.jooq.impl.SQLDataType.VARCHAR.length(15), this, "");

	/**
	 * The column <code>isy.student.parent_name</code>.
	 */
	public final TableField<StudentRecord, String> PARENT_NAME = createField("parent_name", org.jooq.impl.SQLDataType.VARCHAR.length(10), this, "");

	/**
	 * The column <code>isy.student.parent_contact_phone</code>.
	 */
	public final TableField<StudentRecord, String> PARENT_CONTACT_PHONE = createField("parent_contact_phone", org.jooq.impl.SQLDataType.VARCHAR.length(15), this, "");

	/**
	 * The column <code>isy.student.place_origin</code>.
	 */
	public final TableField<StudentRecord, String> PLACE_ORIGIN = createField("place_origin", org.jooq.impl.SQLDataType.VARCHAR.length(500), this, "");

	/**
	 * The column <code>isy.student.organize_id</code>.
	 */
	public final TableField<StudentRecord, Integer> ORGANIZE_ID = createField("organize_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>isy.student.username</code>.
	 */
	public final TableField<StudentRecord, String> USERNAME = createField("username", org.jooq.impl.SQLDataType.VARCHAR.length(200).nullable(false), this, "");

	/**
	 * Create a <code>isy.student</code> table reference
	 */
	public Student() {
		this("student", null);
	}

	/**
	 * Create an aliased <code>isy.student</code> table reference
	 */
	public Student(String alias) {
		this(alias, STUDENT);
	}

	private Student(String alias, Table<StudentRecord> aliased) {
		this(alias, aliased, null);
	}

	private Student(String alias, Table<StudentRecord> aliased, Field<?>[] parameters) {
		super(alias, Isy.ISY, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<StudentRecord, Integer> getIdentity() {
		return Keys.IDENTITY_STUDENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<StudentRecord> getPrimaryKey() {
		return Keys.KEY_STUDENT_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<StudentRecord>> getKeys() {
		return Arrays.<UniqueKey<StudentRecord>>asList(Keys.KEY_STUDENT_PRIMARY, Keys.KEY_STUDENT_STUDENT_NUMBER, Keys.KEY_STUDENT_ID_CARD);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ForeignKey<StudentRecord, ?>> getReferences() {
		return Arrays.<ForeignKey<StudentRecord, ?>>asList(Keys.STUDENT_IBFK_1, Keys.STUDENT_IBFK_2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Student as(String alias) {
		return new Student(alias, this);
	}

	/**
	 * Rename this table
	 */
	public Student rename(String name) {
		return new Student(name, null);
	}
}
