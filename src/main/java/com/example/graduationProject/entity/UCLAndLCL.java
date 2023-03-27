package com.example.graduationProject.entity;

import lombok.Data;

import java.util.List;

@Data
public class UCLAndLCL {
    private List<Double> UCL;
    private List<Double> LCL;
}
