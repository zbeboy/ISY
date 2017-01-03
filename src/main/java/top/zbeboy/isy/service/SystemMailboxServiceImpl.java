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
import top.zbeboy.isy.domain.tables.daos.SystemMailboxDao;
import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;
import top.zbeboy.isy.service.plugin.DataTablesPlugin;
import top.zbeboy.isy.service.util.SQLQueryUtils;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;

import static top.zbeboy.isy.domain.Tables.SYSTEM_MAILBOX;

/**
 * Created by lenovo on 2016-09-17.
 */
@Service("systemMailboxService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SystemMailboxServiceImpl extends DataTablesPlugin<SystemMailboxBean> implements SystemMailboxService {

    private final Logger log = LoggerFactory.getLogger(SystemMailboxServiceImpl.class);

    private final DSLContext create;

    @Resource
    private SystemMailboxDao systemMailboxDao;

    @Autowired
    public SystemMailboxServiceImpl(DSLContext dslContext) {
        this.create = dslContext;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void save(SystemMailbox systemMailbox) {
        systemMailboxDao.insert(systemMailbox);
    }

    @Override
    public void deleteBySendTime(Timestamp sendTime) {
        create.deleteFrom(SYSTEM_MAILBOX).where(SYSTEM_MAILBOX.SEND_TIME.le(sendTime)).execute();
    }

    @Override
    public Result<Record> findAllByPage(DataTablesUtils<SystemMailboxBean> dataTablesUtils) {
        return dataPagingQueryAll(dataTablesUtils, create, SYSTEM_MAILBOX);
    }

    @Override
    public int countAll() {
        return statisticsAll(create, SYSTEM_MAILBOX);
    }

    @Override
    public int countByCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils) {
        return statisticsWithCondition(dataTablesUtils, create, SYSTEM_MAILBOX);
    }

    /**
     * 系统邮件全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    @Override
    public Condition searchCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils) {
        Condition a = null;

        JSONObject search = dataTablesUtils.getSearch();
        if (!ObjectUtils.isEmpty(search)) {
            String acceptMail = StringUtils.trimWhitespace(search.getString("acceptMail"));
            if (StringUtils.hasLength(acceptMail)) {
                a = SYSTEM_MAILBOX.ACCEPT_MAIL.like(SQLQueryUtils.likeAllParam(acceptMail));
            }
        }
        return a;
    }

    /**
     * 系统邮件排序
     *
     * @param dataTablesUtils datatables工具类
     * @param selectConditionStep 条件
     */
    @Override
    public void sortCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils, SelectConditionStep<Record> selectConditionStep, SelectJoinStep<Record> selectJoinStep, int type) {
        String orderColumnName = dataTablesUtils.getOrderColumnName();
        String orderDir = dataTablesUtils.getOrderDir();
        boolean isAsc = "asc".equalsIgnoreCase(orderDir);
        if (StringUtils.hasLength(orderColumnName)) {
            if ("system_mailbox_id".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_MAILBOX.SYSTEM_MAILBOX_ID.asc();
                } else {
                    sortString = SYSTEM_MAILBOX.SYSTEM_MAILBOX_ID.desc();
                }
            }

            if ("send_time".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortTimestamp = SYSTEM_MAILBOX.SEND_TIME.asc();
                } else {
                    sortTimestamp = SYSTEM_MAILBOX.SEND_TIME.desc();
                }
            }

            if ("accept_mail".equalsIgnoreCase(orderColumnName)) {
                if (isAsc) {
                    sortString = SYSTEM_MAILBOX.ACCEPT_MAIL.asc();
                } else {
                    sortString = SYSTEM_MAILBOX.ACCEPT_MAIL.desc();
                }
            }

        }

        sortToFinish(selectConditionStep, selectJoinStep, type);
    }
}
