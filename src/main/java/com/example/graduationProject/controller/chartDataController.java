package com.example.graduationProject.controller;

import com.example.graduationProject.Calculate;
import com.example.graduationProject.Criterion;
import com.example.graduationProject.entity.*;
import com.example.graduationProject.service.chartDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(value = "/chartData")
@Api(value = "数据接口", tags = "数据接口" )
public class chartDataController {

    @Autowired
    private  chartDataService chartdataService;

    @RequestMapping(value = "/selectByDataID", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据",notes = "查询数据")
    public List<ChartData> selectByDataID(@RequestParam(value = "dataId") int dataId) {
        log.info("开始查询...");
        List<ChartData> chartData = chartdataService.selectByDataId(dataId);
        return chartData;
    }
    @RequestMapping(value = "/insertChartData", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据",notes = "新增数据")
    public boolean insertChartData(@RequestBody ChartData chartData) {
        log.info("开始新增...={}",chartData);
        chartdataService.insertChartData(chartData);
        return true;
    }

    @RequestMapping(value = "/updateChartData", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据", notes = "修改数据")
    @CrossOrigin //跨域请求
    public boolean updateChartData(@RequestBody ChartData chartData) {
        log.info("开始修改...{}",chartData);
        List<ChartData> datas = chartdataService.selectByDataId(chartData.getDataId());
        if(datas.size()==1){
            return chartdataService.updateChartData(chartData);
        }else return false;
    }

    @RequestMapping(value = "/deleteChartData", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据", notes = "删除数据")
    @CrossOrigin //跨域请求
    public boolean deleteChartData( @RequestParam(value = "dataId") int dataId) {
        log.info("开始删除...{}",dataId);
        List<ChartData> datas = chartdataService.selectByDataId(dataId);
        if (datas.size()==1){
            return chartdataService.deleteChartData(dataId);
        }else return false;
    }

    /*
     * 计算Xbar绘图数据
     */
    @RequestMapping(value = "/Xbar", method = RequestMethod.POST)
    @ApiOperation(value = "计算Xbar绘图数据",notes = "计算Xbar绘图数据")
    public ResultDataOfXbar Xbar(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "subGroupSize") int subGroupSize,
                                 @RequestParam(value = "k") int k,@RequestParam(value = "sigmaMode") String sigmaMode,
                                 @RequestParam(value = "criterion") String criterion,@RequestParam(value = "criterionData") List<Integer> criterionData) {
        log.info("开始计算Xbar绘图数据...");
        ResultDataOfXbar resultData = new ResultDataOfXbar();
        int dataSize = dataValue.split(" ").length;
        double CL_Xbar = Calculate.calculateDataAve(dataValue,dataSize);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,subGroupSize);
        List<Double> data_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<Double> groupRange_R = Calculate.calculateRange(data);
        double standardDeviation = 0;
        if (sigmaMode.equals("RbarSigma")){
            standardDeviation = Calculate.RbarSigma(data,groupRange_R);
        }else if (sigmaMode.equals("SbarSigmaWithConstant")){
            standardDeviation = Calculate.SbarSigmaWithConstant(data,data_Xbar);
        }else if (sigmaMode.equals("SbarSigmaWithoutConstant")){
            standardDeviation = Calculate.SbarSigmaWithoutConstant(data,data_Xbar);
        }else if (sigmaMode.equals("UnionSigmaWithConstant")){
            standardDeviation = Calculate.UnionSigmaWithConstant(data,data_Xbar);
        }
        else if(sigmaMode.equals("UnionSigmaWithoutConstant")){
            standardDeviation = Calculate.UnionSigmaWithoutConstant(data,data_Xbar);
        }

        List<Double> cl = new ArrayList<>();
        for (int i = 0;i<data_Xbar.size();i++){
            cl.add(CL_Xbar);
        }
        List<Double> UCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getUCL();
        List<Double> LCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getLCL();

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion(data_Xbar,cl,standardDeviation,criterion,criterionData);

        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setCL_Xbar(CL_Xbar);
        resultData.setData_Xbar(data_Xbar);
        resultData.setUCL_Xbar(UCL_Xbar);
        resultData.setLCL_Xbar(LCL_Xbar);
        resultData.setCriterionList(resultOfCriterionList);
        return resultData;
    }

    /*
     * 计算R控制图数据
     */
    @RequestMapping(value = "/R", method = RequestMethod.POST)
    @ApiOperation(value = "计算R控制图数据",notes = "计算R控制图数据")
    public ResultDataOfR R(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "subGroupSize") int subGroupSize,
                           @RequestParam(value = "k") int k,@RequestParam(value = "sigmaMode") String sigmaMode,
                           @RequestParam(value = "criterion") String criterion,@RequestParam(value = "criterionData") List<Integer> criterionData) {
        log.info("开始计算Xbar_R绘图数据...");
        ResultDataOfR resultData = new ResultDataOfR();
        int dataSize = dataValue.split(" ").length;
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,subGroupSize);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<Double> groupRange_R = Calculate.calculateRange(data);
        double standardDeviation = 0;
        if (sigmaMode.equals("RbarSigma")){
            standardDeviation = Calculate.RbarSigma(data,groupRange_R);
        }else if (sigmaMode.equals("UnionSigmaWithConstant")){
            standardDeviation = Calculate.UnionSigmaWithConstant(data,groupAve_Xbar);
        }
        else if(sigmaMode.equals("UnionSigmaWithoutConstant")){
            standardDeviation = Calculate.UnionSigmaWithoutConstant(data,groupAve_Xbar);
        }
        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        LimitsOfChart limitsOfChart = Calculate.calculateLimitsOfR(data,standardDeviation,k);

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion(groupRange_R,limitsOfChart.getCL(),standardDeviation,criterion,criterionData);

        resultData.setCL_R(limitsOfChart.getCL());
        resultData.setUCL_R(limitsOfChart.getUCL());
        resultData.setLCL_R(limitsOfChart.getLCL());
        resultData.setData_R(groupRange_R);
        resultData.setCriterionList(resultOfCriterionList);
        return resultData;
    }


