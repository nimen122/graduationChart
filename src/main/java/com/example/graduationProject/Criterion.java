package com.example.graduationProject;

import com.example.graduationProject.entity.ResultOfCriterion;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Criterion {
    /*
    检验一：距离中心线大于 K 个标准差
     */
    public static ResultOfCriterion Criterion_1(List<Double> data,List<Double> cl,Double standardDeviation,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        String msg ="检验1：1个点，距离中心线超过{0}个标准差。\n检验出下列点不合格：{1}";
        for(int i = 0;i<data.size();i++){
            if(data.get(i)> cl.get(i)+k*standardDeviation||data.get(i)<cl.get(i)-k*standardDeviation){
                result.setFit(true);
                errorPointIndex.add(i+1);
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    //适合于没有方差的控制图，如P控制图
    public static ResultOfCriterion Criterion_1_2(List<Double> data,List<Double> lcl,List<Double> ucl,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        String msg ="检验1：1个点，距离中心线超过{0}个标准差。\n检验出下列点不合格：{1}";
        for(int i = 0;i<data.size();i++){
            if(data.get(i)> ucl.get(i)||data.get(i)<lcl.get(i)){
                result.setFit(true);
                errorPointIndex.add(i+1);
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    //MR控制图专用
    public static ResultOfCriterion Criterion_1_MR(List<Double> data,List<Double> lcl,List<Double> ucl,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        String msg ="检验1：1个点，距离中心线超过{0}个标准差。\n检验出下列点不合格：{1}";
        for(int i = 0;i<data.size();i++){
            if(data.get(i)> ucl.get(i)||data.get(i)<lcl.get(i)){
                result.setFit(true);
                errorPointIndex.add(i+2);
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验二：连续 K 点在中心线同一侧
     */
    public static ResultOfCriterion Criterion_2(List<Double> data,List<Double> cl,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> twoAbnormalIndex = new ArrayList<>();
        String msg ="检验2：连续{0}点在中心线同一侧\n检验出下列点不合格：{1}";
        int twoDirection = 1;       // 1：中心线上方。-1：中心线下方
        for(int i = 0;i<data.size();i++){
            if(i == 0){
                twoDirection = data.get(0).compareTo(cl.get(0)) > 0 ? 1: -1;
                twoAbnormalIndex.add(i);
            }else {
                if (data.get(i) > cl.get(i) && twoDirection > 0 || data.get(i) < cl.get(i) && twoDirection < 0){
                    twoAbnormalIndex.add(i);
                    if (twoAbnormalIndex.size() >= k){
                        result.setFit(true);
                        errorPointIndex.add(i+1);
                    }
                }else {
                    twoDirection = data.get(i).compareTo(cl.get(i)) > 0 ? 1: -1;
                    twoAbnormalIndex.clear();
                    twoAbnormalIndex.add(i+1);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    //MR控制图专用
    public static ResultOfCriterion Criterion_2_MR(List<Double> data,List<Double> cl,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> twoAbnormalIndex = new ArrayList<>();
        String msg ="检验2：连续{0}点在中心线同一侧\n检验出下列点不合格：{1}";
        int twoDirection = 1;       // 1：中心线上方。-1：中心线下方
        for(int i = 0;i<data.size();i++){
            if(i == 0){
                twoDirection = data.get(0) > cl.get(0) ? 1: -1;
                twoAbnormalIndex.add(i);
            }else {
                if (data.get(i) > cl.get(i) && twoDirection > 0 || data.get(i) < cl.get(i) && twoDirection < 0){
                    twoAbnormalIndex.add(i);
                    if (twoAbnormalIndex.size() >= k){
                        result.setFit(true);
                        errorPointIndex.add(i+2);
                    }
                }else {
                    twoDirection = data.get(i).compareTo(cl.get(i)) > 0 ? 1: -1;
                    twoAbnormalIndex.clear();
                    twoAbnormalIndex.add(i+2);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验三：连续 K 个点，全部递增或全部递减
     */
    public static ResultOfCriterion Criterion_3(List<Double> data,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> threeAbnormalIndex = new ArrayList<>();
        String msg ="检验3：行内连续{0}点，全部递增或全部递减\n检验出下列点不合格：{1}";
        int threeDirection = 1;     // 1：上升。-1：下降
        for(int i = 0;i<data.size();i++){
            if(i<2){
                threeAbnormalIndex.add(i);  // 判断方向至少需要两个点，初始值默认要存两个，从第三个开始判断是否递增递减；
                threeDirection = data.get(1).compareTo(data.get(0)) > 0 ? 1 : -1;
            } else {
                // 跟上个数据同个趋势，累加
                if ((data.get(i).compareTo(data.get(i-1)) > 0 && threeDirection > 0)  || (data.get(i).compareTo(data.get(i-1)) < 0 && threeDirection < 0)) {
                    threeAbnormalIndex.add(i);
                    if(threeAbnormalIndex.size() > k) {
                        result.setFit(true);
                        errorPointIndex.add(i+1);
                    }
                }
                // 不符合报警条件，归零重新计算
                else {
                    threeDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;
                    threeAbnormalIndex.clear();
                    threeAbnormalIndex.add(i-1);
                    threeAbnormalIndex.add(i);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    public static ResultOfCriterion Criterion_3_MR(List<Double> data,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> threeAbnormalIndex = new ArrayList<>();
        String msg ="检验3：行内连续{0}点，全部递增或全部递减\n检验出下列点不合格：{1}";
        int threeDirection = 1;     // 1：上升。-1：下降
        for(int i = 0;i<data.size();i++){
            if(i<2){
                threeAbnormalIndex.add(i);  // 判断方向至少需要两个点，初始值默认要存两个，从第三个开始判断是否递增递减；
                threeDirection = data.get(1).compareTo(data.get(0)) > 0 ? 1 : -1;
            } else {
                // 跟上个数据同个趋势，累加
                if ((data.get(i).compareTo(data.get(i-1)) > 0 && threeDirection > 0)  || (data.get(i).compareTo(data.get(i-1)) < 0 && threeDirection < 0)) {
                    threeAbnormalIndex.add(i);
                    if(threeAbnormalIndex.size() > k) {
                        result.setFit(true);
                        errorPointIndex.add(i+2);
                    }
                }
                // 不符合报警条件，归零重新计算
                else {
                    threeDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;
                    threeAbnormalIndex.clear();
                    threeAbnormalIndex.add(i-1);
                    threeAbnormalIndex.add(i);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验四：连续 K 个点，上下交错
     */
    public static ResultOfCriterion Criterion_4(List<Double> data,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> fourAbnormalIndex = new ArrayList<>();
        String msg ="检验4：行内连续{0}点上下交错\n检验出下列点不合格：{1}";
        int fourDirection = 1;       // 1：上升。-1：下降
        for(int i = 0;i<data.size();i++){
            if(i<2){
                fourAbnormalIndex.add(i);  // 判断方向至少需要两个点，初始值默认要存两个，从第三个开始判断是否递增递减；
            } else {
                // 跟上个数据同个趋势，累加
                if ((data.get(i).compareTo(data.get(i-1)) > 0 && fourDirection < 0)  || (data.get(i).compareTo(data.get(i-1)) < 0 && fourDirection > 0)) {
                    fourDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;  // 方向重置
                    fourAbnormalIndex.add(i);
                    if(fourAbnormalIndex.size() > k) {
                        result.setFit(true);
                        errorPointIndex.add(i+1);
                    }
                }
                // 不符合报警条件，归零重新计算
                else {
                    fourDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;
                    fourAbnormalIndex.clear();
                    fourAbnormalIndex.add(i-1);
                    fourAbnormalIndex.add(i);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    public static ResultOfCriterion Criterion_4_MR(List<Double> data,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> fourAbnormalIndex = new ArrayList<>();
        String msg ="检验4：行内连续{0}点上下交错\n检验出下列点不合格：{1}";
        int fourDirection = 1;       // 1：上升。-1：下降
        for(int i = 0;i<data.size();i++){
            if(i<2){
                fourAbnormalIndex.add(i);  // 判断方向至少需要两个点，初始值默认要存两个，从第三个开始判断是否递增递减；
            } else {
                // 跟上个数据同个趋势，累加
                if ((data.get(i).compareTo(data.get(i-1)) > 0 && fourDirection < 0)  || (data.get(i).compareTo(data.get(i-1)) < 0 && fourDirection > 0)) {
                    fourDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;  // 方向重置
                    fourAbnormalIndex.add(i);
                    if(fourAbnormalIndex.size() > k) {
                        result.setFit(true);
                        errorPointIndex.add(i+2);
                    }
                }
                // 不符合报警条件，归零重新计算
                else {
                    fourDirection = data.get(i).compareTo(data.get(i-1)) > 0 ? 1 : -1;
                    fourAbnormalIndex.clear();
                    fourAbnormalIndex.add(i-1);
                    fourAbnormalIndex.add(i);
                }
            }
        }
        if(result.isFit()){
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验五：K+1 个点中有 K 个点，距离中心线（同侧）大于 2 个标准差
     */
    public static ResultOfCriterion Criterion_5(List<Double> data,List<Double> cl,Double standardDeviation,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        String msg ="检验5：{0}点中有{1}点，距离中心线超过2个标准差（在中心线同一侧）\n检验出下列点不合格：{2}";
        for(int i = 0;i<data.size();i++){
            List<Integer> fiveUpAbnormalIndex = new ArrayList<>(); // 中心线上方累计异常数据下标
            List<Integer> fiveDownAbnormalIndex = new ArrayList<>(); // 中心线下方累计异常数据下标
            // 从第K个值开始统计，往前遍历是否有K-1个值符合条件
            if (i >= k - 1) {
                for (int j = 0; j < k; j++) {
                    if (data.get(i-j) > cl.get(i-j)+2*standardDeviation && data.get(i) > cl.get(i)+2*standardDeviation) {
                        fiveUpAbnormalIndex.add(i+1);
                    }
                    if (data.get(i-j) < cl.get(i-j)-2*standardDeviation && data.get(i) < cl.get(i)-2*standardDeviation) {
                        fiveDownAbnormalIndex.add(i+1);
                    }
                }
                if (fiveUpAbnormalIndex.size() > k-1) {
                    result.setFit(true);
                    errorPointIndex.addAll(fiveUpAbnormalIndex);
                }
                if (fiveDownAbnormalIndex.size() > k-1) {
                    result.setFit(true);
                    errorPointIndex.addAll(fiveDownAbnormalIndex);
                }
            }
        }
        if(result.isFit()){
            errorPointIndex = errorPointIndex.stream().distinct().collect(Collectors.toList());
            result.setMsg(MessageFormat.format(msg,k+1,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验六：K+1 个点中有 K 个点，距离中心线（同侧）大于 1 个标准差
     */
    public static ResultOfCriterion Criterion_6(List<Double> data,List<Double> cl,Double standardDeviation,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();

        String msg ="检验6：{0}点中有{1}点，距离中心线超过1个标准差（在中心线同一侧）\n检验出下列点不合格：{2}";
        for(int i = 0;i<data.size();i++){
            List<Integer> fiveUpAbnormalIndex = new ArrayList<>(); // 中心线上方累计异常数据下标
            List<Integer> fiveDownAbnormalIndex = new ArrayList<>(); // 中心线下方累计异常数据下标
            // 从第K个值开始统计，往前遍历是否有K-1个值符合条件
            if (i >= k - 1) {
                for (int j = 0; j < k; j++) {
                    if (data.get(i-j) > cl.get(i-j) + standardDeviation && data.get(i) > cl.get(i)+standardDeviation) {
                        fiveUpAbnormalIndex.add(i+1);
                    }
                    if (data.get(i-j) < cl.get(i-j) - standardDeviation && data.get(i) < cl.get(i)-standardDeviation) {
                        fiveDownAbnormalIndex.add(i+1);
                    }
                }
                if (fiveUpAbnormalIndex.size() > k-1) {
                    result.setFit(true);
                    errorPointIndex.addAll(fiveUpAbnormalIndex);
                }
                if (fiveDownAbnormalIndex.size() > k-1) {
                    result.setFit(true);
                    errorPointIndex.addAll(fiveDownAbnormalIndex);
                }
            }
        }
        if(result.isFit()){
            errorPointIndex = errorPointIndex.stream().distinct().collect(Collectors.toList());
            result.setMsg(MessageFormat.format(msg,k+1,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验七：连续 K 个点，距离中心线（任一侧）1 个标准差以内
     */
    public static ResultOfCriterion Criterion_7(List<Double> data,List<Double> cl,Double standardDeviation,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> sevenAbnormalIndex = new ArrayList<>();

        String msg ="检验7：连续{0}点在中心线1个标准差以内（中心线的上方和下方）\n检验出下列点不合格：{1}";
        for(int i = 0;i<data.size();i++){
            if (data.get(i) < cl.get(i)+standardDeviation && data.get(i) > cl.get(i)-standardDeviation) {
                sevenAbnormalIndex.add(i);
                if(sevenAbnormalIndex.size() >= k) {
                    result.setFit(true);
                    errorPointIndex.add(i+1);
                }
            }
            // 不符合报警条件，归零重新计算
            else {
                sevenAbnormalIndex.clear();
            }

        }
        if(result.isFit()){
            errorPointIndex = errorPointIndex.stream().distinct().collect(Collectors.toList());
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    /*
    检验八：连续 K 个点，距离中心线（任一侧）大于 1 个标准差
     */
    public static ResultOfCriterion Criterion_8(List<Double> data,List<Double> cl,Double standardDeviation,int k){
        ResultOfCriterion result = new ResultOfCriterion();
        List<Integer> errorPointIndex = new ArrayList<>();
        List<Integer> eightAbnormalIndex = new ArrayList<>();

        String msg ="检验8：行内连续{0}点距离中心线超过1个标准差（中心线的上方和下方）\n检验出下列点不合格：{1}";
        for(int i = 0;i<data.size();i++){
            if (data.get(i) > cl.get(i)+standardDeviation || data.get(i) < cl.get(i)-standardDeviation) {
                eightAbnormalIndex.add(i);
                if(eightAbnormalIndex.size() >= k) {
                    result.setFit(true);
                    errorPointIndex.add(i+1);
                }
            }
            // 不符合报警条件，归零重新计算
            else {
                eightAbnormalIndex.clear();
            }

        }
        if(result.isFit()){
            errorPointIndex = errorPointIndex.stream().distinct().collect(Collectors.toList());
            result.setMsg(MessageFormat.format(msg,k,errorPointIndex));
            result.setErrorPointIndex(errorPointIndex);
        }
        return result;
    }

    public static List<ResultOfCriterion> ResultOfCriterion(List<Double> data,List<Double> cl,Double standardDeviation,String criterion,List<Integer> criterionData){
        List<ResultOfCriterion> resultOfCriterionList =new ArrayList<>();
        if (criterion.charAt(0) == '1'){
            ResultOfCriterion resultOfCriterion_1 = Criterion.Criterion_1(data,cl,standardDeviation,criterionData.get(0));
            resultOfCriterionList.add(resultOfCriterion_1);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(1) == '1'){
            ResultOfCriterion resultOfCriterion_2 = Criterion.Criterion_2(data,cl,criterionData.get(1));
            resultOfCriterionList.add(resultOfCriterion_2);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(2) == '1'){
            ResultOfCriterion resultOfCriterion_3 = Criterion.Criterion_3(data,criterionData.get(2));
            resultOfCriterionList.add(resultOfCriterion_3);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(3) == '1'){
            ResultOfCriterion resultOfCriterion_4 = Criterion.Criterion_4(data,criterionData.get(3));
            resultOfCriterionList.add(resultOfCriterion_4);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(4) == '1'){
            ResultOfCriterion resultOfCriterion_5 = Criterion.Criterion_5(data,cl,standardDeviation,criterionData.get(4));
            resultOfCriterionList.add(resultOfCriterion_5);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(5) == '1'){
            ResultOfCriterion resultOfCriterion_6 = Criterion.Criterion_6(data,cl,standardDeviation,criterionData.get(5));
            resultOfCriterionList.add(resultOfCriterion_6);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(6) == '1'){
            ResultOfCriterion resultOfCriterion_7 = Criterion.Criterion_7(data,cl,standardDeviation,criterionData.get(6));
            resultOfCriterionList.add(resultOfCriterion_7);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(7) == '1'){
            ResultOfCriterion resultOfCriterion_8 = Criterion.Criterion_8(data,cl,standardDeviation,criterionData.get(7));
            resultOfCriterionList.add(resultOfCriterion_8);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        return resultOfCriterionList;
    }

    public static List<ResultOfCriterion> ResultOfCriterion2(List<Double> data,List<Double> cl,List<Double> lcl,List<Double> ucl,String criterion,List<Integer> criterionData){
        List<ResultOfCriterion> resultOfCriterionList =new ArrayList<>();
        if (criterion.charAt(0) == '1'){
            ResultOfCriterion resultOfCriterion_1 = Criterion.Criterion_1_2(data,lcl,ucl,criterionData.get(0));
            resultOfCriterionList.add(resultOfCriterion_1);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(1) == '1'){
            ResultOfCriterion resultOfCriterion_2 = Criterion.Criterion_2(data,cl,criterionData.get(1));
            resultOfCriterionList.add(resultOfCriterion_2);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(2) == '1'){
            ResultOfCriterion resultOfCriterion_3 = Criterion.Criterion_3(data,criterionData.get(2));
            resultOfCriterionList.add(resultOfCriterion_3);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(3) == '1'){
            ResultOfCriterion resultOfCriterion_4 = Criterion.Criterion_4(data,criterionData.get(3));
            resultOfCriterionList.add(resultOfCriterion_4);
        }else resultOfCriterionList.add(new ResultOfCriterion());

        return resultOfCriterionList;
    }

    public static List<ResultOfCriterion> ResultOfCriterionOfMR(List<Double> data,List<Double> cl,List<Double> lcl,List<Double> ucl,String criterion,List<Integer> criterionData){
        List<ResultOfCriterion> resultOfCriterionList =new ArrayList<>();
        if (criterion.charAt(0) == '1'){
            ResultOfCriterion resultOfCriterion_1 = Criterion.Criterion_1_MR(data,lcl,ucl,criterionData.get(0));
            resultOfCriterionList.add(resultOfCriterion_1);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(1) == '1'){
            ResultOfCriterion resultOfCriterion_2 = Criterion.Criterion_2_MR(data,cl,criterionData.get(1));
            resultOfCriterionList.add(resultOfCriterion_2);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(2) == '1'){
            ResultOfCriterion resultOfCriterion_3 = Criterion.Criterion_3_MR(data,criterionData.get(2));
            resultOfCriterionList.add(resultOfCriterion_3);
        }else resultOfCriterionList.add(new ResultOfCriterion());
        if (criterion.charAt(3) == '1'){
            ResultOfCriterion resultOfCriterion_4 = Criterion.Criterion_4_MR(data,criterionData.get(3));
            resultOfCriterionList.add(resultOfCriterion_4);
        }else resultOfCriterionList.add(new ResultOfCriterion());

        return resultOfCriterionList;
    }
}
