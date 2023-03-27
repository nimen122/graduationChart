package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfP {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_P;
    private Double cl_P;
    private List<Double> lcl_P;
    private List<Double> ucl_P;

}
