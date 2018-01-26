package top.zbeboy.isy.service.graduate.design

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.GraduationDesignSubjectTypeDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignSubjectType
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-26 .
 **/
@Service("graduationDesignSubjectTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignSubjectTypeServiceImpl :GraduationDesignSubjectTypeService{

    @Resource
    open lateinit var graduationDesignSubjectTypeDao: GraduationDesignSubjectTypeDao

    override fun findAll(): List<GraduationDesignSubjectType> {
        return graduationDesignSubjectTypeDao.findAll()
    }
}