package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Science;
import top.zbeboy.isy.domain.tables.records.ScienceRecord;
import top.zbeboy.isy.web.bean.data.science.ScienceBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-21.
 */
public interface ScienceService {

    /**
     * 通过系id查询全部专业
     *
     * @param departmentId 系id
     * @return 系下全部专业
     */
    Result<ScienceRecord> findByDepartmentId(int departmentId);

    /**
     * 通过年级查询全部专业
     *
     * @param grade 年级
     * @param departmentId 系id
     * @return 年级下全部专业
     */
    Result<Record2<String,Integer>> findByGradeAndDepartmentId(String grade,int departmentId);

    /**
     * 保存
     *
     * @param science 专业
     */
    void save(Science science);

    /**
     * 更新
     *
     * @param science 专业
     */
    void update(Science science);

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 通过id查询
     *
     * @param id 专业id
     * @return 专业
     */
    Science findById(int id);

    /**
     * 通过id关联查询
     *
     * @param id 专业id
     * @return 专业
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<ScienceBean> dataTablesUtils);

    /**
     * 专业总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<ScienceBean> dataTablesUtils);

    /**
     * 系下 专业名查询 注：等于专业名
     *
     * @param scienceName  专业名
     * @param departmentId 系id
     * @return 数据
     */
    Result<ScienceRecord> findByScienceNameAndDepartmentId(String scienceName, int departmentId);

    /**
     * 查找系下不等于该专业id的专业名
     *
     * @param scienceName  专业名
     * @param scienceId    专业id
     * @param departmentId 系id
     * @return 数据
     */
    Result<ScienceRecord> findByScienceNameAndDepartmentIdNeScienceId(String scienceName, int scienceId, int departmentId);
}
