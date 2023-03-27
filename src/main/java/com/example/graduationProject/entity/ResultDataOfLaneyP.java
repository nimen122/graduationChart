package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfLaneyP {
    private String dataValue;
    private int dataSize;
    private boolean isAvailable;
    private String msg;
    private List<ResultOfCriterion> criterionList;

    private double sigmaZ;
    private List<Double> data_LaneyP;
    private double cl_LaneyP;
    private List<Double> lcl_LaneyP;
    private List<Double> ucl_LaneyP;

}
