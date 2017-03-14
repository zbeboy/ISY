package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Department;
import top.zbeboy.isy.domain.tables.records.DepartmentRecord;
import top.zbeboy.isy.web.bean.data.department.DepartmentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-21.
 */
public interface DepartmentService {

    /**
     * 通过院id查询全部系
     *
     * @param collegeId 院id
     * @param b         状态
     * @return 院下全部系
     */
    Result<DepartmentRecord> findByCollegeIdAndIsDel(int collegeId, Byte b);

    /**
     * 保存
     *
     * @param department 系
     */
    void save(Department department);

    /**
     * 更新
     *
     * @param department 系
     */
    void update(Department department);

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<DepartmentBean> dataTablesUtils);

    /**
     * 系总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<DepartmentBean> dataTablesUtils);

    /**
     * 院下 系名查询 注：等于系名
     *
     * @param departmentName 系名
     * @param collegeId      院id
     * @return 数据
     */
    Result<DepartmentRecord> findByDepartmentNameAndCollegeId(String departmentName, int collegeId);

    /**
     * 查找院下不等于该系id的系名
     *
     * @param departmentName 系名
     * @param departmentId   系id
     * @param collegeId      院id
     * @return 数据
     */
    Result<DepartmentRecord> findByDepartmentNameAndCollegeIdNeDepartmentId(String departmentName, int departmentId, int collegeId);

    /**
     * 通过id关联查询系
     *
     * @param id 系id
     * @return 系
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 通过id查询系
     *
     * @param id 系id
     * @return 系
     */
    Department findById(int id);
}
