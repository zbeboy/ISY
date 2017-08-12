package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Building;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.records.BuildingRecord;
import top.zbeboy.isy.web.bean.data.building.BuildingBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/27.
 */
public interface BuildingService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 楼
     */
    Building findById(int id);

    /**
     * 通过id关联查询楼
     *
     * @param id 楼id
     * @return 楼
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<BuildingBean> dataTablesUtils);

    /**
     * 根据楼名与院id查询
     *
     * @param buildingName 楼名
     * @param collegeId    院
     * @return 数据
     */
    Result<BuildingRecord> findByBuildingNameAndCollegeId(String buildingName, int collegeId);

    /**
     * 根据院id和状态查询全部楼
     *
     * @param collegeId 院id
     * @param isDel     状态
     * @return 数据
     */
    Result<BuildingRecord> findByCollegeIdAndIsDel(int collegeId, Byte isDel);

    /**
     * 查找院下不等于该楼id的楼名
     *
     * @param buildingName 楼名
     * @param collegeId    院id
     * @param buildingId   楼id
     * @return 数据
     */
    Result<BuildingRecord> findByBuildingNameAndCollegeIdNeBuildingId(String buildingName, int collegeId, int buildingId);

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
    int countByCondition(DataTablesUtils<BuildingBean> dataTablesUtils);

    /**
     * 保存
     *
     * @param building 数据
     */
    void save(Building building);

    /**
     * 更新
     *
     * @param building 数据
     */
    void update(Building building);

    /**
     * 通过id更新is_del状态
     *
     * @param ids   ids
     * @param isDel is_del
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 通过毕业设计发布 生成楼数据
     *
     * @param graduationDesignRelease 毕业设计发布
     * @return 楼
     */
    List<Building> generateBuildFromGraduationDesignRelease(GraduationDesignRelease graduationDesignRelease);
}
