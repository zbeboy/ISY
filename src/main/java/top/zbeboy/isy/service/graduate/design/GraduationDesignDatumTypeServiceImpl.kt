package top.zbeboy.isy.service.graduate.design

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.GraduationDesignDatumTypeDao
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumType
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-29 .
 **/
@Service("graduationDesignDatumTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class GraduationDesignDatumTypeServiceImpl : GraduationDesignDatumTypeService {

    @Resource
    open lateinit var graduationDesignDatumTypeDao: GraduationDesignDatumTypeDao

    override fun findAll(): List<GraduationDesignDatumType> {
        return graduationDesignDatumTypeDao.findAll()
    }
}