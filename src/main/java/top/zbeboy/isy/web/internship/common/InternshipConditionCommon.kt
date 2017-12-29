package top.zbeboy.isy.web.internship.common

import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.service.cache.CacheBook
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.web.bean.error.ErrorBean
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-19 .
 **/
@Component
open class InternshipConditionCommon {

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    @Resource(name = "redisTemplate")
    open lateinit var errorBeanValueOperations: ValueOperations<String, ErrorBean<InternshipRelease>>

    /**
     * 基础条件，判断是否已注销或存在
     *
     * @param internshipReleaseId 实习发布id
     * @return 错误消息
     */
    fun basicCondition(internshipReleaseId: String): ErrorBean<InternshipRelease> {
        val cacheKey = CacheBook.INTERNSHIP_BASE_CONDITION + internshipReleaseId
        if (errorBeanValueOperations.operations.hasKey(cacheKey)!!) {
            return errorBeanValueOperations.get(cacheKey)
        }
        val errorBean = ErrorBean.of<InternshipRelease>()
        val internshipRelease = internshipReleaseService.findById(internshipReleaseId)
        if (!ObjectUtils.isEmpty(internshipRelease)) {
            errorBean.data = internshipRelease
            if (internshipRelease.internshipReleaseIsDel == 1.toByte()) {
                errorBean.hasError = true
                errorBean.errorMsg = "该实习已被注销"
            } else {
                errorBean.hasError = false
            }
        } else {
            errorBean.hasError = true
            errorBean.errorMsg = "未查询到相关实习信息"
        }
        errorBeanValueOperations.set(cacheKey, errorBean, CacheBook.EXPIRES_MINUTES, TimeUnit.MINUTES)
        return errorBean
    }
}