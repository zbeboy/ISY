package top.zbeboy.isy.service.graduate.wishes

import org.springframework.stereotype.Service
import top.zbeboy.isy.elastic.pojo.GraduationWishesElastic
import top.zbeboy.isy.elastic.repository.GraduationWishesElasticRepository
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-05-23 .
 **/
@Service("graduationWishesService")
open class GraduationWishesServiceImpl : GraduationWishesService {

    @Resource
    open lateinit var graduationWishesElasticRepository: GraduationWishesElasticRepository

    override fun findAll(): MutableIterable<GraduationWishesElastic>? {
        return graduationWishesElasticRepository.findAll()
    }

    override fun save(graduationWishesElastic: GraduationWishesElastic) {
        graduationWishesElasticRepository.save(graduationWishesElastic)
    }
}