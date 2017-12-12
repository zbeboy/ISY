package top.zbeboy.isy.service.data

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import top.zbeboy.isy.domain.tables.daos.ScoreTypeDao
import top.zbeboy.isy.domain.tables.pojos.ScoreType
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-12 .
 **/
@Service("scoreTypeService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class ScoreTypeServiceImpl:ScoreTypeService {

    @Resource
    open lateinit var scoreTypeDao: ScoreTypeDao

    override fun findAll(): List<ScoreType> {
        return scoreTypeDao.findAll()
    }
}