package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfLaneyU {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private double sigmaZ;
    private List<Double> data_LaneyU;
    private double cl_LaneyU;
    private List<Double> lcl_LaneyU;
    private List<Double> ucl_LaneyU;

}
