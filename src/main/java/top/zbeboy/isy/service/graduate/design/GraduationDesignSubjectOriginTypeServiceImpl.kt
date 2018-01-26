package top.zbeboy.isy.service.graduate.design

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.GraduationDesignSubjectOriginTypeDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectOriginType
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Service("graduationDesignSubjectOriginTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignSubjectOriginTypeServiceImpl :GraduationDesignSubjectOriginTypeService{

    @Resource
    open lateinit var graduationDesignSubjectOriginTypeDao: GraduationDesignSubjectOriginTypeDao

    override fun findAll(): List<GraduationDesignSubjectOriginType> {
        return graduationDesignSubjectOriginTypeDao.findAll()
    }
}