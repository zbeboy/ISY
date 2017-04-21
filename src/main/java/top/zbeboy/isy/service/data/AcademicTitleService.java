package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.AcademicTitle;
import top.zbeboy.isy.domain.tables.records.AcademicTitleRecord;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/4/21.
 */
public interface AcademicTitleService {

    /**
     * 查询全部职称
     *
     * @return 全部职称
     */
    List<AcademicTitle> findAll();

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 职称
     */
    AcademicTitle findById(int id);

    /**
     * 通过职称查询
     *
     * @param academicTitleName 职称
     * @return 职称
     */
    List<AcademicTitle> findByAcademicTitleName(String academicTitleName);

    /**
     * 保存
     *
     * @param academicTitle 职称
     */
    void save(AcademicTitle academicTitle);

    /**
     * 更新
     *
     * @param academicTitle 职称
     */
    void update(AcademicTitle academicTitle);

    /**
     * 通过职称查询 注：不等于职称id
     *
     * @param academicTitleName 职称
     * @param academicTitleId   职称id
     * @return 职称
     */
    Result<AcademicTitleRecord> findByAcademicTitleNameNeAcademicTitleId(String academicTitleName, int academicTitleId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<AcademicTitle> dataTablesUtils);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<AcademicTitle> dataTablesUtils);
}
