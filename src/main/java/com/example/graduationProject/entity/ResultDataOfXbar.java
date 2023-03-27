package com.example.graduationProject.entity;

import com.example.graduationProject.Criterion;
import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfXbar {
    private int dataSize;
    private String dataValue;
    private List<ResultOfCriterion> criterionList;
//    private String msg;
//    private List<Integer> errorPointIndex;

    private double CL_Xbar;               //Xbar数据中心线
    private List<Double> data_Xbar;       //Xbar分组平均值
    private List<Double> UCL_Xbar;        //Xbar控制上限
    private List<Double> LCL_Xbar;        //Xbar控制下限
}
