package top.zbeboy.isy.glue.system;

import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.system.mailbox.SystemMailboxBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-04-08.
 */
public interface SystemMailboxGlue {
    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    ResultUtils<List<SystemMailboxBean>> findAllByPage(DataTablesUtils<SystemMailboxBean> dataTablesUtils);

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    long countAll(DataTablesUtils<SystemMailboxBean> dataTablesUtils);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    long countByCondition(DataTablesUtils<SystemMailboxBean> dataTablesUtils);
}
