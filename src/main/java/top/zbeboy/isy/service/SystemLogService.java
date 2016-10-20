package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by lenovo on 2016-09-11.
 */
public interface SystemLogService {

    /**
     * 保存
     *
     * @param systemLog
     */
    void save(SystemLog systemLog);

    /**
     * 分页查询
     *
     * @param dataTablesUtils
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<SystemLogBean> dataTablesUtils);

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
    int countByCondition(DataTablesUtils<SystemLogBean> dataTablesUtils);
}
