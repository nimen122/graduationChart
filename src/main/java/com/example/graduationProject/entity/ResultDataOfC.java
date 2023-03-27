package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfC {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private List<Integer> data_C;
    private double cl_C;
    private double lcl_C;
    private double ucl_C;

}
