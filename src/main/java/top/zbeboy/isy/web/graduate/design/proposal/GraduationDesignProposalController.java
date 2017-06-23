package top.zbeboy.isy.web.graduate.design.proposal;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignDatumService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.DataTablesUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by zbeboy on 2017/6/22.
 */
@Slf4j
@Controller
public class GraduationDesignProposalController {

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignDatumService graduationDesignDatumService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    /**
     * 毕业设计资料
     *
     * @return 毕业设计资料页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/proposal", method = RequestMethod.GET)
    public String proposal() {
        return "web/graduate/design/proposal/design_proposal::#page-wrapper";
    }

    /**
     * 附件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/proposal/affix", method = RequestMethod.GET)
    public String list(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/proposal/design_proposal_affix::#page-wrapper";
        } else {
            page = commonControllerMethodService.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 我的资料数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/proposal/my/data", method = RequestMethod.GET)
    @ResponseBody
    public DataTablesUtils<GraduationDesignDatumBean> myData(HttpServletRequest request) {
        String graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId");
        DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils = DataTablesUtils.of();
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                Users users = usersService.getUserFromSession();
                Student student = studentService.findByUsername(users.getUsername());
                ErrorBean<GraduationDesignRelease> errorBean = studentCondition(graduationDesignReleaseId, student.getStudentId());
                if (!errorBean.isHasError()) {
                    GraduationDesignTutor graduationDesignTutor = (GraduationDesignTutor) errorBean.getMapData().get("graduationDesignTutor");
                    // 前台数据标题 注：要和前台标题顺序一致，获取order用
                    List<String> headers = new ArrayList<>();
                    headers.add("select");
                    headers.add("original_file_name");
                    headers.add("graduation_design_datum_type_name");
                    headers.add("version");
                    headers.add("update_time");
                    headers.add("operator");
                    GraduationDesignDatumBean otherCondition = new GraduationDesignDatumBean();
                    dataTablesUtils = new DataTablesUtils<>(request, headers);
                    List<GraduationDesignDatumBean> graduationDesignDatumBeens = new ArrayList<>();
                    otherCondition.setGraduationDesignReleaseId(graduationDesignReleaseId);
                    otherCondition.setGraduationDesignTutorId(graduationDesignTutor.getGraduationDesignTutorId());
                    Result<Record> records = graduationDesignDatumService.findAllByPage(dataTablesUtils, otherCondition);
                    if (!ObjectUtils.isEmpty(records) && records.isNotEmpty()) {
                        graduationDesignDatumBeens = records.into(GraduationDesignDatumBean.class);
                        graduationDesignDatumBeens.forEach(i -> i.setUpdateTimeStr(DateTimeUtils.formatDate(i.getUpdateTime())));
                    }
                    dataTablesUtils.setData(graduationDesignDatumBeens);
                    dataTablesUtils.setiTotalRecords(graduationDesignDatumService.countAll(otherCondition));
                    dataTablesUtils.setiTotalDisplayRecords(graduationDesignDatumService.countByCondition(dataTablesUtils, otherCondition));
                }
            }
        }
        return dataTablesUtils;
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/proposal/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入学生资料条件
     *
     * @param graduationDesignReleaseId 发布
     * @param studentId                 学生id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> studentCondition(String graduationDesignReleaseId, int studentId) {
        Map<String, Object> dataMap = new HashMap<>();
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignReleaseService.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 是否已确认调整
                if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                    Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelation(studentId, graduationDesignReleaseId);
                    if (record.isPresent()) {
                        GraduationDesignTutor graduationDesignTutor = record.get().into(GraduationDesignTutor.class);
                        dataMap.put("graduationDesignTutor", graduationDesignTutor);
                        errorBean.setHasError(false);
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("您不符合该毕业设计条件");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("请等待确认调整后查看");
                }
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("不在毕业时间范围，无法操作");
            }
            errorBean.setMapData(dataMap);
        }

        return errorBean;
    }
}
