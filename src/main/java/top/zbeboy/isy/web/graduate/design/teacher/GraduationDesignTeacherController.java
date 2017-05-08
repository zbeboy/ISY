package top.zbeboy.isy.web.graduate.design.teacher;

import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbeboy on 2017/5/8.
 */
@Controller
public class GraduationDesignTeacherController {

    private final Logger log = LoggerFactory.getLogger(GraduationDesignTeacherController.class);

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    /**
     * 毕业指导教师
     *
     * @return 毕业指导教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/tutor", method = RequestMethod.GET)
    public String teacherData() {
        return "web/graduate/design/teacher/design_teacher::#page-wrapper";
    }

    /**
     * 毕业指导教师列表页
     *
     * @return 毕业指导教师列表页
     */
    @RequestMapping(value = "/web/graduate/design/tutor/look", method = RequestMethod.GET)
    public String teacherList(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        modelMap.addAttribute("graduationDesignReleaseId",graduationDesignReleaseId);
        return "web/graduate/design/teacher/design_teacher_look::#page-wrapper";
    }

    /**
     * 毕业指导教师列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/tutor/look/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignTeacherBean> listData(HttpServletRequest request) {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        List<String> headers = new ArrayList<>();
        headers.add("select");
        headers.add("real_name");
        headers.add("staff_number");
        headers.add("staff_username");
        headers.add("assigner_name");
        headers.add("operator");
        DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils = new DataTablesUtils<>(request, headers);
        GraduationDesignTeacherBean otherCondition = new GraduationDesignTeacherBean();
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
            List<GraduationDesignTeacherBean> graduationDesignTeacherBeens = graduationDesignTeacherService.findAllByPage(dataTablesUtils, otherCondition);
            dataTablesUtils.setData(graduationDesignTeacherBeens);
            dataTablesUtils.setiTotalRecords(graduationDesignTeacherService.countAll(otherCondition));
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignTeacherService.countByCondition(dataTablesUtils, otherCondition));
        } else {
            dataTablesUtils.setData(null);
            dataTablesUtils.setiTotalRecords(0);
            dataTablesUtils.setiTotalDisplayRecords(0);
        }
        return dataTablesUtils;
    }

    /**
     * 进入指导教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = new ErrorBean<>();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            errorBean.setData(graduationDesignRelease);
            if (graduationDesignRelease.getGraduationDesignIsDel() == 1) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("该毕业设计已被注销");
            } else {
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在毕业设计时间范围，无法进入");
                }
                // 学生填报教师时间
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getFillTeacherStartTime(), graduationDesignRelease.getFillTeacherEndTime())) {
                    errorBean.setHasError(false);
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("学生申报指导教师已开始，已锁定操作");
                }
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        return errorBean;
    }
}
