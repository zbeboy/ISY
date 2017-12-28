package top.zbeboy.isy.service.export

import org.apache.poi.ss.usermodel.Row
import top.zbeboy.isy.domain.tables.pojos.InternshipCollege
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.ExportUtils

/**
 * Created by zbeboy 2017-12-28 .
 **/
class InternshipCollegeExport(data: List<InternshipCollege>) : ExportUtils<InternshipCollege>(data) {

    // 序号
    private var sequence = 0

    override fun createHeader(row: Row) {
        row.createCell(0).setCellValue("序号")
        row.createCell(1).setCellValue("学生姓名")
        row.createCell(2).setCellValue("专业班级")
        row.createCell(3).setCellValue("性别")
        row.createCell(4).setCellValue("学号")
        row.createCell(5).setCellValue("电话号码")
        row.createCell(6).setCellValue("qq邮箱")
        row.createCell(7).setCellValue("父母联系方式")
        row.createCell(8).setCellValue("班主任")
        row.createCell(9).setCellValue("班主任联系方式")
        row.createCell(10).setCellValue("实习单位名称")
        row.createCell(11).setCellValue("实习单位地址")
        row.createCell(12).setCellValue("实习单位联系人")
        row.createCell(13).setCellValue("实习单位联系人联系方式")
        row.createCell(14).setCellValue("校内指导教师")
        row.createCell(15).setCellValue("校内指导教师联系方式")
        row.createCell(16).setCellValue("实习开始时间")
        row.createCell(17).setCellValue("实习结束时间")
        row.createCell(18).setCellValue("承诺书")
        row.createCell(19).setCellValue("安全责任书")
        row.createCell(20).setCellValue("实践协议书")
        row.createCell(21).setCellValue("实习申请书")
        row.createCell(22).setCellValue("实习接收")
        row.createCell(23).setCellValue("安全教育协议")
        row.createCell(24).setCellValue("父母同意书")
    }

    override fun createCell(row: Row, t: InternshipCollege) {
        sequence++
        row.createCell(0).setCellValue(sequence.toDouble())
        row.createCell(1).setCellValue(t.studentName)
        row.createCell(2).setCellValue(t.collegeClass)
        row.createCell(3).setCellValue(t.studentSex)
        row.createCell(4).setCellValue(t.studentNumber)
        row.createCell(5).setCellValue(t.phoneNumber)
        row.createCell(6).setCellValue(t.qqMailbox)
        row.createCell(7).setCellValue(t.parentalContact)
        row.createCell(8).setCellValue(t.headmaster)
        row.createCell(9).setCellValue(t.headmasterContact)
        row.createCell(10).setCellValue(t.internshipCollegeName)
        row.createCell(11).setCellValue(t.internshipCollegeAddress)
        row.createCell(12).setCellValue(t.internshipCollegeContacts)
        row.createCell(13).setCellValue(t.internshipCollegeTel)
        row.createCell(14).setCellValue(t.schoolGuidanceTeacher)
        row.createCell(15).setCellValue(t.schoolGuidanceTeacherTel)
        row.createCell(16).setCellValue(DateTimeUtils.formatDate(t.startTime, "yyyy-MM-dd"))
        row.createCell(17).setCellValue(DateTimeUtils.formatDate(t.endTime, "yyyy-MM-dd"))
        row.createCell(18).setCellValue(dealByte(t.commitmentBook))
        row.createCell(19).setCellValue(dealByte(t.safetyResponsibilityBook))
        row.createCell(20).setCellValue(dealByte(t.practiceAgreement))
        row.createCell(21).setCellValue(dealByte(t.internshipApplication))
        row.createCell(22).setCellValue(dealByte(t.practiceReceiving))
        row.createCell(23).setCellValue(dealByte(t.securityEducationAgreement))
        row.createCell(24).setCellValue(dealByte(t.parentalConsent))
    }

    private fun dealByte(b: Byte?): String {
        return if (b != null && b == 1.toByte()) {
            "已交"
        } else "未交"
    }
}