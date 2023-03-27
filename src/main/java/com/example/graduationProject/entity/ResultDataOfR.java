package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfR {
    private int dataSize;
    private String dataValue;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_R;    //分组极差
    private List<Double> CL_R;      //R控制图中心线
    private List<Double> UCL_R;     //R控制图控制上限
    private List<Double> LCL_R;     //R控制图控制下限
}
