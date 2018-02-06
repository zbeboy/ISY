package top.zbeboy.isy.service.graduate.design

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.DefenseGroupDao
import top.zbeboy.isy.domain.tables.pojos.DefenseGroup
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean
import java.sql.Timestamp
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-02-06 .
 **/
@Service("defenseGroupService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class DefenseGroupServiceImpl @Autowired constructor(dslContext: DSLContext) : DefenseGroupService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var defenseGroupDao: DefenseGroupDao

    override fun findById(id: String): DefenseGroup {
        return defenseGroupDao.findById(id)
    }

    override fun findByIdRelation(id: String): Optional<Record> {
        return create.select()
                .from(DEFENSE_GROUP)
                .join(SCHOOLROOM)
                .on(DEFENSE_GROUP.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .where(DEFENSE_GROUP.DEFENSE_GROUP_ID.eq(id))
                .fetchOptional()
    }

    override fun findByDefenseArrangementId(defenseArrangementId: String): List<DefenseGroupBean> {
        val records = create.select()
                .from(DEFENSE_GROUP)
                .join(SCHOOLROOM)
                .on(DEFENSE_GROUP.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .leftJoin(GRADUATION_DESIGN_TEACHER)
                .on(DEFENSE_GROUP.LEADER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .leftJoin(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(USERS)
                .on(USERS.USERNAME.eq(DEFENSE_GROUP.SECRETARY_ID))
                .where(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(defenseArrangementId))
                .orderBy<Timestamp>(DEFENSE_GROUP.CREATE_TIME.desc())
                .fetch()
        return buildDefenseGroupList(records)
    }

    override fun findByGraduationDesignReleaseId(graduationDesignReleaseId: String): Result<Record> {
        return create.select()
                .from(DEFENSE_ARRANGEMENT)
                .join(DEFENSE_GROUP)
                .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                .where(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .orderBy<Timestamp>(DEFENSE_GROUP.CREATE_TIME.asc())
                .fetch()
    }

    override fun findByGraduationDesignReleaseIdRelation(graduationDesignReleaseId: String): List<DefenseGroupBean> {
        val records = create.select()
                .from(DEFENSE_ARRANGEMENT)
                .join(DEFENSE_GROUP)
                .on(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID.eq(DEFENSE_ARRANGEMENT.DEFENSE_ARRANGEMENT_ID))
                .join(SCHOOLROOM)
                .on(DEFENSE_GROUP.SCHOOLROOM_ID.eq(SCHOOLROOM.SCHOOLROOM_ID))
                .join(BUILDING)
                .on(SCHOOLROOM.BUILDING_ID.eq(BUILDING.BUILDING_ID))
                .leftJoin(GRADUATION_DESIGN_TEACHER)
                .on(DEFENSE_GROUP.LEADER_ID.eq(GRADUATION_DESIGN_TEACHER.GRADUATION_DESIGN_TEACHER_ID))
                .leftJoin(STAFF)
                .on(GRADUATION_DESIGN_TEACHER.STAFF_ID.eq(STAFF.STAFF_ID))
                .leftJoin(USERS)
                .on(USERS.USERNAME.eq(DEFENSE_GROUP.SECRETARY_ID))
                .where(DEFENSE_ARRANGEMENT.GRADUATION_DESIGN_RELEASE_ID.eq(graduationDesignReleaseId))
                .orderBy<Timestamp>(DEFENSE_GROUP.CREATE_TIME.asc())
                .fetch()

        return buildDefenseGroupList(records)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(defenseGroup: DefenseGroup) {
        defenseGroupDao.insert(defenseGroup)
    }

    override fun update(defenseGroup: DefenseGroup) {
        defenseGroupDao.update(defenseGroup)
    }

    override fun deleteById(id: String) {
        defenseGroupDao.deleteById(id)
    }

    /**
     * 构建组
     *
     * @param records 数据
     */
    private fun buildDefenseGroupList(records: Result<Record>): List<DefenseGroupBean> {
        val defenseGroupBeens = ArrayList<DefenseGroupBean>()
        for (r in records) {
            val defenseGroupBean = DefenseGroupBean()
            defenseGroupBean.defenseGroupId = r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_ID)
            defenseGroupBean.defenseGroupName = r.getValue(DEFENSE_GROUP.DEFENSE_GROUP_NAME)
            defenseGroupBean.schoolroomId = r.getValue(SCHOOLROOM.SCHOOLROOM_ID)
            defenseGroupBean.note = r.getValue(DEFENSE_GROUP.NOTE)
            defenseGroupBean.createTime = r.getValue<Timestamp>(DEFENSE_GROUP.CREATE_TIME)
            defenseGroupBean.leaderId = r.getValue(DEFENSE_GROUP.LEADER_ID)
            defenseGroupBean.secretaryId = r.getValue(DEFENSE_GROUP.SECRETARY_ID)
            defenseGroupBean.defenseArrangementId = r.getValue(DEFENSE_GROUP.DEFENSE_ARRANGEMENT_ID)
            defenseGroupBean.buildingName = r.getValue(BUILDING.BUILDING_NAME)
            defenseGroupBean.buildingCode = r.getValue(SCHOOLROOM.BUILDING_CODE)
            defenseGroupBean.staffName = r.getValue(GRADUATION_DESIGN_TEACHER.STAFF_REAL_NAME)
            defenseGroupBean.studentName = r.getValue(USERS.REAL_NAME)

            defenseGroupBeens.add(defenseGroupBean)
        }
        return defenseGroupBeens
    }
}