package top.zbeboy.isy.service.system;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import top.zbeboy.isy.domain.tables.daos.SystemLogDao;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.elastic.repository.SystemLogElasticRepository;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;

import static top.zbeboy.isy.domain.Tables.SYSTEM_LOG;

/**
 * Created by lenovo on 2016-09-11.
 */
@Slf4j
@Service("systemLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemLogServiceImpl extends DataTablesPlugin<SystemLogBean> implements SystemLogService {

    private final DSLContext create;

    @Resource
    private SystemLogDao systemLogDao;

    @Resource
    private SystemLogElasticRepository systemLogElasticRepository;

    @Autowired
    public SystemLogServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemLog systemLog) {
        systemLogDao.insert(systemLog);
        systemLogElasticRepository.save(new SystemLogElastic(systemLog.getSystemLogId(),systemLog.getBehavior(),systemLog.getOperatingTime(),systemLog.getUsername(),systemLog.getIpAddress()));
    }

    @Override
    public void deleteByOperatingTime(Timestamp operatingTime) {
        create.deleteFrom(SYSTEM_LOG).where(SYSTEM_LOG.OPERATING_TIME.le(operatingTime)).execute();
        systemLogElasticRepository.deleteByOperatingTimeLessThanEqual(operatingTime.getTime());
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
     * @param dataTablesUtils datatables工具类
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
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<SystemLogBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        SortField[] sortField = null;
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_log_id".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = SYSTEM_LOG.SYSTEM_LOG_ID.asc();
                } else {
                    sortField[0] = SYSTEM_LOG.SYSTEM_LOG_ID.desc();
                }
            }

            if ("username".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SYSTEM_LOG.USERNAME.asc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.asc();
                } else {
                    sortField[0] = SYSTEM_LOG.USERNAME.desc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.desc();
                }
            }

            if ("behavior".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SYSTEM_LOG.BEHAVIOR.asc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.asc();
                } else {
                    sortField[0] = SYSTEM_LOG.BEHAVIOR.desc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.desc();
                }
            }

            if ("operating_time".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[1];
                if (isAsc) {
                    sortField[0] = SYSTEM_LOG.OPERATING_TIME.asc();
                } else {
                    sortField[0] = SYSTEM_LOG.OPERATING_TIME.desc();
                }
            }

            if ("ip_address".equalsIgnoreCase(orderColumnName)) {
                sortField = new SortField[2];
                if (isAsc) {
                    sortField[0] = SYSTEM_LOG.IP_ADDRESS.asc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.asc();
                } else {
                    sortField[0] = SYSTEM_LOG.IP_ADDRESS.desc();
                    sortField[1] = SYSTEM_LOG.SYSTEM_LOG_ID.desc();
                }
            }
        }
        sortToFinish(selectConditionStep, selectJoinStep, type, sortField);
    }
}
