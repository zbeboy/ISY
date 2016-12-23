package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean;
import top.zbeboy.isy.web.bean.internship.regulate.InternshipRegulateBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by zbeboy on 2016/12/23.
 */
public interface InternshipRegulateService {

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
    int countByCondition(DataTablesUtils<InternshipRegulateBean> dataTablesUtils,InternshipRegulateBean internshipRegulateBean);
}
