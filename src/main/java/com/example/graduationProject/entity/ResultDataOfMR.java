package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfMR {
    private String dataValue;
    private int dataSize;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_MR;
    private double cl_MR;
    private double ucl_MR;
    private double lcl_MR;
}
