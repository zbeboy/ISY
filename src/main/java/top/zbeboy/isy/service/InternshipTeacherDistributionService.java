package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

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
    List<InternshipTeacherDistributionBean> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, String internshipReleaseId);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll(String internshipReleaseId);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils,String internshipReleaseId);
}
