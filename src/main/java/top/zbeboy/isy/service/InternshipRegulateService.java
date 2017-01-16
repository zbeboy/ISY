package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipRegulate;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2016/12/23.
 */
public interface InternshipRegulateService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 监管记录
     */
    InternshipRegulate findById(String id);

    /**
     * 保存实习监管
     *
     * @param internshipRegulate 数据
     */
    void save(InternshipRegulate internshipRegulate);

    /**
     * 更新实习监管
     *
     * @param internshipRegulate 数据
     */
    void update(InternshipRegulate internshipRegulate);

    /**
     * 批量删除
     *
     * @param ids ids
     */
    void batchDelete(List<String> ids);

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(InternshipRegulateBean internshipRegulateBean);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean);

    /**
     * 导出
     *
     * @param dataTablesUtils        datatables工具类
     * @param internshipRegulateBean 监管
     * @return 导出数据
     */
    Result<Record> exportData(DataTablesUtils<InternshipRegulateBean> dataTablesUtils, InternshipRegulateBean internshipRegulateBean);
}