    /*
     * 计算S控制图数据
     */
    @RequestMapping(value = "/S", method = RequestMethod.POST)
    @ApiOperation(value = "计算S控制图数据",notes = "计算S控制图数据")
    public ResultDataOfS S(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "subGroupSize") int subGroupSize,
                           @RequestParam(value = "k") int k, @RequestParam(value = "sigmaMode") String sigmaMode,
                           @RequestParam(value = "criterion") String criterion,@RequestParam(value = "criterionData") List<Integer> criterionData) {
        log.info("开始计算S控制图数据...");
        ResultDataOfS resultData = new ResultDataOfS();
        int dataSize = dataValue.split(" ").length;
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,subGroupSize);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<BigDecimal> groupSigma = Calculate.calculateGroupSigma(data,groupAve_Xbar);
        List<Double> data_S = new ArrayList<>();
        for (int i = 0;i<groupSigma.size();i++){
            data_S.add(groupSigma.get(i).doubleValue());
        }
        double standardDeviation = 0;
        LimitsOfChart limitsOfChart = new LimitsOfChart();
        if (sigmaMode.equals("SbarSigmaWithConstant")){
            standardDeviation = Calculate.SbarSigmaWithConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("SbarSigmaWithoutConstant")){
            standardDeviation = Calculate.SbarSigmaWithoutConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithoutConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("UnionSigmaWithConstant")){
            standardDeviation = Calculate.UnionSigmaWithConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("UnionSigmaWithoutConstant")){
            standardDeviation = Calculate.UnionSigmaWithoutConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithoutConstant(data,standardDeviation,k);
        }

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion(data_S,limitsOfChart.getCL(),standardDeviation,criterion,criterionData);

        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setData_S(data_S);
        resultData.setCL_S(limitsOfChart.getCL());
        resultData.setUCL_S(limitsOfChart.getUCL());
        resultData.setLCL_S(limitsOfChart.getLCL());
        resultData.setCriterionList(resultOfCriterionList);
        return resultData;
    }

    /*
    * 计算Xbar_R绘图数据
    */
    @RequestMapping(value = "/Xbar_R", method = RequestMethod.POST)
    @ApiOperation(value = "计算Xbar_R绘图数据",notes = "计算Xbar_R绘图数据")
    public List<ResultDataOfXbar_R> Xbar_R(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "subGroupSize") int subGroupSize,
                                               @RequestParam(value = "k") int k,@RequestParam(value = "sigmaMode") String sigmaMode) {
        log.info("开始计算Xbar_R绘图数据...");
        ResultDataOfXbar_R resultData = new ResultDataOfXbar_R();
        int dataSize = dataValue.split(" ").length;
        double CL_Xbar = Calculate.calculateDataAve(dataValue,dataSize);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,subGroupSize);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<Double> groupRange_R = Calculate.calculateRange(data);
        double standardDeviation;
        if (sigmaMode.equals("RbarSigma")){
            standardDeviation = Calculate.RbarSigma(data,groupRange_R);
        }else if(sigmaMode.equals("UnionSigmaWithoutConstant")){
            standardDeviation = Calculate.UnionSigmaWithoutConstant(data,groupAve_Xbar);
        }else {
            standardDeviation = Calculate.UnionSigmaWithConstant(data,groupAve_Xbar);
        }
        List<Double> UCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getUCL();
        List<Double> LCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getLCL();
        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setCL_Xbar(CL_Xbar);
        resultData.setGroupAve_Xbar(groupAve_Xbar);
        resultData.setUCL_Xbar(UCL_Xbar);
        resultData.setLCL_Xbar(LCL_Xbar);
        LimitsOfChart limitsOfChart = Calculate.calculateLimitsOfR(data,standardDeviation,k);
        resultData.setCL_R(limitsOfChart.getCL());
        resultData.setUCL_R(limitsOfChart.getUCL());
        resultData.setLCL_R(limitsOfChart.getLCL());
        resultData.setGroupRange_R(groupRange_R);
        List<ResultDataOfXbar_R> resultDataList = new ArrayList<>();
        resultDataList.add(resultData);
        return resultDataList;
    }


    /*
     * 计算Xbar_S绘图数据
     */
    @RequestMapping(value = "/Xbar_S", method = RequestMethod.POST)
    @ApiOperation(value = "计算Xbar_S绘图数据",notes = "计算Xbar_S绘图数据")
    public ResultDataOfXbar_S Xbar_S(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "subGroupSize") int subGroupSize,
                                           @RequestParam(value = "k") int k, @RequestParam(value = "sigmaMode") String sigmaMode) {
        log.info("开始计算Xbar_S绘图数据...");
        ResultDataOfXbar_S resultData = new ResultDataOfXbar_S();
        int dataSize = dataValue.split(" ").length;
        double CL_Xbar = Calculate.calculateDataAve(dataValue,dataSize);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,subGroupSize);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        double standardDeviation = 0;
        LimitsOfChart limitsOfChart = new LimitsOfChart();
        if (sigmaMode.equals("SbarSigmaWithConstant")){
            standardDeviation = Calculate.SbarSigmaWithConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("SbarSigmaWithoutConstant")){
            standardDeviation = Calculate.SbarSigmaWithoutConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithoutConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("UnionSigmaWithConstant")){
            standardDeviation = Calculate.UnionSigmaWithConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithConstant(data,standardDeviation,k);
        }else if(sigmaMode.equals("UnionSigmaWithoutConstant")){
            standardDeviation = Calculate.UnionSigmaWithoutConstant(data,groupAve_Xbar);
            limitsOfChart = Calculate.LimitsOfSWithoutConstant(data,standardDeviation,k);
        }
        List<Double> UCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getUCL();
        List<Double> LCL_Xbar = Calculate.calculateUCLAndLCLOfXbar(data,CL_Xbar,k,standardDeviation).getLCL();
        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setCL_Xbar(CL_Xbar);
        resultData.setGroupAve_Xbar(groupAve_Xbar);
        resultData.setUCL_Xbar(UCL_Xbar);
        resultData.setLCL_Xbar(LCL_Xbar);
