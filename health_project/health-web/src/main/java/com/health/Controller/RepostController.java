package com.health.Controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.health.constant.MessageConstant;
import com.health.pojo.Result;
import com.health.service.ReportService;
import com.health.service.UserService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("report")
public class RepostController {


    @Reference
    private UserService userService;


    @RequestMapping("/getMemberReport")
    /**
     * 根据时间查询会员数量
     * @param
     * @Return:
     */
    public Result getMemberReport() {
        //获取日历
        Calendar calendar = Calendar.getInstance();
        //获取当前日期之前的12个月
        calendar.add(Calendar.MONTH, -12);
        //创建日历集合
        List<String> list = new ArrayList<>();
        //获取12个月的日历并格式化
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            list.add(new SimpleDateFormat("yyyy-MM").format(calendar.getTime()));
        }
        //创建map集合用于发送前端所需数据
        Map<String, Object> map = new HashMap<>();
        map.put("months", list);
        List<Integer> memberCount = userService.findUserByDate(list);
        map.put("memberCount", memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    @Reference
    private ReportService reportService;

    @RequestMapping("/getSetmealReport")
    /**
     * 套餐分析数据
     * @param
     * @Return:
     */
    public Result getSetmealReport() {
        //套餐预与订单关系
        List<Map<String, Object>> count = reportService.findSemealAndOrderCount();
        //创建map,用于存放返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("setmealCount", count);
        //创建套餐集合
        List<String> list = new ArrayList<>();
        //获取套餐集合,并存入套餐集合
        for (Map<String, Object> setmeal : count) {
            String name = (String) setmeal.get("name");
            list.add(name);
        }
        map.put("setmealNames", list);
        //返回数据
        return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS, map);
    }


    @RequestMapping("/getBusinessReportData")
    /**
     * 获取运营统计数据
     * @param
     * @Return:
     */
    public Result getBusinessReportData() {
        try {
            Map<String, Object> map = reportService.getBusinessReport();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;


    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport() {
        try {
            //获取平台统计数据
            Map<String, Object> map = reportService.getBusinessReport();
            String reportDate = (String) map.get("reportDate");//今日日期
            Integer todayNewMember = (Integer) map.get("todayNewMember");//今日新增会员
            Integer totalMember = (Integer) map.get("totalMember");//总会员数
            Integer thisWeekNewMember = (Integer) map.get("thisWeekNewMember");//本周新增会员数
            Integer thisMonthNewMember = (Integer) map.get("thisMonthNewMember");//本月新增会员数
            Integer todayOrderNumber = (Integer) map.get("todayOrderNumber");//今日预约数
            Integer thisWeekOrderNumber = (Integer) map.get("thisWeekOrderNumber");//本周预约数
            Integer thisMonthOrderNumber = (Integer) map.get("thisMonthOrderNumber");//本月预约数
            Integer todayVisitsNumber = (Integer) map.get("todayVisitsNumber");//今日到诊数
            Integer thisWeekVisitsNumber = (Integer) map.get("thisWeekVisitsNumber");//本周到诊数
            Integer thisMonthVisitsNUmber = (Integer) map.get("thisMonthVisitsNumber");//本月到诊数
            List<Map> hotSetmeal = (List<Map>) map.get("hotSetmeal");

            //获取excel模板文件的绝对路径
            String template = httpServletRequest.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";

            //读取excel模板创建模板对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(template)));

            //获取给定索引处的XSSFSheet对象
            XSSFSheet sheet = workbook.getSheetAt(0);//索引从0开始

            XSSFRow row = sheet.getRow(2);//获取起始行
            //插入列值
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//今日新增会员数
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNUmber);//本月到诊数

            int rowNum = 12;
            for (Map map1 : hotSetmeal) {
                String name = (String) map1.get("name");
                Long count = (Long) map1.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map1.get("proportion");
                //表格插入之前一定要选择行!!!**
                row = sheet.getRow(rowNum++);
                row.getCell(4).setCellValue(name);//套餐名
                row.getCell(5).setCellValue(count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //通过流进行文件下载
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            workbook.write(outputStream);
            //关闭资源
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL, null);
        }

    }
}

