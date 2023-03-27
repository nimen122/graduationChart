package com.example.graduationProject;

import com.example.graduationProject.entity.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Calculate {

    /*
    根据字符串数据和用户期望的分组大小将数据分组，方便后续计算各分组的均值和极差
    */
    public static List<BigDecimal[]> splitGroup(String datas, int groupSize) {
        List<BigDecimal> dataArray = new ArrayList<>();
        List<BigDecimal[]> data = new ArrayList<>();
        String[] split = datas.split(" ");
        for (int i = 0; i < split.length; i++) {
            dataArray.add(new BigDecimal(split[i]));
        }
        int step = groupSize;
        while (step <= dataArray.size()) {
            BigDecimal[] subGroupData = new BigDecimal[groupSize];
            for (int i = 0; i < groupSize; i++) {
                subGroupData[i] = dataArray.get(i + step - groupSize);
            }
            data.add(subGroupData);
            step += groupSize;
        }
        if (step < dataArray.size() + groupSize) {
            BigDecimal[] subGroupData = new BigDecimal[dataArray.size() + groupSize - step];
            for (int i = 0; i < dataArray.size() + groupSize - step; i++) {
                subGroupData[i] = dataArray.get(step - groupSize + i);
            }
            data.add(subGroupData);
        }
        return data;
    }

    /*
    * 计算子组标准差
    */
    public static List<BigDecimal> calculateGroupSigma(List<BigDecimal[]> groupDataList, List<Double> groupAve){
        List<BigDecimal> groupSigma = new ArrayList<>();
        BigDecimal deviationAve = new BigDecimal("0.0");
        for (int i = 0; i < groupDataList.size(); i++) {
            BigDecimal deviationSum = new BigDecimal("0.0");
            for (int j = 0; j < groupDataList.get(i).length; j++) {
                deviationSum = deviationSum.add((groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))).multiply(groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))));
            }
            deviationAve = deviationSum.divide(BigDecimal.valueOf(groupDataList.get(i).length-1),4,BigDecimal.ROUND_HALF_UP) ;
            groupSigma.add(BigDecimal.valueOf(Math.sqrt(deviationAve.doubleValue())).setScale(4,BigDecimal.ROUND_HALF_UP));
        }
        return groupSigma;
    }

    /*
    计算分好组的各小组均值
    */
    public static List<Double> calculateGroupAveOfXbar(List<BigDecimal[]> groupDataList) {
        List<Double> aveData = new ArrayList<>();
        for (int i = 0; i < groupDataList.size(); i++) {
            BigDecimal sum = new BigDecimal("0.0");
            for (int j = 0; j < groupDataList.get(i).length; j++) {
                sum = sum.add(groupDataList.get(i)[j]);
            }
            BigDecimal ave = sum.divide(BigDecimal.valueOf(groupDataList.get(i).length), 5, BigDecimal.ROUND_HALF_UP);
            aveData.add(ave.doubleValue());
        }
        return aveData;
    }

    /*
    计算数据整体的均值，即中心线
    */
    public static Double calculateDataAve(String datas, int dataSize) {
        List<BigDecimal> data = new ArrayList<>();
        String[] split = datas.split(" ");
        double aveData;
        for (int i = 0; i < split.length; i++) {
            data.add(new BigDecimal(split[i]));
        }
        BigDecimal sum = new BigDecimal("0.0");
        for (int i = 0; i < data.size(); i++) {
            sum = sum.add(data.get(i));
        }
        aveData = sum.divide(BigDecimal.valueOf(dataSize),4,BigDecimal.ROUND_HALF_UP).doubleValue();
        return aveData;
    }

    /*
    计算每个分组的极差
    */
    public static List<Double> calculateRange(List<BigDecimal[]> groupDataList) {
        List<Double> rangeList = new ArrayList<>();
        for (int i = 0; i < groupDataList.size(); i++) {
            Arrays.sort(groupDataList.get(i));
            BigDecimal range = groupDataList.get(i)[groupDataList.get(i).length - 1].subtract(groupDataList.get(i)[0]);
            rangeList.add(range.doubleValue());
        }
        return rangeList;
    }

    /*
    使用Rbar方法估计西格玛值
    */
    public static double RbarSigma(List<BigDecimal[]> groupDataList, List<Double> groupRange) {

        GlobalStatic globalStatic = new GlobalStatic();
        BigDecimal sumData = new BigDecimal("0");
        BigDecimal fiSum = new BigDecimal("0");
        for(int i = 0;i < groupDataList.size();i++){
            int n = groupDataList.get(i).length;
            BigDecimal f_i = BigDecimal.valueOf( (globalStatic.getd_2(n)*globalStatic.getd_2(n) ) /
                    (globalStatic.getd_3(n)*globalStatic.getd_2(n)) );
            sumData = sumData.add(
                    (f_i.multiply(BigDecimal.valueOf(groupRange.get(i))
                            .divide(BigDecimal.valueOf(globalStatic.getd_2(n)),4,BigDecimal.ROUND_HALF_UP)))
            );
            fiSum = fiSum.add(f_i);
        }
        return sumData.divide(fiSum,4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /*
    使用Sbar方法(不使用无偏常量)估计西格玛值
    */
    public static double SbarSigmaWithoutConstant(List<BigDecimal[]> groupDataList, List<Double> groupAve) {
        BigDecimal deviation;
        List<BigDecimal> groupSigma = Calculate.calculateGroupSigma(groupDataList,groupAve);
        BigDecimal groupSigmaSum = new BigDecimal("0.0");
        for (int i = 0;i<groupSigma.size();i++){
            groupSigmaSum = groupSigmaSum.add(groupSigma.get(i));
        }
        deviation = BigDecimal.valueOf(groupSigmaSum.divide(BigDecimal.valueOf(groupAve.size()), 4, BigDecimal.ROUND_HALF_UP).doubleValue());
        return deviation.doubleValue();
    }

    /*
    使用Sbar方法(使用无偏常量)估计西格玛值
    */
    public static double SbarSigmaWithConstant(List<BigDecimal[]> groupDataList, List<Double> groupAve) {
        GlobalStatic globalStatic = new GlobalStatic();
        BigDecimal result = new BigDecimal("0.0");
        BigDecimal h_iSum = new BigDecimal("0.0");
        List<BigDecimal> groupSigma = Calculate.calculateGroupSigma(groupDataList,groupAve);
        for(int i = 0;i < groupDataList.size();i++){
            int n = groupDataList.get(i).length;
            BigDecimal h_i = BigDecimal.valueOf(globalStatic.getc_4(n)*globalStatic.getc_4(n)
                    /(1 - globalStatic.getc_4(n)*globalStatic.getc_4(n))) ;
            result = result.add(h_i.multiply(groupSigma.get(i)).divide(BigDecimal.valueOf(globalStatic.getc_4(n)),4,BigDecimal.ROUND_HALF_UP));
            h_iSum = h_iSum.add(h_i);
        }
        result = result.divide(h_iSum,4,BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    /*
    使用合并标准差的方法（不使用无偏常量）计算小组的方差，以此作为数据的西格玛估计值
    */
    public static double UnionSigmaWithoutConstant(List<BigDecimal[]> groupDataList, List<Double> groupAve) {
        int groupSizeSum = 0;
        BigDecimal deviation;
        BigDecimal deviationSum = new BigDecimal("0.0");
        for (int i = 0; i < groupDataList.size(); i++) {
            groupSizeSum += groupDataList.get(i).length-1;
            for (int j = 0; j < groupDataList.get(i).length; j++) {
                deviationSum = deviationSum.add((groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))).multiply(groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))));
            }
        }
        deviation = BigDecimal.valueOf(Math.sqrt(deviationSum.divide(BigDecimal.valueOf(groupSizeSum), 5, BigDecimal.ROUND_HALF_UP).doubleValue()));
        return deviation.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /*
    使用合并标准差的方法（使用无偏常量无偏估计）计算小组的方差，以此作为数据的西格玛估计值
    */
    public static double UnionSigmaWithConstant(List<BigDecimal[]> groupDataList, List<Double> groupAve) {
        int groupSizeSum = 0;
        GlobalStatic globalStatic = new GlobalStatic();
        BigDecimal deviation;
        BigDecimal deviationSum = new BigDecimal("0.0");
        for (int i = 0; i < groupDataList.size(); i++) {
            groupSizeSum += groupDataList.get(i).length-1;
            for (int j = 0; j < groupDataList.get(i).length; j++) {
                deviationSum = deviationSum.add((groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))).multiply(groupDataList.get(i)[j].subtract(BigDecimal.valueOf(groupAve.get(i)))));
            }
        }
        deviation = BigDecimal.valueOf(Math.sqrt(deviationSum.divide(BigDecimal.valueOf(groupSizeSum), 5, BigDecimal.ROUND_HALF_UP).doubleValue()));
        deviation = deviation.divide(BigDecimal.valueOf(globalStatic.getc_4(groupSizeSum+1)),4,BigDecimal.ROUND_HALF_UP);
        return deviation.doubleValue();
    }

    /*
    计算Xbar控制图的控制上下限
    */
    public static UCLAndLCL calculateUCLAndLCLOfXbar(List<BigDecimal[]> groupData, double aveData, int k, double standardDeviation) {
        List<Double> UCLList = new ArrayList<>();
        List<Double> LCLList = new ArrayList<>();
        UCLAndLCL result = new UCLAndLCL();
        for (int i = 0;i<groupData.size();i++){
            BigDecimal UCLBuffer = BigDecimal.valueOf(aveData + k*standardDeviation/(Math.sqrt(groupData.get(i).length)));
            BigDecimal LCLBuffer = BigDecimal.valueOf(aveData - k*standardDeviation/(Math.sqrt(groupData.get(i).length)));
            UCLList.add(UCLBuffer.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
            LCLList.add(LCLBuffer.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setUCL(UCLList);
        result.setLCL(LCLList);
        return result;
    }

    /*
    计算R控制图的中心线和控制上下限
    */
    public static LimitsOfChart calculateLimitsOfR(List<BigDecimal[]> groupData, double standardDeviation, int k){
        List<Double> CL_RList = new ArrayList<>();
        List<Double> UCL_RList = new ArrayList<>();
        List<Double> LCL_RList = new ArrayList<>();
        LimitsOfChart result = new LimitsOfChart();
        GlobalStatic globalStatic =new GlobalStatic();
        for (int i = 0;i < groupData.size();i++){
            int n = groupData.get(i).length;
            BigDecimal cl = BigDecimal.valueOf(globalStatic.getd_2(n)*standardDeviation);
            CL_RList.add(cl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal lcl = BigDecimal.valueOf(globalStatic.getd_2(n)*standardDeviation - k*standardDeviation*globalStatic.getd_3(n));
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            LCL_RList.add(lcl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal ucl = BigDecimal.valueOf(globalStatic.getd_2(n)*standardDeviation + k*standardDeviation*globalStatic.getd_3(n));
            UCL_RList.add(ucl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setCL(CL_RList);
        result.setLCL(LCL_RList);
        result.setUCL(UCL_RList);
        return result;
    }

    /*
    计算S控制图的中心线和控制上下限（使用无偏常量）
    */
    public static LimitsOfChart LimitsOfSWithConstant(List<BigDecimal[]> groupData, double standardDeviation, int k){
        List<Double> CL_SList = new ArrayList<>();
        List<Double> UCL_SList = new ArrayList<>();
        List<Double> LCL_SList = new ArrayList<>();
        LimitsOfChart result = new LimitsOfChart();
        GlobalStatic globalStatic =new GlobalStatic();
        for (int i = 0;i < groupData.size();i++){
            int n = groupData.get(i).length;
            BigDecimal cl = BigDecimal.valueOf(globalStatic.getc_4(n)*standardDeviation);
            CL_SList.add(cl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal lcl = BigDecimal.valueOf(globalStatic.getc_4(n)*standardDeviation - k*standardDeviation*globalStatic.getc_5(n));
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            LCL_SList.add(lcl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal ucl = BigDecimal.valueOf(globalStatic.getc_4(n)*standardDeviation + k*standardDeviation*globalStatic.getc_5(n));
            UCL_SList.add(ucl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setCL(CL_SList);
        result.setLCL(LCL_SList);
        result.setUCL(UCL_SList);
        return result;
    }

    /*
    计算S控制图的中心线和控制上下限（不使用无偏常量）
    */
    public static LimitsOfChart LimitsOfSWithoutConstant(List<BigDecimal[]> groupData, double standardDeviation, int k){
        List<Double> CL_SList = new ArrayList<>();
        List<Double> UCL_SList = new ArrayList<>();
        List<Double> LCL_SList = new ArrayList<>();
        LimitsOfChart result = new LimitsOfChart();
        GlobalStatic globalStatic =new GlobalStatic();
        for (int i = 0;i < groupData.size();i++){
            int n = groupData.get(i).length;
            BigDecimal cl = BigDecimal.valueOf(standardDeviation);
            CL_SList.add(cl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal lcl = BigDecimal.valueOf(standardDeviation - k*(globalStatic.getc_5(n)/globalStatic.getc_4(n))*standardDeviation);
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            LCL_SList.add(lcl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal ucl = BigDecimal.valueOf(standardDeviation + k*(globalStatic.getc_5(n)/globalStatic.getc_4(n))*standardDeviation);
            UCL_SList.add(ucl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setCL(CL_SList);
        result.setLCL(LCL_SList);
        result.setUCL(UCL_SList);
        return result;
    }

    /*
    为计算移动极差对数据进行分组
    */
    public static List<BigDecimal[]> splitMovingRangeGroup(String datas, int movingRangeLength){
        List<BigDecimal> dataArray = new ArrayList<>();
        List<BigDecimal[]> data = new ArrayList<>();
        String[] split = datas.split(" ");
        for (int i = 0; i < split.length; i++) {
            dataArray.add(new BigDecimal(split[i]));
        }
        int step = movingRangeLength;
        while (step <= dataArray.size()) {
            BigDecimal[] subGroupData = new BigDecimal[movingRangeLength];
            for (int i = 0; i < movingRangeLength; i++) {
                subGroupData[i] = dataArray.get(i + step - movingRangeLength);
            }
            data.add(subGroupData);
            step ++;
        }
        return data;
    }

    /*
    计算移动极差平均值MRbar,作为西格玛的无偏估计
    w:移动极差中的观测值个数
     */
    public static double movingRangeAverageAsSigma(List<Double> groupMovingRange,int w){
        GlobalStatic globalStatic = new GlobalStatic();
        BigDecimal rangeSum = new BigDecimal("0.0");
        for(int i = 0;i<groupMovingRange.size();i++){
            rangeSum = rangeSum.add(BigDecimal.valueOf(groupMovingRange.get(i)));
        }
        BigDecimal rangeAve = rangeSum.divide(BigDecimal.valueOf(groupMovingRange.size()),4,BigDecimal.ROUND_HALF_UP);
        return rangeAve.divide(BigDecimal.valueOf(globalStatic.getd_2(w)),4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /*
    计算移动极差中位数,作为西格玛的无偏估计
     */
    public static double movingRangeMedianAsSigma(List<Double> groupMovingRange,int w){
        GlobalStatic globalStatic = new GlobalStatic();
        Collections.sort(groupMovingRange);
        int size = groupMovingRange.size();
        double median;
        if (size % 2 != 1){
            median = (groupMovingRange.get(size/2-1)+groupMovingRange.get(size/2)+0.0)/2;
        }else {
            median = groupMovingRange.get((size-1)/2);
        }
        BigDecimal data = BigDecimal.valueOf(median/globalStatic.getd_4(w));
        data =data.setScale(4,BigDecimal.ROUND_HALF_UP);
        return data.doubleValue();
    }

    /*
    使用均方递差的平方根方法（使用无偏常量）估计西格玛
    groupMovingRange:数据样本的移动极差
    n:数据样本的个数
    */
    public static double SRMSSDAsSigmaWithConstant(List<Double> groupMovingRange,int n){
        BigDecimal rangeSum = new BigDecimal("0.0");
        GlobalStatic globalStatic = new GlobalStatic();
        for (int i = 0;i<groupMovingRange.size();i++){
            rangeSum = rangeSum.add(BigDecimal.valueOf(groupMovingRange.get(i)*groupMovingRange.get(i)).setScale(4,BigDecimal.ROUND_HALF_UP));
        }
        rangeSum = BigDecimal.valueOf(0.5).multiply(rangeSum.divide(BigDecimal.valueOf(n-1),4,BigDecimal.ROUND_HALF_UP));
        double result = Math.sqrt(rangeSum.doubleValue())/globalStatic.getc_4p(n);
        rangeSum = BigDecimal.valueOf(result).setScale(4,BigDecimal.ROUND_HALF_UP);
        return rangeSum.doubleValue();
    }

    /*
    使用均方递差的平方根方法（不使用无偏常量）估计西格玛
    groupMovingRange:数据样本的移动极差
    n:数据样本的个数
    */
    public static double SRMSSDAsSigmaWithoutConstant(List<Double> groupMovingRange,int n){
        BigDecimal rangeSum = new BigDecimal("0.0");
        for (int i = 0;i<groupMovingRange.size();i++){
            rangeSum = rangeSum.add(BigDecimal.valueOf(groupMovingRange.get(i)*groupMovingRange.get(i)));
        }
        rangeSum = BigDecimal.valueOf(0.5).multiply(rangeSum.divide(BigDecimal.valueOf(n-1),4,BigDecimal.ROUND_HALF_UP));
        double result = Math.sqrt(rangeSum.doubleValue());
        rangeSum = BigDecimal.valueOf(result).setScale(4,BigDecimal.ROUND_HALF_UP);
        return rangeSum.doubleValue();
    }

    /*
    计算MR控制图的中心线以及控制上下限
    groupMovingRange:移动极差
    w:移动极差长度
    k:k倍西格玛
     */
    public static LimitsOfChart MRLimits(double sigma,int w,int k){
        LimitsOfChart result = new LimitsOfChart();
        GlobalStatic globalStatic = new GlobalStatic();
        BigDecimal cl = BigDecimal.valueOf(sigma).multiply(BigDecimal.valueOf(globalStatic.getd_2(w))).setScale(4,BigDecimal.ROUND_HALF_UP);
        BigDecimal lcl = BigDecimal.valueOf(globalStatic.getd_2(w) * sigma).subtract(BigDecimal.valueOf(k * globalStatic.getd_3(w) * sigma))
                .setScale(4,BigDecimal.ROUND_HALF_UP);
        if(lcl.compareTo(BigDecimal.ZERO) == -1){
            lcl = new BigDecimal("0");
        }
        BigDecimal ucl = BigDecimal.valueOf(globalStatic.getd_2(w) * sigma).add(BigDecimal.valueOf(k * globalStatic.getd_3(w) * sigma))
                .setScale(4,BigDecimal.ROUND_HALF_UP);
        List<Double> clList = new ArrayList<>();
        clList.add(cl.doubleValue());
        List<Double> lclList = new ArrayList<>();
        lclList.add(lcl.doubleValue());
        List<Double> uclList = new ArrayList<>();
        uclList.add(ucl.doubleValue());
        result.setCL(clList);
        result.setLCL(lclList);
        result.setUCL(uclList);
        return result;
    }

    /*
    计算p控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfP resultDataOfP(String dataValue,String sizeValue,int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data =new ArrayList<>();
        List<Integer> groupSize = new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data.add(Integer.parseInt(split[i]));
        }
        split = sizeValue.split(" ");
        for (int i =0;i<split.length;i++){
            groupSize.add(Integer.parseInt(split[i]));
        }
        ResultDataOfP result = new ResultDataOfP();
        List<Double> data_p = new ArrayList<>();
        List<Double> lcl_p = new ArrayList<>();
        List<Double> ucl_p = new ArrayList<>();
        int dataSum = 0;
        int groupSizeSum = 0;
        for (int i = 0;i<data.size();i++){
            dataSum += data.get(i);
            groupSizeSum += groupSize.get(i);
            data_p.add(BigDecimal.valueOf(data.get(i)/(groupSize.get(i)+0.0)).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        double cl_p = BigDecimal.valueOf(dataSum/(groupSizeSum+0.0)).doubleValue();
        for (int i = 0;i<data.size();i++){
            BigDecimal lcl = BigDecimal.valueOf(cl_p-k*Math.sqrt(cl_p*(1-cl_p)/(groupSize.get(i)+0.0)));
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            lcl_p.add(lcl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal ucl = BigDecimal.valueOf(cl_p+k*Math.sqrt(cl_p*(1-cl_p)/(groupSize.get(i)+0.0)));
            if(ucl.compareTo(BigDecimal.ONE) == 1){
                ucl= new BigDecimal("1");
            }
            ucl_p.add(ucl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setData_P(data_p);
        result.setCl_P(BigDecimal.valueOf(cl_p).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        result.setLcl_P(lcl_p);
        result.setUcl_P(ucl_p);
        return result;
    }

    /*
    计算Laney P'控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfLaneyP resultDataOfLaneyP(String dataValue, String sizeValue, int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data =new ArrayList<>();
        List<Integer> groupSize = new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data.add(Integer.parseInt(split[i]));
        }
        split = sizeValue.split(" ");
        for (int i =0;i<split.length;i++){
            groupSize.add(Integer.parseInt(split[i]));
        }
        ResultDataOfLaneyP result = new ResultDataOfLaneyP();
        List<Double> data_LaneyP = new ArrayList<>();
        List<Double> lcl_LaneyP = new ArrayList<>();
        List<Double> ucl_LaneyP = new ArrayList<>();
        List<BigDecimal> zList = new ArrayList<>();
        int dataSum = 0;
        int groupSizeSum = 0;
        for (int i = 0;i<data.size();i++){
            dataSum += data.get(i);
            groupSizeSum += groupSize.get(i);
            data_LaneyP.add(BigDecimal.valueOf(data.get(i)/(groupSize.get(i)+0.0)).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        double cl_LaneyP = BigDecimal.valueOf(dataSum/(groupSizeSum+0.0)).doubleValue();
        for (int i = 0;i<data.size();i++){
            double flush = Math.sqrt(cl_LaneyP*(1-cl_LaneyP)/(groupSize.get(i)+0.0));
            zList.add(BigDecimal.valueOf((data_LaneyP.get(i)-cl_LaneyP)/flush));
        }
        //将List拼接为String，方便使用写好的函数
        String z_String = "";
        for (int i = 0;i<zList.size()-1;i++){
            z_String += zList.get(i) + " ";
        }
        z_String += zList.get(zList.size()-1);
        //使用平均移动极差方法计算sigma （计算得出的sigma即为要求的sigmaZ）
        List<BigDecimal[]> z_data = Calculate.splitMovingRangeGroup(z_String,2);
        List<Double> rangeList = Calculate.calculateRange(z_data);
        double MR = Calculate.movingRangeAverageAsSigma(rangeList,2);
        double sigmaZ = BigDecimal.valueOf(MR).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue(); //这里并没有除1.128，因为移动极差平均值估计的sigma=MRbar/d2(w),这里d2(2)=1.128
        for (int i = 0;i<data.size();i++){
            double flush = Math.sqrt(cl_LaneyP*(1-cl_LaneyP)/(groupSize.get(i)+0.0));
            BigDecimal lcl = BigDecimal.valueOf(cl_LaneyP-k*flush*sigmaZ);
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            lcl_LaneyP.add(lcl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal ucl = BigDecimal.valueOf(cl_LaneyP+k*flush*sigmaZ);
            if(ucl.compareTo(BigDecimal.ONE) == 1){
                ucl= new BigDecimal("1");
            }
            ucl_LaneyP.add(ucl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setSigmaZ(sigmaZ);
        result.setData_LaneyP(data_LaneyP);
        result.setCl_LaneyP(BigDecimal.valueOf(cl_LaneyP).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        result.setLcl_LaneyP(lcl_LaneyP);
        result.setUcl_LaneyP(ucl_LaneyP);
        return result;
    }

    /*
    计算np控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfNP resultDataOfNP(String dataValue, String sizeValue, int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data_np =new ArrayList<>();
        List<Integer> groupSize = new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data_np.add(Integer.parseInt(split[i]));
        }
        split = sizeValue.split(" ");
        for (int i =0;i<split.length;i++){
            groupSize.add(Integer.parseInt(split[i]));
        }
        ResultDataOfNP result = new ResultDataOfNP();
        List<Double> cl_np = new ArrayList<>();
        List<Double> lcl_np = new ArrayList<>();
        List<Double> ucl_np = new ArrayList<>();
        int dataSum = 0;
        int groupSizeSum = 0;
        for (int i = 0;i<data_np.size();i++){
            dataSum += data_np.get(i);
            groupSizeSum += groupSize.get(i);
        }
        double p = BigDecimal.valueOf(dataSum/(groupSizeSum+0.0)).doubleValue();
        for (int i = 0;i<data_np.size();i++){
            cl_np.add(BigDecimal.valueOf(groupSize.get(i)*p).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal lcl = BigDecimal.valueOf(groupSize.get(i)*p-k*Math.sqrt(groupSize.get(i)*p*(1-p)));
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            lcl_np.add(lcl.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal ucl = BigDecimal.valueOf(groupSize.get(i)*p+k*Math.sqrt(groupSize.get(i)*p*(1-p)));
            if(ucl.compareTo(BigDecimal.valueOf(groupSize.get(i))) == 1){
                ucl= BigDecimal.valueOf(groupSize.get(i));
            }
            ucl_np.add(ucl.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setData_NP(data_np);
        result.setCl_NP(cl_np);
        result.setLcl_NP(lcl_np);
        result.setUcl_NP(ucl_np);
        return result;
    }

    /*
    计算U控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfU resultDataOfU(String dataValue,String sizeValue,int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data =new ArrayList<>();
        List<Integer> groupSize = new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data.add(Integer.parseInt(split[i]));
        }
        split = sizeValue.split(" ");
        for (int i =0;i<split.length;i++){
            groupSize.add(Integer.parseInt(split[i]));
        }
        ResultDataOfU result = new ResultDataOfU();
        List<Double> data_u = new ArrayList<>();
        List<Double> lcl_u = new ArrayList<>();
        List<Double> ucl_u = new ArrayList<>();
        int dataSum = 0;
        int groupSizeSum = 0;
        for (int i = 0;i<data.size();i++){
            dataSum += data.get(i);
            groupSizeSum += groupSize.get(i);
            data_u.add(BigDecimal.valueOf(data.get(i)/(groupSize.get(i)+0.0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        double cl_u = BigDecimal.valueOf(dataSum/(groupSizeSum+0.0)).doubleValue();
        for (int i = 0;i<data.size();i++){
            double flush = Math.sqrt(cl_u/(groupSize.get(i)+0.0));
            BigDecimal lcl = BigDecimal.valueOf(cl_u-k*flush);
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            lcl_u.add(lcl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal ucl = BigDecimal.valueOf(cl_u+k*flush);
            ucl_u.add(ucl.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setData_U(data_u);
        result.setCl_U(BigDecimal.valueOf(cl_u).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        result.setLcl_U(lcl_u);
        result.setUcl_U(ucl_u);
        return result;
    }

    /*
    计算Laney U'控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfLaneyU resultDataOfLaneyU(String dataValue,String sizeValue,int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data =new ArrayList<>();
        List<Integer> groupSize = new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data.add(Integer.parseInt(split[i]));
        }
        split = sizeValue.split(" ");
        for (int i =0;i<split.length;i++){
            groupSize.add(Integer.parseInt(split[i]));
        }
        ResultDataOfLaneyU result = new ResultDataOfLaneyU();
        List<Double> data_LaneyU = new ArrayList<>();
        List<Double> lcl_LaneyU = new ArrayList<>();
        List<Double> ucl_LaneyU = new ArrayList<>();
        List<BigDecimal> zList = new ArrayList<>();
        int dataSum = 0;
        int groupSizeSum = 0;
        for (int i = 0;i<data.size();i++){
            dataSum += data.get(i);
            groupSizeSum += groupSize.get(i);
            data_LaneyU.add(BigDecimal.valueOf(data.get(i)/(groupSize.get(i)+0.0)).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        double cl_LaneyU = BigDecimal.valueOf(dataSum/(groupSizeSum+0.0)).doubleValue();
        for (int i = 0;i<data.size();i++){
            double flush = Math.sqrt(cl_LaneyU/(groupSize.get(i)+0.0));
            zList.add(BigDecimal.valueOf((data_LaneyU.get(i)-cl_LaneyU)/flush));
        }
        //将List拼接为String，方便使用写好的函数
        String z_String = "";
        for (int i = 0;i<zList.size()-1;i++){
            z_String += zList.get(i) + " ";
        }
        z_String += zList.get(zList.size()-1);
        //使用平均移动极差方法计算sigma （计算得出的sigma即为要求的sigmaZ）
        List<BigDecimal[]> z_data = Calculate.splitMovingRangeGroup(z_String,2);
        List<Double> rangeList = Calculate.calculateRange(z_data);
        double MR = Calculate.movingRangeAverageAsSigma(rangeList,2);
        double sigmaZ = BigDecimal.valueOf(MR).setScale(6,BigDecimal.ROUND_HALF_UP).doubleValue(); //这里并没有除1.128，因为移动极差平均值估计的sigma=MRbar/d2(w),这里d2(2)=1.128
        for (int i = 0;i<data.size();i++){
            double flush = Math.sqrt(cl_LaneyU/(groupSize.get(i)+0.0));
            BigDecimal lcl = BigDecimal.valueOf(cl_LaneyU-k*flush*sigmaZ);
            if(lcl.compareTo(BigDecimal.ZERO) == -1){
                lcl= new BigDecimal("0");
            }
            lcl_LaneyU.add(lcl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal ucl = BigDecimal.valueOf(cl_LaneyU+k*flush*sigmaZ);
            if(ucl.compareTo(BigDecimal.ONE) == 1){
                ucl= new BigDecimal("1");
            }
            ucl_LaneyU.add(ucl.setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        result.setSigmaZ(sigmaZ);
        result.setData_LaneyU(data_LaneyU);
        result.setCl_LaneyU(BigDecimal.valueOf(cl_LaneyU).setScale(5,BigDecimal.ROUND_HALF_UP).doubleValue());
        result.setLcl_LaneyU(lcl_LaneyU);
        result.setUcl_LaneyU(ucl_LaneyU);
        return result;
    }

    /*
    计算np控制图的数据、中心线以及控制上下限
     */
    public static ResultDataOfC resultDataOfC(String dataValue, int k){ //需检验data的size和groupSize的size是否一致
        List<Integer> data_c =new ArrayList<>();
        String[] split = dataValue.split(" ");
        for (int i =0;i<split.length;i++){
            data_c.add(Integer.parseInt(split[i]));
        }
        ResultDataOfC result = new ResultDataOfC();
        int dataSum = 0;
        for (int i = 0;i<data_c.size();i++){
            dataSum += data_c.get(i);
        }
        double cl_c = BigDecimal.valueOf(dataSum/(data_c.size()+0.0)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        double lcl_c = BigDecimal.valueOf(cl_c-k*Math.sqrt(cl_c)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        if (lcl_c < 0){
            lcl_c = 0;
        }
        double ucl_c = BigDecimal.valueOf(cl_c+k*Math.sqrt(cl_c)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        result.setData_C(data_c);
        result.setCl_C(cl_c);
        result.setLcl_C(lcl_c);
        result.setUcl_C(ucl_c);
        return result;
    }
}