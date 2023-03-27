package com.example.graduationProject;

import com.example.graduationProject.entity.ResultDataOfI;
import com.example.graduationProject.entity.ResultDataOfP;
import com.example.graduationProject.mapper.LoginMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class BugTest {

    @Resource
    private LoginMapper loginMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testConnect(){
        Long al=jdbcTemplate.queryForObject("select count(*) from login",Long.class);
        log.info("test connect:{}条",al);
    }

    @Test
    public void testLogin(){
        String loginAccount="0";
        System.out.println(Md5.code(loginAccount));
    }
    @Test
    public void testCaluate(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        String data2 = "0.6269 0.6028 0.59889 0.5684 0.6234 0.5964 0.5435 0.5994 0.5437 0.5996 0.6537 0.5751 0.6004 0.6187 0.5826 0.5769 0.598 0.6021 0.5989 0.5967";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
//        double ave = Calculate.calculateAve().get(0);
        System.out.println(Calculate.calculateGroupAveOfXbar(data));
    }
    @Test
    public void testRange(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,3);
        List<Double> rangeList = Calculate.calculateRange(data);
        System.out.println(rangeList);
    }
    @Test
    public void testSplitGroup(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,10);
        for(int i = 0;i<data.size();i++){
            System.out.println(Arrays.toString(data.get(i)));
        }
    }
    @Test
    public void testStandardDeviation(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
        List<Double> aveList = Calculate.calculateGroupAveOfXbar(data);
        System.out.println(aveList);
        System.out.println(Calculate.UnionSigmaWithoutConstant(data,aveList));
        System.out.println(Calculate.UnionSigmaWithConstant(data,aveList));
    }
    @Test
    public void testBigDecimal(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
        List<Double> aveList = Calculate.calculateGroupAveOfXbar(data);
        BigDecimal c = data.get(0)[0].subtract(BigDecimal.valueOf(aveList.get(0)));
        BigDecimal a = new BigDecimal("-0.6");
        BigDecimal b = new BigDecimal("-0.6");
        System.out.println(c.multiply(c));
    }
    @Test
    public void testCalculateSumAve(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        System.out.println(Calculate.calculateDataAve(datas,20));
    }

    @Test
    public void testUCl(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
        List<Double> aveList = Calculate.calculateGroupAveOfXbar(data);
        double aveData = Calculate.calculateDataAve(datas,20);
//        double standardDeviation = Calculate.UnionSigma(data,aveList);
        double standardDeviation = Calculate.UnionSigmaWithConstant(data,aveList);
        System.out.println(Calculate.calculateUCLAndLCLOfXbar(data,aveData,3,standardDeviation));
    }
    @Test
    public void testStatic(){
        GlobalStatic globalStatic = new GlobalStatic();
        System.out.println(globalStatic.getc_4p(500));
    }
    @Test
    public void testRbarSigma(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
        List<Double> rangeList = Calculate.calculateRange(data);
        double sigma  = Calculate.RbarSigma(data,rangeList);
        double cl_Xbar = Calculate.calculateDataAve(datas,20);
        System.out.println(Calculate.calculateDataAve(datas,20));
        System.out.println(Calculate.RbarSigma(data,rangeList));
        System.out.println(Calculate.calculateUCLAndLCLOfXbar(data,cl_Xbar,3,sigma));
    }
    @Test
    public void testGamma(){
        GammaFunction gammaFunction = new GammaFunction();
        System.out.println(gammaFunction.la_gamma(0.5));
    }
    @Test
    public void testLimitsOfS(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitGroup(datas,2);
        List<Double> rangeList = Calculate.calculateRange(data);
        List<Double> aveList = Calculate.calculateGroupAveOfXbar(data);
        double sigma  = Calculate.SbarSigmaWithoutConstant(data,aveList);
        double sigma1  = Calculate.SbarSigmaWithConstant(data,aveList);
        double sigma2  = Calculate.UnionSigmaWithConstant(data,aveList);
        double sigma3  = Calculate.UnionSigmaWithoutConstant(data,aveList);

        System.out.println(Calculate.LimitsOfSWithConstant(data,sigma1,3));   //Sbar使用无偏常量
        System.out.println(Calculate.LimitsOfSWithoutConstant(data,sigma,3));   //Sbar不使用无偏常量
        System.out.println(Calculate.LimitsOfSWithConstant(data,sigma2,3));     //合并标准差使用无偏常量
        System.out.println(Calculate.LimitsOfSWithoutConstant(data,sigma3,3));  //合并标准差不使用无偏常量
    }
    @Test
    public void testSplitMovingRangeGroup(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(datas,3);
        System.out.println(datas);
        for(int i = 0;i<data.size();i++){
            System.out.println(Arrays.toString(data.get(i)));
        }
        List<Double> rangeList = Calculate.calculateRange(data);
        System.out.println(rangeList);
    }

    @Test
    public void testCalculateMovingRange(){
//        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        String datas = "2.0529201014879344 1.0013360346958184 0.541643514416597 2.40961357205791 1.3895537464231988 3.396637994506791 -2.835872814205937 -1.330341387403733 2.388022538008722 -1.330341387403733 -0.1487527468233708 0.9277871345433224 -1.21691327304466 0.823206378224763 -3.25623972071717 1.044732701594226 -1.4429762981658847 -0.9900570443265221 -1.4429762981658847 -1.6698325268840224";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(datas,2);
        List<Double> rangeList = Calculate.calculateRange(data);
        System.out.println(rangeList);
        System.out.println(Calculate.movingRangeAverageAsSigma(rangeList,2));
        System.out.println(Calculate.movingRangeMedianAsSigma(rangeList,2));
    }
    @Test
    public void testSRMSSD(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(datas,2);
        List<Double> rangeList = Calculate.calculateRange(data);
        System.out.println(rangeList);
        System.out.println(Calculate.SRMSSDAsSigmaWithConstant(rangeList,20));
        System.out.println(Calculate.SRMSSDAsSigmaWithoutConstant(rangeList,20));
    }
    @Test
    public void testMRLimits(){
        String datas = "601.6 602.8 598.4 598.2 600.8 600.8 600.4 598.2 599.4 601.2 602.2 601.6 599.8 603.8 600.8 598.0 601.6 602.4 601.4 601.2";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(datas,2);
        List<Double> rangeList = Calculate.calculateRange(data);
        double sigma = Calculate.movingRangeAverageAsSigma(rangeList,2);
        System.out.println(Calculate.MRLimits(sigma,2,3));
    }
    @Test
    public void testResultDataOfP(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        System.out.println(Calculate.resultDataOfP(dataValue,sizeValue,3));
    }
    @Test
    public void testMsg(){
        List<Integer> errorPointIndex = new ArrayList<>();
        errorPointIndex.add(6);
        errorPointIndex.add(15);
        int k = 3;
        String msg ="检验1：{0}个点，距离中心线超过{1}个标准差。\n检验出下列点不合格：{2}";
        System.out.println(MessageFormat.format(msg,errorPointIndex.size(),k,errorPointIndex.toString()));
    }
    @Test
    public void testCriterion_1(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        ResultDataOfP result = Calculate.resultDataOfP(dataValue,sizeValue,3);
        System.out.println(Criterion.Criterion_1_2(result.getData_P(), result.getLcl_P(), result.getUcl_P(), 3).getMsg());
    }
    @Test
    public void testCriterion_2(){
        String dataValue = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50";
        List<Double> cl = new ArrayList<>();
        double cl_Xbar = Calculate.calculateDataAve(dataValue,50);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,2);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        for (int i = 0;i< 25;i++){
            cl.add(cl_Xbar);
        }
        System.out.println(groupAve_Xbar);
        System.out.println(cl);
        System.out.println(Criterion.Criterion_2(groupAve_Xbar,cl,9));

    }
    @Test
    public void testCriterion_3(){
        String dataValue = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50";
//        List<Double> data = new ArrayList<>();
        String[] s = dataValue.split(" ");
        double cl_Xbar = Calculate.calculateDataAve(dataValue,50);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,2);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
