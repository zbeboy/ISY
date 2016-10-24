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
import top.zbeboy.isy.domain.tables.daos.SystemSmsDao;
import top.zbeboy.isy.domain.tables.pojos.SystemSms;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Timestamp;

import static top.zbeboy.isy.domain.Tables.SYSTEM_SMS;

/**
 * Created by lenovo on 2016-08-22.
 */
@Service("systemSmsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemSmsServiceImpl extends DataTablesPlugin<SystemSmsBean> implements SystemSmsService {

    private final Logger log = LoggerFactory.getLogger(SystemSmsServiceImpl.class);

    private final DSLContext create;

    private SystemSmsDao systemSmsDao;

    @Autowired
    public SystemSmsServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.systemSmsDao = new SystemSmsDao(configuration);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemSms systemSms) {
        systemSmsDao.insert(systemSms);
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<SystemSmsBean> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, SYSTEM_SMS);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, SYSTEM_SMS);
    }

    @Override
    public int countByCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, SYSTEM_SMS);
    }

    /**
     * 系统短信全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String acceptPhone = StringUtils.trimWhitespace(search.getString("acceptPhone"));
            if (StringUtils.hasLength(acceptPhone)) {
                a = SYSTEM_SMS.ACCEPT_PHONE.like(SQLQueryUtils.likeAllParam(acceptPhone));
            }
        }
        return a;
    }

    /**
     * 系统短信排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = orderDir.equalsIgnoreCase("asc");
        SortField<String> a = null;
        SortField<Timestamp> b = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if (orderColumnName.equalsIgnoreCase("system_sms_id")) {
                if (isAsc) {
                    a = SYSTEM_SMS.SYSTEM_SMS_ID.asc();
                } else {
                    a = SYSTEM_SMS.SYSTEM_SMS_ID.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("send_time")) {
                if (isAsc) {
                    b = SYSTEM_SMS.SEND_TIME.asc();
                } else {
                    b = SYSTEM_SMS.SEND_TIME.desc();
                }
            }

            if (orderColumnName.equalsIgnoreCase("accept_phone")) {
                if (isAsc) {
                    a = SYSTEM_SMS.ACCEPT_PHONE.asc();
                } else {
                    a = SYSTEM_SMS.ACCEPT_PHONE.desc();
                }
            }

        }

        if (!ObjectUtils.isEmpty(a)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(a);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(a);
            }

        } else if (!ObjectUtils.isEmpty(b)) {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(b);
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(b);
            }
        } else {
            if (type == CONDITION_TYPE) {
                selectConditionStep.orderBy(SYSTEM_SMS.SEND_TIME.desc());
            }

            if (type == JOIN_TYPE) {
                selectJoinStep.orderBy(SYSTEM_SMS.SEND_TIME.desc());
            }
        }
    }
}
