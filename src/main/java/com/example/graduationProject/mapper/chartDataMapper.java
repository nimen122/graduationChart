package com.example.graduationProject.mapper;

import com.example.graduationProject.entity.ChartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface chartDataMapper {
    List<ChartData> selectByDataId(@Param("dataId") int dataId);

    void insertChartData(ChartData chartData);

    boolean updateChartData(ChartData chartData);

    boolean deleteChartData(@Param("dataId") int dataId);
}
