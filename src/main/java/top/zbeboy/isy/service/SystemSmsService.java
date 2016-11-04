package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemSms;
import top.zbeboy.isy.web.bean.system.sms.SystemSmsBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by lenovo on 2016-08-22.
 */
public interface SystemSmsService {

    /**
     * 保存
     *
     * @param systemSms 系统短信
     */
    void save(SystemSms systemSms);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<SystemSmsBean> dataTablesUtils);

    /**
     * 系统短信总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<SystemSmsBean> dataTablesUtils);
}
