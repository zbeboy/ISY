package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.SchoolroomDao
import top.zbeboy.isy.domain.tables.pojos.Schoolroom
import top.zbeboy.isy.domain.tables.records.SchoolroomRecord
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.schoolroom.SchoolroomBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-08 .
 **/
@Service("schoolroomService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class SchoolroomServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<SchoolroomBean>(), SchoolroomService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var schoolroomDao: SchoolroomDao

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon


    override fun findById(id: Int): Schoolroom {
        return schoolroomDao.findById(id)
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(SCHOOLROOM)
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .join(COLLEGE)
                .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(SCHOOLROOM.SCHOOLROOM_ID.eq(id))
                .fetchOptional()
    }

    override fun findByBuildingCodeAndBuildingId(buildingCode: String, buildingId: Int): Result<SchoolroomRecord> {
        return create.selectFrom<SchoolroomRecord>(SCHOOLROOM)
                .where(SCHOOLROOM.BUILDING_CODE.eq(buildingCode).and(SCHOOLROOM.BUILDING_ID.eq(buildingId)))
                .fetch()
    }

    override fun findByBuildingCodeAndBuildingIdNeSchoolroomId(buildingCode: String, schoolroomId: Int, buildingId: Int): Result<SchoolroomRecord> {
        return create.selectFrom<SchoolroomRecord>(SCHOOLROOM)
                .where(SCHOOLROOM.BUILDING_CODE.eq(buildingCode).and(SCHOOLROOM.BUILDING_ID.eq(buildingId).and(SCHOOLROOM.SCHOOLROOM_ID.ne(schoolroomId))))
                .fetch()
    }

    override fun findByBuildingIdAndIsDel(buildingId: Int, b: Byte?): Result<SchoolroomRecord> {
        return create.selectFrom<SchoolroomRecord>(SCHOOLROOM)
                .where(SCHOOLROOM.BUILDING_ID.eq(buildingId).and(SCHOOLROOM.SCHOOLROOM_IS_DEL.eq(b)))
                .fetch()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<SchoolroomBean>): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildSchoolroomCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.select()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                selectJoinStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                sortCondition(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                pagination(dataTablesUtils, selectConditionStep, null, DataTablesPlugin.CONDITION_TYPE)
                selectConditionStep.fetch()
            }
        }
    }

    override fun countAll(): Int {
        val roleCondition = buildSchoolroomCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            statisticsAll(create, SCHOOLROOM)
        } else {
            create.selectCount()
                    .from(SCHOOLROOM)
                    .join(BUILDING)
                    .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                    .join(COLLEGE)
                    .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .join(SCHOOL)
                    .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                    .where(roleCondition)
                    .fetchOne().value1()
        }
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<SchoolroomBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildSchoolroomCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.selectCount()
                        .from(SCHOOLROOM)
                selectJoinStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition)
                selectConditionStep.fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(SCHOOLROOM)
                        .join(BUILDING)
                        .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(roleCondition!!.and(a))
                selectConditionStep.fetchOne()
            }
        }
        return count.value1()
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(schoolroom: Schoolroom) {
        schoolroomDao.insert(schoolroom)
    }

    override fun update(schoolroom: Schoolroom) {
        schoolroomDao.update(schoolroom)
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update<SchoolroomRecord>(SCHOOLROOM).set<Byte>(SCHOOLROOM.SCHOOLROOM_IS_DEL, isDel).where(SCHOOLROOM.SCHOOLROOM_ID.eq(id)).execute()
        }
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<SchoolroomBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val buildingName = StringUtils.trimWhitespace(search.getString("buildingName"))
            val buildingCode = StringUtils.trimWhitespace(search.getString("buildingCode"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a = a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

            if (StringUtils.hasLength(buildingName)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName))
                } else {
                    a = a!!.and(BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName)))
                }
            }

            if (StringUtils.hasLength(buildingCode)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SCHOOLROOM.BUILDING_CODE.like(SQLQueryUtils.likeAllParam(buildingCode))
                } else {
                    a = a!!.and(SCHOOLROOM.BUILDING_CODE.like(SQLQueryUtils.likeAllParam(buildingCode)))
                }
            }

        }
        return a
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    override fun sortCondition(dataTablesUtils: DataTablesUtils<SchoolroomBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("schoolroom_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCHOOLROOM.SCHOOLROOM_ID.asc()
                } else {
                    sortField[0] = SCHOOLROOM.SCHOOLROOM_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.desc()
                }
            }

            if ("building_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_NAME.asc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.asc()
                } else {
                    sortField[0] = BUILDING.BUILDING_NAME.desc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.desc()
                }
            }

            if ("building_code".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = SCHOOLROOM.BUILDING_CODE.asc()
                } else {
                    sortField[0] = SCHOOLROOM.BUILDING_CODE.desc()
                }
            }

            if ("schoolroom_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOLROOM.SCHOOLROOM_IS_DEL.asc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.asc()
                } else {
                    sortField[0] = SCHOOLROOM.SCHOOLROOM_IS_DEL.desc()
                    sortField[1] = SCHOOLROOM.SCHOOLROOM_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildSchoolroomCondition(): Condition? {
        return methodServiceCommon.buildCollegeCondition()
    }
}