# 错误记录
1.WebSecurity中，获取session中用户信息会为空，导致异常到错误页面，解决该
问题需要对UsersServiceImpl中的获取session用户信息进行返回值处理，但目前
转换工作未进行到此，暂时采取异常常规处理，待完成kotlin转换后进行详细处理。  
2.SecurityLoginFilter中因Users为空产生异常。  
3.MenuInterceptor 因Users为空产生异常。  
4.调整民族表中数据顺序。  
5.在院与角色关联表增加 "是否允许代理该角色" 字段。  
6.等待学生与教职工数据转为kotlin后，重构增加代理角色功能。  
7.添加专业，班级时，管理员可以选择系，但是如果不是管理员的用户，要单独
处理。