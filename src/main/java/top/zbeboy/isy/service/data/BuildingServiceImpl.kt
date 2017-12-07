package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.BuildingDao
import top.zbeboy.isy.domain.tables.pojos.Building
import top.zbeboy.isy.domain.tables.records.BuildingRecord
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.plugin.DataTablesPlugin
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.building.BuildingBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Service("buildingService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class BuildingServiceImpl @Autowired constructor(dslContext: DSLContext) : DataTablesPlugin<BuildingBean>(), BuildingService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var buildingDao: BuildingDao

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon

    override fun findById(id: Int): Building {
        return buildingDao.findById(id)
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(BUILDING)
                .join(COLLEGE)
                .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(BUILDING.BUILDING_ID.eq(id))
                .fetchOptional()
    }

    override fun findAllByPage(dataTablesUtils: DataTablesUtils<BuildingBean>): Result<Record> {
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildBuildingCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.select()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                sortCondition(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                pagination(dataTablesUtils, null, selectJoinStep, DataTablesPlugin.JOIN_TYPE)
                selectJoinStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(BUILDING)
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
                        .from(BUILDING)
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
                        .from(BUILDING)
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

    override fun findByBuildingNameAndCollegeId(buildingName: String, collegeId: Int): Result<BuildingRecord> {
        return create.selectFrom<BuildingRecord>(BUILDING)
                .where(BUILDING.BUILDING_NAME.eq(buildingName).and(BUILDING.COLLEGE_ID.eq(collegeId)))
                .fetch()
    }

    override fun findByCollegeIdAndIsDel(collegeId: Int, isDel: Byte?): Result<BuildingRecord> {
        return create.selectFrom<BuildingRecord>(BUILDING)
                .where(BUILDING.COLLEGE_ID.eq(collegeId).and(BUILDING.BUILDING_IS_DEL.eq(isDel)))
                .fetch()
    }

    override fun findByBuildingNameAndCollegeIdNeBuildingId(buildingName: String, collegeId: Int, buildingId: Int): Result<BuildingRecord> {
        return create.selectFrom<BuildingRecord>(BUILDING)
                .where(BUILDING.BUILDING_NAME.eq(buildingName).and(BUILDING.COLLEGE_ID.eq(collegeId)).and(BUILDING.BUILDING_ID.ne(buildingId)))
                .fetch()
    }

    override fun countAll(): Int {
        val roleCondition = buildBuildingCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            statisticsAll(create, BUILDING)
        } else {
            create.selectCount()
                    .from(BUILDING)
                    .join(COLLEGE)
                    .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition)
                    .fetchOne().value1()
        }
    }

    override fun countByCondition(dataTablesUtils: DataTablesUtils<BuildingBean>): Int {
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildBuildingCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectJoinStep = create.selectCount()
                        .from(BUILDING)
                selectJoinStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition)
                selectConditionStep.fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(BUILDING)
                        .join(COLLEGE)
                        .on(BUILDING.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .where(a)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(BUILDING)
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
    override fun save(building: Building) {
        buildingDao.insert(building)
    }

    override fun update(building: Building) {
        buildingDao.update(building)
    }

    override fun updateIsDel(ids: List<Int>, isDel: Byte?) {
        for (id in ids) {
            create.update<BuildingRecord>(BUILDING).set<Byte>(BUILDING.BUILDING_IS_DEL, isDel).where(BUILDING.BUILDING_ID.eq(id)).execute()
        }
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    override fun searchCondition(dataTablesUtils: DataTablesUtils<BuildingBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val schoolName = StringUtils.trimWhitespace(search!!.getString("schoolName"))
            val collegeName = StringUtils.trimWhitespace(search.getString("collegeName"))
            val buildingName = StringUtils.trimWhitespace(search.getString("buildingName"))
            if (StringUtils.hasLength(schoolName)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(schoolName))
            }

            if (StringUtils.hasLength(collegeName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName))
                } else {
                    a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(collegeName)))
                }
            }

            if (StringUtils.hasLength(buildingName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName))
                } else {
                    a!!.and(BUILDING.BUILDING_NAME.like(SQLQueryUtils.likeAllParam(buildingName)))
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
    override fun sortCondition(dataTablesUtils: DataTablesUtils<BuildingBean>, selectConditionStep: SelectConditionStep<Record>?, selectJoinStep: SelectJoinStep<Record>?, type: Int) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("building_id".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_ID.asc()
                } else {
                    sortField[0] = BUILDING.BUILDING_ID.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = BUILDING.BUILDING_ID.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = BUILDING.BUILDING_ID.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = BUILDING.BUILDING_ID.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = BUILDING.BUILDING_ID.desc()
                }
            }

            if ("building_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_NAME.asc()
                    sortField[1] = BUILDING.BUILDING_ID.asc()
                } else {
                    sortField[0] = BUILDING.BUILDING_NAME.desc()
                    sortField[1] = BUILDING.BUILDING_ID.desc()
                }
            }

            if ("building_is_del".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = BUILDING.BUILDING_IS_DEL.asc()
                    sortField[1] = BUILDING.BUILDING_ID.asc()
                } else {
                    sortField[0] = BUILDING.BUILDING_IS_DEL.desc()
                    sortField[1] = BUILDING.BUILDING_ID.desc()
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, *sortField!!)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildBuildingCondition(): Condition? {
        return methodServiceCommon.buildCollegeCondition()
    }

}