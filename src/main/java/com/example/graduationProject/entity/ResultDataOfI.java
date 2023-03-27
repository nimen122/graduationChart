package com.example.graduationProject.entity;

import lombok.Data;
import java.util.List;

@Data
public class ResultDataOfI {
    private int dataSize;
    private String dataValue;
    private List<ResultOfCriterion> criterionList;

    private List<Double> data_I;
    private double cl_I;
    private double ucl_I;
    private double lcl_I;
}
