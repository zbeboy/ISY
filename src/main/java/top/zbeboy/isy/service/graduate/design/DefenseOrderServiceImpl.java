package top.zbeboy.isy.service.graduate.design;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.DefenseOrderDao;
import top.zbeboy.isy.domain.tables.pojos.DefenseOrder;
import top.zbeboy.isy.domain.tables.records.DefenseOrderRecord;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseOrderBean;

import javax.annotation.Resource;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.DEFENSE_GROUP;
import static top.zbeboy.isy.domain.Tables.DEFENSE_ORDER;
import static top.zbeboy.isy.domain.Tables.SCORE_TYPE;

/**
 * Created by zbeboy on 2017/7/12.
 */
@Slf4j
@Service("defenseOrderService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DefenseOrderServiceImpl implements DefenseOrderService {

    private final DSLContext create;

    @Resource
    private DefenseOrderDao defenseOrderDao;

    @Autowired
    public DefenseOrderServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public DefenseOrder findById(String id) {
        return defenseOrderDao.findById(id);
    }

    @Override
    public Result<Record> findAll(DefenseOrderBean condition) {
        Result<Record> records;
        Condition a = searchCondition(condition);
        a = otherCondition(a, condition);
        if (ObjectUtils.isEmpty(a)) {
            records = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .orderBy(DEFENSE_ORDER.SORT_NUM)
                    .fetch();
        } else {
            records = create.select()
                    .from(DEFENSE_ORDER)
                    .leftJoin(SCORE_TYPE)
                    .on(DEFENSE_ORDER.SCORE_TYPE_ID.eq(SCORE_TYPE.SCORE_TYPE_ID))
                    .leftJoin(DEFENSE_GROUP)
                    .on(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(DEFENSE_GROUP.DEFENSE_GROUP_ID))
                    .where(a)
                    .orderBy(DEFENSE_ORDER.SORT_NUM)
                    .fetch();
        }
        return records;
    }

    @Override
    public DefenseOrderRecord findBySortNumAndDefenseGroupId(int sortNum, String defenseGroupId) {
        return create.selectFrom(DEFENSE_ORDER)
                .where(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseGroupId).and(DEFENSE_ORDER.SORT_NUM.eq(sortNum)))
                .fetchOne();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(List<DefenseOrder> defenseOrders) {
        defenseOrderDao.insert(defenseOrders);
    }

    @Override
    public void deleteByDefenseGroupId(String defenseGroupId) {
        create.deleteFrom(DEFENSE_ORDER)
                .where(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseGroupId))
                .execute();
    }

    @Override
    public void update(DefenseOrder defenseOrder) {
        defenseOrderDao.update(defenseOrder);
    }

    /**
     * 搜索条件
     *
     * @param condition 条件
     * @return 条件
     */
    public Condition searchCondition(DefenseOrderBean condition) {
        Condition a = null;
        String studentName = StringUtils.trimWhitespace(condition.getStudentName());
        String studentNumber = StringUtils.trimWhitespace(condition.getStudentNumber());
        if (StringUtils.hasLength(studentName)) {
            a = DEFENSE_ORDER.STUDENT_NAME.like(SQLQueryUtils.likeAllParam(studentName));
        }

        if (StringUtils.hasLength(studentNumber)) {
            if (!ObjectUtils.isEmpty(a)) {
                a = a.and(DEFENSE_ORDER.STUDENT_NUMBER.eq(studentNumber));
            } else {
                a = DEFENSE_ORDER.STUDENT_NUMBER.eq(studentNumber);
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a                搜索条件
     * @param defenseOrderBean 额外参数
     */
    private Condition otherCondition(Condition a, DefenseOrderBean defenseOrderBean) {
        if (!ObjectUtils.isEmpty(defenseOrderBean)) {
            if (StringUtils.hasLength(defenseOrderBean.getDefenseGroupId())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseOrderBean.getDefenseGroupId()));
                } else {
                    a = DEFENSE_ORDER.DEFENSE_GROUP_ID.eq(defenseOrderBean.getDefenseGroupId());
                }
            }
        }
        return a;
    }
}