//        LimitsOfChart limitsOfChart = Calculate.calculateLimitsOfR(data,standardDeviation,k);
        resultData.setCL_S(limitsOfChart.getCL());
        resultData.setUCL_S(limitsOfChart.getUCL());
        resultData.setLCL_S(limitsOfChart.getLCL());
        return resultData;
    }

    /*
     * 计算单值控制图数据
     */
    @RequestMapping(value = "/I", method = RequestMethod.POST)
    @ApiOperation(value = "计算单值控制图数据",notes = "计算单值控制图数据")
    public ResultDataOfI SingleValue(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "movingRangeLength") int movingRangeLength,
                                     @RequestParam(value = "k") int k, @RequestParam(value = "sigmaMode") String sigmaMode,
                                     @RequestParam(value = "criterion") String criterion,@RequestParam(value = "criterionData") List<Integer> criterionData) {
        log.info("开始计算单值控制图数据...");
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(dataValue,movingRangeLength);
        ResultDataOfI resultData = new ResultDataOfI();
        String[] split = dataValue.split(" ");
        int dataSize = split.length;
        List<Double> SingleValueData = new ArrayList<>();
        for (int i = 0;i<dataSize;i++){
            SingleValueData.add(new BigDecimal(split[i]).doubleValue());
        }
        double CL_SingleValue = Calculate.calculateDataAve(dataValue,dataSize);
        List<Double> movingRangeList = Calculate.calculateRange(data);
        double sigma = 0.0;
        if (sigmaMode.equals("movingRangeAverageAsSigma")){
            sigma = Calculate.movingRangeAverageAsSigma(movingRangeList,movingRangeLength);
        }else if (sigmaMode.equals("movingRangeMedianAsSigma")){
            sigma = Calculate.movingRangeMedianAsSigma(movingRangeList,movingRangeLength);
        }else if (sigmaMode.equals("SRMSSDAsSigmaWithConstant")){
            sigma = Calculate.SRMSSDAsSigmaWithConstant(movingRangeList,dataSize);
        }else if (sigmaMode.equals("SRMSSDAsSigmaWithoutConstant")){
            sigma = Calculate.SRMSSDAsSigmaWithoutConstant(movingRangeList,dataSize);
        }
        double UCL_SingleValue = BigDecimal.valueOf(CL_SingleValue + k * sigma).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        double LCL_SingleValue = BigDecimal.valueOf(CL_SingleValue - k * sigma).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        List<Double> cl =new ArrayList<>();

        for (int i = 0;i<SingleValueData.size();i++){
            cl.add(CL_SingleValue);
        }
        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion(SingleValueData,cl,sigma,criterion,criterionData);

        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setData_I(SingleValueData);
        resultData.setCl_I(CL_SingleValue);
        resultData.setUcl_I(UCL_SingleValue);
        resultData.setLcl_I(LCL_SingleValue);
        resultData.setCriterionList(resultOfCriterionList);
        return resultData;
    }

    /*
     * 计算移动极差控制图MR数据
     */
    @RequestMapping(value = "/MR", method = RequestMethod.POST)
    @ApiOperation(value = "计算MR控制图数据",notes = "计算MR控制图数据")
    public ResultDataOfMR MR(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "movingRangeLength") int movingRangeLength,
                             @RequestParam(value = "k") int k, @RequestParam(value = "sigmaMode") String sigmaMode,
                             @RequestParam(value = "criterion") String criterion,@RequestParam(value = "criterionData") List<Integer> criterionData) {
        log.info("开始计算MR...");
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(dataValue,movingRangeLength);
        List<Double> groupMovingRange = Calculate.calculateRange(data);
        String[] split = dataValue.split(" ");
        int dataSize = split.length;
        ResultDataOfMR resultData = new ResultDataOfMR();
        double sigma = 0.0;
        if (sigmaMode.equals("movingRangeAverageAsSigma")){
            sigma = Calculate.movingRangeAverageAsSigma(groupMovingRange,movingRangeLength);
        }else if (sigmaMode.equals("movingRangeMedianAsSigma")){
            sigma = Calculate.movingRangeMedianAsSigma(groupMovingRange,movingRangeLength);
        }
        LimitsOfChart MR = Calculate.MRLimits(sigma,movingRangeLength,k);
        double cl_MR = BigDecimal.valueOf(MR.getCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        double lcl_MR = BigDecimal.valueOf(MR.getLCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        double ucl_MR = BigDecimal.valueOf(MR.getUCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();

        List<Double> cl =new ArrayList<>();
        List<Double> lcl =new ArrayList<>();
        List<Double> ucl =new ArrayList<>();

        for (int i = 0;i<groupMovingRange.size();i++){
            cl.add(cl_MR);
            lcl.add(lcl_MR);
            ucl.add(ucl_MR);
        }

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterionOfMR(groupMovingRange,cl,lcl,ucl,criterion,criterionData);

        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);
        resultData.setData_MR(groupMovingRange);
        resultData.setCl_MR(cl_MR);
        resultData.setUcl_MR(ucl_MR);
        resultData.setLcl_MR(lcl_MR);

        resultData.setCriterionList(resultOfCriterionList);

        return resultData;
    }

    /*
    计算I-MR控制图数据
     */
    @RequestMapping(value = "/I-MR", method = RequestMethod.POST)
    @ApiOperation(value = "计算I-MR控制图数据",notes = "计算I-MR控制图数据")
    public ResultDataOfI_MR I_MR(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "movingRangeLength") int movingRangeLength,
                             @RequestParam(value = "k") int k, @RequestParam(value = "sigmaMode") String sigmaMode) {
        log.info("开始计算MR...");
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(dataValue,movingRangeLength);
        List<Double> groupMovingRange = Calculate.calculateRange(data);
        String[] split = dataValue.split(" ");
        int dataSize = split.length;
        List<Double> SingleValueData = new ArrayList<>();
        for (int i = 0;i<dataSize;i++){
            SingleValueData.add(new BigDecimal(split[i]).doubleValue());
        }
        double CL_SingleValue = Calculate.calculateDataAve(dataValue,dataSize);
        ResultDataOfI_MR resultData = new ResultDataOfI_MR();
        double sigma = 0.0;
        if (sigmaMode.equals("movingRangeAverageAsSigma")){
            sigma = Calculate.movingRangeAverageAsSigma(groupMovingRange,movingRangeLength);
        }else if (sigmaMode.equals("movingRangeMedianAsSigma")){
            sigma = Calculate.movingRangeMedianAsSigma(groupMovingRange,movingRangeLength);
        }
        double UCL_SingleValue = CL_SingleValue + k * sigma;
        double LCL_SingleValue = CL_SingleValue - k * sigma;
        LimitsOfChart MR = Calculate.MRLimits(sigma,movingRangeLength,k);
        resultData.setDataSize(dataSize);
        resultData.setDataValue(dataValue);

        resultData.setData_I(SingleValueData);
        resultData.setCl_I(BigDecimal.valueOf(CL_SingleValue).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        resultData.setUcl_I(BigDecimal.valueOf(UCL_SingleValue).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        resultData.setLcl_I(BigDecimal.valueOf(LCL_SingleValue).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());

        resultData.setData_MR(groupMovingRange);
        resultData.setCl_MR(BigDecimal.valueOf(MR.getCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        resultData.setUcl_MR(BigDecimal.valueOf(MR.getUCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        resultData.setLcl_MR(BigDecimal.valueOf(MR.getLCL().get(0)).setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue());
        return resultData;
    }

    /*
    计算P控制图数据
     */
    @RequestMapping(value = "/P", method = RequestMethod.POST)
    @ApiOperation(value = "计算P控制图数据",notes = "计算P控制图数据")
    public ResultDataOfP P(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "sizeValue") String sizeValue,
                           @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                           @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfP result = new ResultDataOfP();
        String[] splitOfData = dataValue.split(" ");
        String[] splitOfSize = sizeValue.split(" ");
        if(splitOfData.length != splitOfSize.length){       //还未判断只填一个数表示等样本量数据
            result.setAvailable(false);
            result.setMsg("数据不规范！（缺陷产品组数与采样组数不一致）");
            return result;
        }else {
            for (int i = 0;i<splitOfData.length;i++){
                if(Integer.parseInt(splitOfData[i]) > Integer.parseInt(splitOfSize[i])){
                    result.setAvailable(false);
                    result.setMsg("数据不规范！（样本数量至少必须和最大计数一样大）");
                    return result;
                }
            }
        }


        ResultDataOfP resultDataOfP = Calculate.resultDataOfP(dataValue,sizeValue,k);


        List<Double> cl = new ArrayList<>();
        for (int i = 0;i<resultDataOfP.getData_P().size();i++){
            cl.add(resultDataOfP.getCl_P());
        }

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfP.getData_P(),cl,resultDataOfP.getLcl_P(), resultDataOfP.getUcl_P(),criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setData_P(resultDataOfP.getData_P());
        result.setCl_P(resultDataOfP.getCl_P());
        result.setUcl_P(resultDataOfP.getUcl_P());
        result.setLcl_P(resultDataOfP.getLcl_P());
        result.setCriterionList(resultOfCriterionList);
        return result;
    }

    /*
    计算Laney P'控制图数据
     */
    @RequestMapping(value = "/LaneyP", method = RequestMethod.POST)
    @ApiOperation(value = "计算Laney P'控制图数据",notes = "计算Laney P'控制图数据")
    public ResultDataOfLaneyP LaneyP(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "sizeValue") String sizeValue,
                                     @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                                     @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfLaneyP result = new ResultDataOfLaneyP();
        String[] splitOfData = dataValue.split(" ");
        String[] splitOfSize = sizeValue.split(" ");
        if(splitOfData.length != splitOfSize.length){       //还未判断只填一个数表示等样本量数据
            result.setAvailable(false);
            result.setMsg("数据不规范！（缺陷产品组数与采样组数不一致）");
            return result;
        }else {
           for (int i = 0;i<splitOfData.length;i++){
               if(Integer.parseInt(splitOfData[i]) > Integer.parseInt(splitOfSize[i])){
                   result.setAvailable(false);
                   result.setMsg("数据不规范！（样本数量至少必须和最大计数一样大）");
                   return result;
               }
           }
        }
        ResultDataOfLaneyP resultDataOfLaneyP = Calculate.resultDataOfLaneyP(dataValue,sizeValue,k);

        List<Double> cl = new ArrayList<>();
        for (int i = 0;i<resultDataOfLaneyP.getData_LaneyP().size();i++){
            cl.add(resultDataOfLaneyP.getCl_LaneyP());
        }

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfLaneyP.getData_LaneyP(),cl,resultDataOfLaneyP.getLcl_LaneyP(), resultDataOfLaneyP.getUcl_LaneyP(),criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setSigmaZ(resultDataOfLaneyP.getSigmaZ());
        result.setData_LaneyP(resultDataOfLaneyP.getData_LaneyP());
        result.setCl_LaneyP(resultDataOfLaneyP.getCl_LaneyP());
        result.setUcl_LaneyP(resultDataOfLaneyP.getUcl_LaneyP());
        result.setLcl_LaneyP(resultDataOfLaneyP.getLcl_LaneyP());

        result.setCriterionList(resultOfCriterionList);

        return result;
    }

    /*
    计算NP控制图数据
     */
    @RequestMapping(value = "/NP", method = RequestMethod.POST)
    @ApiOperation(value = "计算NP控制图数据",notes = "计算NP控制图数据")
    public ResultDataOfNP NP(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "sizeValue") String sizeValue,
                             @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                             @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfNP result = new ResultDataOfNP();
        String[] splitOfData = dataValue.split(" ");
        String[] splitOfSize = sizeValue.split(" ");
        if(splitOfData.length != splitOfSize.length){       //还未判断只填一个数表示等样本量数据
            result.setAvailable(false);
            result.setMsg("数据不规范！（缺陷产品组数与采样组数不一致）");
            return result;
        }else {
            for (int i = 0;i<splitOfData.length;i++){
                if(Integer.parseInt(splitOfData[i]) > Integer.parseInt(splitOfSize[i])){
                    result.setAvailable(false);
                    result.setMsg("数据不规范！（样本数量至少必须和最大计数一样大）");
                    return result;
                }
            }
        }

        ResultDataOfNP resultDataOfNP = Calculate.resultDataOfNP(dataValue,sizeValue,k);

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfNP.getData_NP()
                .stream()
                .map(Integer::doubleValue)
                .collect(Collectors.toList()),resultDataOfNP.getCl_NP(), resultDataOfNP.getLcl_NP(), resultDataOfNP.getUcl_NP(),criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setData_NP(resultDataOfNP.getData_NP());
        result.setCl_NP(resultDataOfNP.getCl_NP());
        result.setUcl_NP(resultDataOfNP.getUcl_NP());
        result.setLcl_NP(resultDataOfNP.getLcl_NP());
        result.setCriterionList(resultOfCriterionList);
        return result;
    }

    /*
    计算U控制图数据
     */
    @RequestMapping(value = "/U", method = RequestMethod.POST)
    @ApiOperation(value = "计算U控制图数据",notes = "计算U控制图数据")
    public ResultDataOfU U(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "sizeValue") String sizeValue,
                           @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                           @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfU result = new ResultDataOfU();
        String[] splitOfData = dataValue.split(" ");
        String[] splitOfSize = sizeValue.split(" ");
        if(splitOfData.length != splitOfSize.length ){      //还未判断只填一个数表示等样本量数据
            result.setAvailable(false);
            result.setMsg("数据不规范！（缺陷产品组数与采样组数不一致）");
            return result;
        }
        ResultDataOfU resultDataOfU = Calculate.resultDataOfU(dataValue,sizeValue,k);

        List<Double> cl = new ArrayList<>();
        for (int i = 0;i<resultDataOfU.getData_U().size();i++){
            cl.add(resultDataOfU.getCl_U());
        }
        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfU.getData_U(),cl,resultDataOfU.getLcl_U(), resultDataOfU.getUcl_U(),criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setData_U(resultDataOfU.getData_U());
        result.setCl_U(resultDataOfU.getCl_U());
        result.setUcl_U(resultDataOfU.getUcl_U());
        result.setLcl_U(resultDataOfU.getLcl_U());
        result.setCriterionList(resultOfCriterionList);
        return result;
    }

    /*
    计算Laney U'控制图数据
     */
    @RequestMapping(value = "/LaneyU", method = RequestMethod.POST)
    @ApiOperation(value = "计算Laney U'控制图数据",notes = "计算Laney U'控制图数据")
    public ResultDataOfLaneyU LaneyU(@RequestParam(value = "dataValue") String dataValue, @RequestParam(value = "sizeValue") String sizeValue,
                                     @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                                     @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfLaneyU result = new ResultDataOfLaneyU();
        String[] splitOfData = dataValue.split(" ");
        String[] splitOfSize = sizeValue.split(" ");
        if(splitOfData.length != splitOfSize.length){       //还未判断只填一个数表示等样本量数据
            result.setAvailable(false);
            result.setMsg("数据不规范！（缺陷产品组数与采样组数不一致）");
            return result;
        }
        ResultDataOfLaneyU resultDataOfLaneyU = Calculate.resultDataOfLaneyU(dataValue,sizeValue,k);

        List<Double> cl = new ArrayList<>();
        for (int i = 0;i<resultDataOfLaneyU.getData_LaneyU().size();i++){
            cl.add(resultDataOfLaneyU.getCl_LaneyU());
        }
        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfLaneyU.getData_LaneyU(),cl,resultDataOfLaneyU.getLcl_LaneyU(), resultDataOfLaneyU.getUcl_LaneyU(),criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setSigmaZ(resultDataOfLaneyU.getSigmaZ());
        result.setData_LaneyU(resultDataOfLaneyU.getData_LaneyU());
        result.setCl_LaneyU(resultDataOfLaneyU.getCl_LaneyU());
        result.setUcl_LaneyU(resultDataOfLaneyU.getUcl_LaneyU());
        result.setLcl_LaneyU(resultDataOfLaneyU.getLcl_LaneyU());
        result.setCriterionList(resultOfCriterionList);
        return result;
    }

    /*
    计算C控制图数据
     */
    @RequestMapping(value = "/C", method = RequestMethod.POST)
    @ApiOperation(value = "计算C控制图数据",notes = "计算C控制图数据")
    public ResultDataOfC C(@RequestParam(value = "dataValue") String dataValue,
                           @RequestParam(value = "k") int k,@RequestParam(value = "criterion") String criterion,
                           @RequestParam(value = "criterionData") List<Integer> criterionData){
        ResultDataOfC result = new ResultDataOfC();
        String[] splitOfData = dataValue.split(" ");
        ResultDataOfC resultDataOfC = Calculate.resultDataOfC(dataValue,k);
        List<Double> cl = new ArrayList<>();
        List<Double> lcl = new ArrayList<>();
        List<Double> ucl = new ArrayList<>();
        for (int i = 0;i<resultDataOfC.getData_C().size();i++){
            cl.add(resultDataOfC.getCl_C());
            lcl.add(resultDataOfC.getLcl_C());
            ucl.add(resultDataOfC.getUcl_C());
        }

        List<ResultOfCriterion> resultOfCriterionList = Criterion.ResultOfCriterion2(resultDataOfC.getData_C()
                .stream()
                .map(Integer::doubleValue)
                .collect(Collectors.toList()),cl, lcl, ucl,criterion,criterionData);

        result.setDataSize(splitOfData.length);
        result.setAvailable(true);
        result.setDataValue(dataValue);
        result.setData_C(resultDataOfC.getData_C());
        result.setCl_C(resultDataOfC.getCl_C());
        result.setUcl_C(resultDataOfC.getUcl_C());
        result.setLcl_C(resultDataOfC.getLcl_C());
        result.setCriterionList(resultOfCriterionList);
        return result;
    }

}

