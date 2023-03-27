package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfXbar_S {
    private int dataSize;
    private String dataValue;
    private double CL_Xbar;               //Xbar数据中心线
    private List<Double> groupAve_Xbar;   //Xbar分组平均值
    private List<Double> UCL_Xbar;        //Xbar控制上限
    private List<Double> LCL_Xbar;        //Xbar控制下限
    private List<Double> CL_S;            //S控制图中心线
    private List<Double> UCL_S;           //S控制图控制上限
    private List<Double> LCL_S;           //S控制图控制下限
}
