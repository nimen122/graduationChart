package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class ResultDataOfI_MR {
    private String dataValue;
    private int dataSize;

    private List<Double> data_I;
    private double cl_I;
    private double ucl_I;
    private double lcl_I;

    private List<Double> data_MR;
    private double cl_MR;
    private double ucl_MR;
    private double lcl_MR;
}
