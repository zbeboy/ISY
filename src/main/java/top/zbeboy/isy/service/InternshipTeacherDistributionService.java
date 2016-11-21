package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by zbeboy on 2016/11/21.
 */
public interface InternshipTeacherDistributionService {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils);

    /**
     * 学校总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils);
}
