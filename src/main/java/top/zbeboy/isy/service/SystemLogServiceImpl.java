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
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Timestamp;

import static top.zbeboy.isy.domain.Tables.SYSTEM_LOG;

/**
 * Created by lenovo on 2016-09-11.
 */
@Service("systemLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemLogServiceImpl extends DataTablesPlugin<SystemLogBean> implements SystemLogService {

    private final Logger log = LoggerFactory.getLogger(SystemLogServiceImpl.class);

    private final DSLContext create;

    private SystemLogDao systemLogDao;

    @Autowired
    public SystemLogServiceImpl(DSLContext dslContext, Configuration configuration) {
        this.create = dslContext;
        this.systemLogDao = new SystemLogDao(configuration);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemLog systemLog) {
        systemLogDao.insert(systemLog);
    }

    @Override
    public void deleteByOperatingTime(Timestamp operatingTime) {
        create.deleteFrom(SYSTEM_LOG).where(SYSTEM_LOG.OPERATING_TIME.le(operatingTime)).execute();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, SYSTEM_LOG);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, SYSTEM_LOG);
    }

    @Override
    public int countByCondition(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, SYSTEM_LOG);
    }

    /**
     * 系统日志全局搜索条件
     *
     * @param dataTablesUtils
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<SystemLogBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String username = StringUtils.trimWhitespace(search.getString("username"));
            String behavior = StringUtils.trimWhitespace(search.getString("behavior"));
            String ipAddress = StringUtils.trimWhitespace(search.getString("ipAddress"));
            if (StringUtils.hasLength(username)) {
                a = SYSTEM_LOG.USERNAME.like(SQLQueryUtils.likeAllParam(username));
            }

            if (StringUtils.hasLength(behavior)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SYSTEM_LOG.BEHAVIOR.like(SQLQueryUtils.likeAllParam(behavior));
                } else {
                    a = a.and(SYSTEM_LOG.BEHAVIOR.like(SQLQueryUtils.likeAllParam(behavior)));
                }
            }

            if (StringUtils.hasLength(ipAddress)) {
                if (ObjectUtils.isEmpty(a)) {
                    a = SYSTEM_LOG.IP_ADDRESS.like(SQLQueryUtils.likeAllParam(ipAddress));
                } else {
                    a = a.and(SYSTEM_LOG.IP_ADDRESS.like(SQLQueryUtils.likeAllParam(ipAddress)));
                }
            }
        }
        return a;
    }

    /**
     * 系统日志排序
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    @Override
    public void sortCondition(DataTablesUtils<SystemLogBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_log_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_LOG.SYSTEM_LOG_ID.asc();
                } else {
                    sortString = SYSTEM_LOG.SYSTEM_LOG_ID.desc();
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_LOG.USERNAME.asc();
                } else {
                    sortString = SYSTEM_LOG.USERNAME.desc();
                }
            }

            if ("behavior".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_LOG.BEHAVIOR.asc();
                } else {
                    sortString = SYSTEM_LOG.BEHAVIOR.desc();
                }
            }

            if ("operating_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortTimestamp = SYSTEM_LOG.OPERATING_TIME.asc();
                } else {
                    sortTimestamp = SYSTEM_LOG.OPERATING_TIME.desc();
                }
            }

            if ("ip_address".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_LOG.IP_ADDRESS.asc();
                } else {
                    sortString = SYSTEM_LOG.IP_ADDRESS.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type, SYSTEM_LOG.OPERATING_TIME);
    }
}
