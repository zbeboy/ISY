package top.zbeboy.isy.web.internship.journal;

import org.jooq.Record;
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
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.*;
import top.zbeboy.isy.service.*;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.SmallPropsUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by zbeboy on 2016/12/14.
 */
@Controller
public class InternshipJournalController {

    private final Logger log = LoggerFactory.getLogger(InternshipJournalController.class);

    @Resource
    private InternshipReleaseService internshipReleaseService;

    @Resource
    private InternshipApplyService internshipApplyService;

    @Resource
    private InternshipJournalService internshipJournalService;

    @Resource
    private UploadService uploadService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    /**
     * 实习日志
     *
     * @return 实习日志页面
     */
    @RequestMapping(value = "/web/menu/internship/journal", method = RequestMethod.GET)
    public String internshipJournal() {
        return "web/internship/journal/internship_journal::#page-wrapper";
    }

    /**
     * 编辑实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list/edit", method = RequestMethod.POST)
    public String journalListEdit(@RequestParam("id") String id, ModelMap modelMap) {
        String page = "web/internship/journal/internship_journal::#page-wrapper";
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            modelMap.addAttribute("journal", internshipJournal);
            page = "web/internship/journal/internship_journal_edit::#page-wrapper";
        }
        return page;
    }

    /**
     * 查看实习日志页面
     *
     * @param id       实习日志id
     * @param modelMap 页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/internship/journal/list/look", method = RequestMethod.POST)
    public String journalListLook(@RequestParam("id") String id, ModelMap modelMap) {
        String page = "web/internship/journal/internship_journal::#page-wrapper";
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            modelMap.addAttribute("journal", internshipJournal);
            page = "web/internship/journal/internship_journal_look::#page-wrapper";
        }
        return page;
    }

    /**
     * 下载实习日志
     *
     * @param id 实习日志id
     */
    @RequestMapping(value = "/web/internship/journal/list/download", method = RequestMethod.POST)
    public void journalListDownload(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response) {
        InternshipJournal internshipJournal = internshipJournalService.findById(id);
        if (!ObjectUtils.isEmpty(internshipJournal)) {
            uploadService.download(internshipJournal.getStudentName() + " " + internshipJournal.getStudentNumber(), "/" + internshipJournal.getInternshipJournalWord(), response, request);
        }
    }

    /**
     * 批量下载实习日志
     *
     * @param ids 实习日志ids
     */
    @RequestMapping(value = "/web/internship/journal/list/downloads", method = RequestMethod.POST)
    public void journalListDownloads(String ids, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.hasLength(ids)) {
            List<InternshipJournal> internshipJournals = internshipJournalService.findInIds(ids);
            internshipJournals.forEach(i -> {
                // TODO:打成ZIP
            });
            uploadService.download("实习日志", "/" + "{{path}}", response, request);
        }
    }

    /**
     * 批量删除日志
     *
     * @param journalIds 系ids
     * @return true 删除成功
     */
    @RequestMapping(value = "/web/internship/journal/list/del", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils departmentUpdateDel(String journalIds) {
        if (StringUtils.hasLength(journalIds)) {
            internshipJournalService.batchDelete(SmallPropsUtils.StringIdsToStringList(journalIds));
            return new AjaxUtils().success().msg("删除日志成功");
        }
        return new AjaxUtils().fail().msg("删除日志失败");
    }

    /**
     * 进入实习日志入口条件
     *
     * @param internshipReleaseId 实习发布id
     * @return true or false
     */
    private ErrorBean<InternshipRelease> accessCondition(String internshipReleaseId, int studentId) {
        ErrorBean<InternshipRelease> errorBean = new ErrorBean<>();
        Map<String, Object> mapData = new HashMap<>();
        InternshipRelease internshipRelease = internshipReleaseService.findById(internshipReleaseId);
        if (ObjectUtils.isEmpty(internshipRelease)) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关实习信息");
            return errorBean;
        }
        errorBean.setData(internshipRelease);
        if (internshipRelease.getInternshipReleaseIsDel() == 1) {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("该实习已被注销");
            return errorBean;
        }
        Optional<Record> internshipApplyRecord = internshipApplyService.findByInternshipReleaseIdAndStudentId(internshipReleaseId, studentId);
        if (internshipApplyRecord.isPresent()) {
            InternshipApply internshipApply = internshipApplyRecord.get().into(InternshipApply.class);
            mapData.put("internshipApply", internshipApply);
            // 状态为 2：已通过；4：基本信息变更申请中；5：基本信息变更填写中；6：单位信息变更申请中；7：单位信息变更填写中 允许进行填写
            if (internshipApply.getInternshipApplyState() == 2 || internshipApply.getInternshipApplyState() == 4 ||
                    internshipApply.getInternshipApplyState() == 5 || internshipApply.getInternshipApplyState() == 6 || internshipApply.getInternshipApplyState() == 7) {
                errorBean.setHasError(false);
                errorBean.setErrorMsg("允许填写");
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("检测到您未通过申请，不允许填写");
            }
        }
        errorBean.setMapData(mapData);
        return errorBean;
    }
}