//        for (int i = 0;i< s.length;i++){
//            data.add(Double.valueOf(s[i]));
//        }
//        System.out.println(groupAve_Xbar);
//        System.out.println(cl);
        System.out.println(Criterion.Criterion_3(groupAve_Xbar,5));

    }

    @Test
    public void testCriterion_4(){
        String dataValue = "1 20 2 19 3 18 4 17 5 16 6 15 7 14 8 13 9 12 10 11";
        List<Double> data = new ArrayList<>();
        String[] s = dataValue.split(" ");
//        double cl_Xbar = Calculate.calculateDataAve(dataValue,50);
//        List<BigDecimal[]> data = Calculate.splitGroup(dataValue,2);
//        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        for (int i = 0;i< s.length;i++){
            data.add(Double.valueOf(s[i]));
        }
//        System.out.println(groupAve_Xbar);
//        System.out.println(cl);
        System.out.println(Criterion.Criterion_4(data,12));

    }
    @Test
    public void testCriterion_5(){
        String dataValue = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50";
//        String dataValue = "1 2 3 4 5 6 7 8 9 10";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(dataValue,2);
        String[] split = dataValue.split(" ");
        int dataSize = split.length;
        List<Double> SingleValueData = new ArrayList<>();
        for (int i = 0;i<dataSize;i++){
            SingleValueData.add(new BigDecimal(split[i]).doubleValue());
        }
        List<Double> movingRangeList = Calculate.calculateRange(data);
        double CL_SingleValue = Calculate.calculateDataAve(dataValue,dataSize);
        double sigma = Calculate.movingRangeAverageAsSigma(movingRangeList,2);
        List<Double> cl = new ArrayList<>();
        for (int i = 0;i< dataSize;i++){
            cl.add(CL_SingleValue);
        }
        System.out.println(cl);
        System.out.println(sigma);
        System.out.println(Criterion.Criterion_5(SingleValueData,cl,sigma,2));

    }

    @Test
    public void testCriterion_6(){
        String dataValue = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50";
//        String dataValue = "1 2 3 4 5 6 7 8 9 10";
        String[] s = dataValue.split(" ");
        double cl_Xbar = Calculate.calculateDataAve(dataValue, 50);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue, 2);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<Double> cl = new ArrayList<>();
        for (int i = 0;i< s.length;i++){
            cl.add(cl_Xbar);
        }
        List<Double> groupRange_R = Calculate.calculateRange(data);
        double standardDiviation = Calculate.RbarSigma(data,groupRange_R);
        System.out.println(Criterion.Criterion_6(groupAve_Xbar,cl,standardDiviation,3));

    }

    @Test
    public void testCriterion_7(){
        String dataValue = "1 20 2 19 3 18 4 17 5 16 6 15 7 14 8 13 9 12 10 11";
        List<BigDecimal[]> data = Calculate.splitMovingRangeGroup(dataValue,2);
        String[] split = dataValue.split(" ");
        int dataSize = split.length;
        List<Double> SingleValueData = new ArrayList<>();
        for (int i = 0;i<dataSize;i++){
            SingleValueData.add(new BigDecimal(split[i]).doubleValue());
        }
        List<Double> movingRangeList = Calculate.calculateRange(data);
        double CL_SingleValue = Calculate.calculateDataAve(dataValue,dataSize);
        double sigma = Calculate.movingRangeAverageAsSigma(movingRangeList,2);
        List<Double> cl = new ArrayList<>();
        for (int i = 0;i< dataSize;i++){
            cl.add(CL_SingleValue);
        }
        System.out.println(Criterion.Criterion_7(SingleValueData,cl,sigma,12));
    }
    @Test
    public void testCriterion_8() {
        String dataValue = "1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50";

        String[] s = dataValue.split(" ");
        double cl_Xbar = Calculate.calculateDataAve(dataValue, 50);
        List<BigDecimal[]> data = Calculate.splitGroup(dataValue, 2);
        List<Double> groupAve_Xbar = Calculate.calculateGroupAveOfXbar(data);
        List<Double> cl = new ArrayList<>();
        for (int i = 0;i< s.length;i++){
            cl.add(cl_Xbar);
        }
        List<Double> groupRange_R = Calculate.calculateRange(data);
        double standardDiviation = Calculate.RbarSigma(data,groupRange_R);
//        System.out.println(groupAve_Xbar);
//        System.out.println(cl);
        System.out.println(Criterion.Criterion_8(groupAve_Xbar, cl,standardDiviation,6));
    }
    @Test
    public void testResultDataOfLaneyP(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        System.out.println(Calculate.resultDataOfLaneyP(dataValue,sizeValue,3));
    }
    @Test
    public void testresultDataOfNP(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        System.out.println(Calculate.resultDataOfNP(dataValue,sizeValue,3));
    }
    @Test
    public void testresultDataOfU(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        System.out.println(Calculate.resultDataOfU(dataValue,sizeValue,3));
    }
    @Test
    public void testResultDataOfLaneyU(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        String sizeValue = "690 680 680 700 700 650 650 700 600 700 750 650 700 700 700 650 700 700 700 700";
        System.out.println(Calculate.resultDataOfLaneyU(dataValue,sizeValue,3));
    }
    @Test
    public void testResultDataOfC(){
        String dataValue = "620 602 598 632 623 596 543 599 543 599 653 575 600 618 582 576 598 602 598 596";
        System.out.println(Calculate.resultDataOfC(dataValue,3));
    }

}
