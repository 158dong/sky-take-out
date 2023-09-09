package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;


    @Override
    public TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end) {
        //获取日期的从开始到结束每天的天数封装进list集合，用于封装进TurnoverReportVo返回
        List<LocalDate> datesList = new ArrayList<>();
        datesList.add(begin);
        //将日期从开启到结束的每一天封装进list集合
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datesList.add(begin);
        }
        //将集合转换成字符串并用逗号连接
        String dates = StringUtils.join(datesList, ",");



        //获取每天的营业额封装进vo内
        //存放每天的营业额
        List<Double> sumTurnover = new ArrayList<>();
        for (LocalDate localDate : datesList) {
            //获取每天最早和最晚的时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //将数据封装进map集合内用于sql查询营业额
            Map m = new HashMap();
            m.put("begin",beginTime);
            m.put("end",endTime);
            m.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(m);
            turnover = turnover == null ? 0.0 :turnover;
            sumTurnover.add(turnover);
        }
        String turnOver = StringUtils.join(sumTurnover, ",");

        //封装返回
        return TurnoverReportVO.builder()
                .dateList(dates)
                .turnoverList(turnOver)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistic(LocalDate begin, LocalDate end) {
        //获取日期的从开始到结束每天的天数封装进list集合，用于封装进userReportVo返回
        List<LocalDate> datesList = new ArrayList<>();
        datesList.add(begin);
        //将日期从开启到结束的每一天封装进list集合
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datesList.add(begin);
        }
        //将集合转换成字符串并用逗号连接
        String dates = StringUtils.join(datesList, ",");


        //查看总用户就查询小于今天之前的全部用户，查询新用户就查询今天之内的用户
        //用于存放总的用户
        List<Integer> totalUserList = new ArrayList<>();
        //用户存放每天的新用户
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate localDate : datesList) {
            //获取每天最早和最晚的时间
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.getUserNum(map);
            totalUserList.add(totalUser);
            map.put("begin",beginTime);
            Integer newUser = userMapper.getUserNum(map);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .dateList(dates)
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO orderStatistic(LocalDate begin, LocalDate end) {
        //获取日期的从开始到结束每天的天数封装进list集合，用于封装进userReportVo返回
        List<LocalDate> datesList = new ArrayList<>();
        datesList.add(begin);
        //将日期从开启到结束的每一天封装进list集合
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            datesList.add(begin);
        }


        List<Integer> orderCountList = new ArrayList<>();
        //用户存放每天的新用户
        List<Integer> validOrderCountList = new ArrayList<>();
        //获取每日订单和获取每日有效订单
        for (LocalDate localDate : datesList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer orderCount = orderMapper.getOrderNum(map);
            orderCountList.add(orderCount);
            map.put("status",Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderNum(map);
            validOrderCountList.add(validOrderCount);
        }

        //获取时间区间内订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //获取时间区间内有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(datesList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        //获取时间区间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //获取排名top10的数据
        List<GoodsSalesDTO> list = orderMapper.getTop10(beginTime,endTime);
        //将Goods成top10内的每个数据为集合
        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        SalesTop10ReportVO top10ReportVO = SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numList, ","))
                .build();
        return top10ReportVO;
    }

    /**
     * 导出数据报表
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        //获取30天的区间
        LocalDate beginDay = LocalDate.now().minusDays(30);
        LocalDate endDay = LocalDate.now().minusDays(1);

        //获取概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginDay, LocalTime.MIN), LocalDateTime.of(endDay, LocalTime.MAX));

        //获取到excel表格
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //使用opi操作excel

        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取excel第一页
            XSSFSheet sleet1 = excel.getSheet("Sheet1");
            //获取第二行
            XSSFRow row = sleet1.getRow(1);
            //获取第二个单元格
            XSSFCell cell = row.getCell(1);
            //写入日期数据
            cell.setCellValue("日期："+beginDay+"至"+endDay);

            //获取第四行
            row = sleet1.getRow(3);
            //获取第三个单元格
            cell  = row.getCell(2);
            //写入概览数据
            cell.setCellValue(businessData.getTurnover());
            //获取第五个单元格
            cell = row.getCell(4);
            cell.setCellValue(businessData.getOrderCompletionRate());
            //获取第七个单元格
            cell = row.getCell(6);
            cell.setCellValue(businessData.getNewUsers());

            //获取第五行
            row = sleet1.getRow(4);
            //获取第三个单元格
            cell  = row.getCell(2);
            //写入概览数据
            cell.setCellValue(businessData.getValidOrderCount());
            //获取第五个单元格
            cell = row.getCell(4);
            cell.setCellValue(businessData.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                //计算每天
                LocalDate date = beginDay.plusDays(i);
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获取第八行
                row = sleet1.getRow(7+i);
                //获取单元格并写入数据
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }
            //将数据表导出
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
