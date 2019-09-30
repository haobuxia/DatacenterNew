package com.tianyi.datacenter.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
public class upRate {
    //todo写错了
    public  List upRate(List<Map<String, Object>> result){
        List list = new ArrayList();
        for (int i = 0; i < result.size(); i++) {
            Double before = (Double) result.get(i).get("avg");
            Double after = 0.0;
            if (i<=result.size()-2) {
               after = (Double) result.get(i+1).get("avg");
            }else {
                break;
            }
            Double i1 = before;
            if (i1==0.0){
                list.add(0);
                continue;
            }
            Double i2 = after;
            double   f   =  (i2-i1)/i1;
            BigDecimal b   =   new   BigDecimal(f);
            double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
            list.add(f1);
        }
        return list;
    }
    public  List upRateNoBefore(List<Map<String, Object>> result){
        List list = new ArrayList();
        double   nothing   =  0.000;
        BigDecimal temp   =   new   BigDecimal(nothing);
        double   temps   =   temp.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        list.add(0,temps);
        for (int i = 0; i < result.size(); i++) {
            Double before = (Double) result.get(i).get("avg");
            Double after = 0.0;
            if (i<=result.size()-2) {
               after = (Double) result.get(i+1).get("avg");
            }else {
                break;
            }
            Double i1 = before;
            if (i1==0.0){
                list.add(0);
                continue;
            }
            Double i2 = after;
            double   f   =  (i2-i1)/i1;
            BigDecimal b   =   new   BigDecimal(f);
            double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
            list.add(f1);
        }
        return list;
    }
}
