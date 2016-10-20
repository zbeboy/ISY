package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemMailbox;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by lenovo on 2016-09-17.
 */
public interface SystemMailboxService {

    /**
     * 保存
     *
     * @param systemMailbox
     */
    void save(SystemMailbox systemMailbox);

    /**
     * 分页查询
     *
     * @param dataTablesUtils
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
