package top.zbeboy.isy.service.internship;

import top.zbeboy.isy.domain.tables.pojos.InternshipType;

import java.util.List;

/**
 * Created by lenovo on 2016-11-12.
 */
public interface InternshipTypeService {

    /**
     * 查询全部类型
     *
     * @return 全部类型
     */
    List<InternshipType> findAll();

    /**
     * 根据实习类型id查询
     *
     * @param internshipTypeId 实习类型id
     * @return 实习类型
     */
    InternshipType findByInternshipTypeId(int internshipTypeId);
}
