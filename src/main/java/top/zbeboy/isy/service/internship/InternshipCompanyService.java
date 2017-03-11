package top.zbeboy.isy.service.internship;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipCompany;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.internship.apply.InternshipCompanyVo;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface InternshipCompanyService {

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 校外自主实习(去单位)
     */
    InternshipCompany findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param internshipCompany 校外自主实习(去单位)
     */
    void save(InternshipCompany internshipCompany);

    /**
     * 开启事务保存
     *
     * @param internshipCompanyVo 校外自主实习(去单位)
     */
    void saveWithTransaction(InternshipCompanyVo internshipCompanyVo);

    /**
     * 更新
     *
     * @param internshipCompany 校外自主实习(去单位)
     */
    void update(InternshipCompany internshipCompany);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<InternshipCompany> dataTablesUtils, InternshipCompany internshipCompany);

    /**
     * 系总数
     *
     * @return 总数
     */
    int countAll(InternshipCompany internshipCompany);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipCompany> dataTablesUtils, InternshipCompany internshipCompany);

    /**
     * 查询
     *
     * @param dataTablesUtils   datatables工具类
     * @param internshipCompany 校外自主实习(去单位)
     * @return 导出数据
     */
    Result<Record> exportData(DataTablesUtils<InternshipCompany> dataTablesUtils, InternshipCompany internshipCompany);
}
