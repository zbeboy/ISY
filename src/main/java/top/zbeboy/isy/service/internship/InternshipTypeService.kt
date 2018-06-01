package top.zbeboy.isy.service.internship

import top.zbeboy.isy.domain.tables.pojos.InternshipType

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface InternshipTypeService {
    /**
     * 查询全部类型
     *
     * @return 全部类型
     */
    fun findAll(): List<InternshipType>

    /**
     * 根据实习类型id查询
     *
     * @param internshipTypeId 实习类型id
     * @return 实习类型
     */
    fun findByInternshipTypeId(internshipTypeId: Int): InternshipType
}