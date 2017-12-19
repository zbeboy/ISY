package top.zbeboy.isy.web.internship.common

import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease
import top.zbeboy.isy.service.internship.InternshipReleaseService
import top.zbeboy.isy.web.bean.error.ErrorBean
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-19 .
 **/
@Component
open class InternshipConditionCommon {

    @Resource
    open lateinit var internshipReleaseService: InternshipReleaseService

    fun basicCondition(internshipReleaseId: String): ErrorBean<InternshipRelease> {
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
        return errorBean
    }
}