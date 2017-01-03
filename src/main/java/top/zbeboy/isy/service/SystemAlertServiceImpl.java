package top.zbeboy.isy.service;

import com.alibaba.fastjson.JSON;
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
import top.zbeboy.isy.domain.tables.daos.SystemAlertDao;
import top.zbeboy.isy.domain.tables.pojos.SystemAlert;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static top.zbeboy.isy.domain.Tables.SYSTEM_ALERT;
import static top.zbeboy.isy.domain.Tables.SYSTEM_ALERT_TYPE;

/**
 * Created by lenovo on 2016-12-24.
 */
@Service("systemAlertService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemAlertServiceImpl implements SystemAlertService {

    private final Logger log = LoggerFactory.getLogger(SystemAlertServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SystemAlertDao systemAlertDao;

    @Autowired
    public SystemAlertServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Override
    public Result<Record> findAllByPageForShow(int pageNum, int pageSize, String username, boolean isSee) {
        Byte b = 0;
        if (isSee) {
            b = 1;
        }
        return create.select()
                .from(SYSTEM_ALERT)
                .join(SYSTEM_ALERT_TYPE)
                .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.IS_SEE.eq(b)))
                .orderBy(SYSTEM_ALERT.ALERT_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public int countAllForShow(String username, boolean isSee) {
        Byte b = 0;
        if (isSee) {
            b = 1;
        }
        Record1<Integer> record = create.selectCount()
                .from(SYSTEM_ALERT)
                .where(SYSTEM_ALERT.USERNAME.eq(username).and(SYSTEM_ALERT.IS_SEE.eq(b)))
                .fetchOne();
        return record.value1();
    }

    @Override
    public Result<Record> findAllByPage(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean) {
        int pageNum = paginationUtils.getPageNum();
        int pageSize = paginationUtils.getPageSize();
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, systemAlertBean);
        return create.select()
                .from(SYSTEM_ALERT)
                .join(SYSTEM_ALERT_TYPE)
                .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                .where(a)
                .orderBy(SYSTEM_ALERT.ALERT_DATE.desc())
                .limit((pageNum - 1) * pageSize, pageSize)
                .fetch();
    }

    @Override
    public List<SystemAlertBean> dealData(PaginationUtils paginationUtils, Result<Record> records, SystemAlertBean systemAlertBean) {
        List<SystemAlertBean> systemAlertBeens = new ArrayList<>();
        if (records.isNotEmpty()) {
            systemAlertBeens = records.into(SystemAlertBean.class);
            systemAlertBeens.forEach(i -> {
                i.setAlertDateStr(DateTimeUtils.formatDate(i.getAlertDate(), "yyyy年MM月dd日 HH:mm:ss"));
            });
            paginationUtils.setTotalDatas(countByCondition(paginationUtils, systemAlertBean));
        }
        return systemAlertBeens;
    }

    @Override
    public int countByCondition(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean) {
        Record1<Integer> count;
        Condition a = searchCondition(paginationUtils);
        a = otherCondition(a, systemAlertBean);
        if (ObjectUtils.isEmpty(a)) {
            SelectJoinStep<Record1<Integer>> selectJoinStep = create.selectCount()
                    .from(SYSTEM_ALERT);
            count = selectJoinStep.fetchOne();
        } else {
            SelectConditionStep<Record1<Integer>> selectConditionStep = create.selectCount()
                    .from(SYSTEM_ALERT)
                    .join(SYSTEM_ALERT_TYPE)
                    .on(SYSTEM_ALERT.SYSTEM_ALERT_TYPE_ID.eq(SYSTEM_ALERT_TYPE.SYSTEM_ALERT_TYPE_ID))
                    .where(a);
            count = selectConditionStep.fetchOne();
        }
        return count.value1();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemAlert systemAlert) {
        systemAlertDao.insert(systemAlert);
    }

    @Override
    public void deleteByAlertDate(Timestamp timestamp) {
        create.deleteFrom(SYSTEM_ALERT).where(SYSTEM_ALERT.ALERT_DATE.le(timestamp)).execute();
    }

    /**
     * 搜索条件
     *
     * @param paginationUtils 分页工具
     * @return 条件
     */
    public Condition searchCondition(PaginationUtils paginationUtils) {
        Condition a = null;
        JSONObject search = JSON.parseObject(paginationUtils.getSearchParams());
        if (!ObjectUtils.isEmpty(search)) {
            String alertContent = StringUtils.trimWhitespace(search.getString("alertContent"));
            if (StringUtils.hasLength(alertContent)) {
                a = SYSTEM_ALERT.ALERT_CONTENT.like(SQLQueryUtils.likeAllParam(alertContent));
            }
        }
        return a;
    }

    /**
     * 其它条件参数
     *
     * @param a               搜索条件
     * @param systemAlertBean 额外参数
     * @return 条件
     */
    private Condition otherCondition(Condition a, SystemAlertBean systemAlertBean) {
        if (!ObjectUtils.isEmpty(systemAlertBean)) {
            if (StringUtils.hasLength(systemAlertBean.getUsername())) {
                if (!ObjectUtils.isEmpty(a)) {
                    a = a.and(SYSTEM_ALERT.USERNAME.eq(systemAlertBean.getUsername()));
                } else {
                    a = SYSTEM_ALERT.USERNAME.eq(systemAlertBean.getUsername());
                }
            }
        }
        return a;
    }
}
