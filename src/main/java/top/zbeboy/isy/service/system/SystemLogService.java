package top.zbeboy.isy.service.system;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemLog;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.sql.Timestamp;

/**
 * Created by lenovo on 2016-09-11.
 */
public interface SystemLogService {

    /**
     * 保存
     *
     * @param systemLog 系统日志
     */
    void save(SystemLog systemLog);

    /**
     * 根据操作时间删除
     *
     * @param operatingTime 操作时间
     */
    void deleteByOperatingTime(Timestamp operatingTime);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
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
