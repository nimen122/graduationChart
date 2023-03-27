package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfNP {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private List<Integer> data_NP;
    private List<Double> cl_NP;
    private List<Double> lcl_NP;
    private List<Double> ucl_NP;

}
