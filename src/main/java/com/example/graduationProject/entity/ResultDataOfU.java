package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfU {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_U;
    private Double cl_U;
    private List<Double> lcl_U;
    private List<Double> ucl_U;

}
