package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class LimitsOfChart {
    private List<Double> CL;
    private List<Double> LCL;
    private List<Double> UCL;

}
