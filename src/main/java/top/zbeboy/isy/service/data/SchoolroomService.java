package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Schoolroom;
import top.zbeboy.isy.domain.tables.records.SchoolroomRecord;
import top.zbeboy.isy.web.bean.data.schoolroom.SchoolroomBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/31.
 */
public interface SchoolroomService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 教室
     */
    Schoolroom findById(int id);

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 通过教室与楼id查询
     *
     * @param buildingCode 教室
     * @param buildingId   楼id
     * @return 数据
     */
    Result<SchoolroomRecord> findByBuildingCodeAndBuildingId(String buildingCode, int buildingId);

    /**
     * 检验一栋里是否有相同教室名
     *
     * @param buildingCode 教室
     * @param schoolroomId 教室id
     * @param buildingId   楼id
     * @return 数据
     */
    Result<SchoolroomRecord> findByBuildingCodeAndBuildingIdNeSchoolroomId(String buildingCode, int schoolroomId, int buildingId);

    /**
     * 通过楼id与状态查询
     *
     * @param buildingId 楼id
     * @param b          状态
     * @return 数据
     */
    Result<SchoolroomRecord> findByBuildingIdAndIsDel(int buildingId, Byte b);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<SchoolroomBean> dataTablesUtils);

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
    int countByCondition(DataTablesUtils<SchoolroomBean> dataTablesUtils);

    /**
     * 保存
     *
     * @param schoolroom 数据
     */
    void save(Schoolroom schoolroom);

    /**
     * 更新
     *
     * @param schoolroom 数据
     */
    void update(Schoolroom schoolroom);

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    void updateIsDel(List<Integer> ids, Byte isDel);
}
