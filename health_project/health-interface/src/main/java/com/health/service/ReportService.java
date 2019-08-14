package com.health.service;

import java.util.List;
import java.util.Map;

public interface ReportService {


    /**
     * 查询套餐与订单量的关系
     * @param
     * @Return:
     */
    List<Map<String,Object>> findSemealAndOrderCount();

    /**
     * 获平台统计数据
     *
     * @param
     * @Return:
     */
    Map<String, Object> getBusinessReport() throws Exception;
}
