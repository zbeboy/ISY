package top.zbeboy.isy.service.system;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-09-17.
 */
public interface SystemMailboxService {

    /**
     * 保存
     *
     * @param systemMailbox 系统邮件
     */
    void save(SystemMailbox systemMailbox);

    /**
     * 根据发送时间删除
     *
     * @param sendTime 发送时间
     */
    void deleteBySendTime(Timestamp sendTime);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<SystemMailboxBean> dataTablesUtils);

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils);
}
