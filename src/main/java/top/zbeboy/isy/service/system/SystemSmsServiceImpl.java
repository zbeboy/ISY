package top.zbeboy.isy.service.system;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
import top.zbeboy.isy.elastic.pojo.SystemSmsElastic;
import top.zbeboy.isy.elastic.repository.SystemSmsElasticRepository;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;

import static top.zbeboy.isy.domain.Tables.SYSTEM_SMS;

/**
 * Created by lenovo on 2016-08-22.
 */
@Slf4j
@Service("systemSmsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemSmsServiceImpl extends DataTablesPlugin<SystemSmsBean> implements SystemSmsService {

    private final DSLContext create;

    @Resource
    private SystemSmsDao systemSmsDao;

    @Resource
    private SystemSmsElasticRepository systemSmsElasticRepository;

    @Autowired
    public SystemSmsServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemSms systemSms) {
        systemSmsDao.insert(systemSms);
        systemSmsElasticRepository.save(new SystemSmsElastic(systemSms.getSystemSmsId(),systemSms.getSendTime(),systemSms.getAcceptPhone(),systemSms.getSendCondition()));
    }

    @Override
    public void deleteBySendTime(Timestamp sendTime) {
        create.deleteFrom(SYSTEM_SMS).where(SYSTEM_SMS.SEND_TIME.le(sendTime)).execute();
        systemSmsElasticRepository.deleteBySendTimeLessThanEqual(sendTime.getTime());
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
     * @param dataTablesUtils datatables工具类
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
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_sms_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = SYSTEM_SMS.SYSTEM_SMS_ID.asc();
                } else {
                    sortField[0] = SYSTEM_SMS.SYSTEM_SMS_ID.desc();
                }
            }

            if ("send_time".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = SYSTEM_SMS.SEND_TIME.asc();
                } else {
                    sortField[0] = SYSTEM_SMS.SEND_TIME.desc();
                }
            }

            if ("accept_phone".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SYSTEM_SMS.ACCEPT_PHONE.asc();
                    sortField[1] = SYSTEM_SMS.SYSTEM_SMS_ID.asc();
                } else {
                    sortField[0] = SYSTEM_SMS.ACCEPT_PHONE.desc();
                    sortField[1] = SYSTEM_SMS.SYSTEM_SMS_ID.desc();
                }
            }

            if ("send_condition".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SYSTEM_SMS.SEND_CONDITION.asc();
                    sortField[1] = SYSTEM_SMS.SYSTEM_SMS_ID.asc();
                } else {
                    sortField[0] = SYSTEM_SMS.SEND_CONDITION.desc();
                    sortField[1] = SYSTEM_SMS.SYSTEM_SMS_ID.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
