package com.example.graduationProject.service;

import com.example.graduationProject.entity.ChartData;
import com.example.graduationProject.mapper.chartDataMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class chartDataService {
    @Resource
    private chartDataMapper chartdataMapper;

    public List<ChartData> selectByDataId(int dataId){
        return chartdataMapper.selectByDataId(dataId);
    }

    public boolean insertChartData(ChartData chartData){
        chartdataMapper.insertChartData(chartData);
        return true;
    }

    public boolean updateChartData(ChartData chartData) {
        return chartdataMapper.updateChartData(chartData);
    }

    public boolean deleteChartData(int dataId) {
        return chartdataMapper.deleteChartData(dataId);
    }
}
