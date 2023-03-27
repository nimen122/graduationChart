package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfS {
    private int dataSize;
    private String dataValue;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_S;
    private List<Double> CL_S;            //S控制图中心线
    private List<Double> UCL_S;           //S控制图控制上限
    private List<Double> LCL_S;           //S控制图控制下限
}
