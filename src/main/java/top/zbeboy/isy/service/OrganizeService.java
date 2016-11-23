package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Organize;
import top.zbeboy.isy.domain.tables.records.OrganizeRecord;
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-21.
 */
public interface OrganizeService {

    /**
     * 根据专业id查询全部年级
     *
     * @param scienceId 专业id
     * @return 专业下全部年级
     */
    Result<Record1<String>> findByScienceIdAndDistinctGrade(int scienceId);

    /**
     * 根据专业ids查询
     *
     * @param scienceIds 专业ids
     * @return 班级
     */
    Result<OrganizeRecord> findInScienceIds(List<Integer> scienceIds);


    /**
     * 根据系id查询全部年级
     *
     * @param departmentId 系id
     * @return 系下全部年级
     */
    Result<Record1<String>> findByDepartmentIdAndDistinctGrade(int departmentId);

    /**
     * 查找专业下不等于该班级id的班级名
     *
     * @param organizeName 班级名
     * @param organizeId   班级id
     * @param scienceId    专业id
     * @return 数据
     */
    Result<OrganizeRecord> findByOrganizeNameAndScienceIdNeOrganizeId(String organizeName, int organizeId, int scienceId);

    /**
     * 根据年级查询全部班级
     *
     * @param grade 年级
     * @return 年级下全部班级
     */
    Result<OrganizeRecord> findByGrade(String grade);

    /**
     * 保存
     *
     * @param organize 班级
     */
    void save(Organize organize);

    /**
     * 更新
     *
     * @param organize 班级
     */
    void update(Organize organize);

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 通过id关联查询班级
     *
     * @param id 班级id
     * @return 班级
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 通过id查询班级
     *
     * @param id 班级id
     * @return 班级
     */
    Organize findById(int id);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<OrganizeBean> dataTablesUtils);

    /**
     * 班级总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<OrganizeBean> dataTablesUtils);

    /**
     * 专业下 班级名查询 注：等于班级名
     *
     * @param organizeName 班级名
     * @param scienceId    专业id
     * @return 数据
     */
    Result<OrganizeRecord> findByOrganizeNameAndScienceId(String organizeName, int scienceId);
}
