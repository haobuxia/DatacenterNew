package com.tianyi.datacenter.common.util;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
public class TimeUtil {
    private static final int minute = 60;
    private static final String decimal = "0.00";
    private static final int bs = 0;
    private static final int be = 10;
    private static final int fs = 12;
    private static final int fe = 19;

    public double getMinute(String beginTime,String finishTime){
        String bstart = beginTime.substring(bs, be);
        String bend = beginTime.substring(fs, fe);

        String fstart = finishTime.substring(bs, be);
        String fend = finishTime.substring(fs, fe);

        DateTime begin = new DateTime(bstart+"T"+bend);
        DateTime finish = new DateTime(fstart+"T"+fend);

        Duration duration = new Duration(begin, finish);
        long standardSeconds = duration.getStandardSeconds();

        //保留?位小数
        DecimalFormat df=new DecimalFormat(decimal);//设置保留位数
        String format = df.format((float) standardSeconds / minute);
        return Double.parseDouble(format);
    }
    public boolean compare(String beginTime,String finishTime){
        int i = beginTime.compareTo(finishTime);
        if (i>0 || i==0){
            return true;
        }else {
            return false;
        }
    }

    public static List<Date> getMouthGroup(String startTime, String endTime) throws Exception {
        Date parse = new SimpleDateFormat("yyyy-MM").parse(startTime);
        Calendar cc = Calendar.getInstance();
        cc.setTime(parse);
//        cc.add(Calendar.MONTH,-1);
        Date parse2 = new SimpleDateFormat("yyyy-MM").parse(endTime);
        Calendar dd = Calendar.getInstance();
        dd.setTime(cc.getTime());
        Calendar ee = Calendar.getInstance();
        ee.setTime(parse2);
        ee.add(Calendar.MONTH,1);
        List<Date> date = new ArrayList<>();
        while (dd.getTime().before(ee.getTime())||dd.getTime().equals(ee.getTime())){
            date.add(dd.getTime());
            dd.add(Calendar.MONTH,1);
        }
        return date;
    }
    public static List<Date> getDayGroup(String month)  {
        List<Date> date = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(month);

        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance.setTime(parse);
        instance2.setTime(parse);
        instance2.add(Calendar.MONTH,1);
        while (instance.before(instance2)){
            String format = simpleDateFormat2.format(instance.getTime());
            date.add(simpleDateFormat2.parse(format));
            instance.add(Calendar.DAY_OF_MONTH,1);

        }
        for (int i = 0; i < date.size(); i++) {
            if (i==date.size()-1){
                instance.setTime(date.get(i));
                String s = simpleDateFormat3.format(instance.getTime()) + " 23:59:59";
                date.remove(i);
                date.add(simpleDateFormat2.parse(s));
            }
        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date parseLong(String time) throws ParseException {
        Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        return parse;
    }
    public static String parseTime(String time) throws ParseException {
        Date parse = new SimpleDateFormat("yyyy-MM").parse(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(parse);

        return format;
    }
    public static String  parse(Date dateTime)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String parse = simpleDateFormat.format(dateTime);
        String date = parse.toString();
        StringBuffer dateSb = new StringBuffer(date);
        String year = dateSb.substring(0, 4);
        String mouth = dateSb.substring(5, 7);
        StringBuffer datefinal = new StringBuffer();
        datefinal.append(year).append("年").append(mouth).append("月");
        String s = datefinal.toString();
        return s;
    }
    public static String  parseDay(Date dateTime)  {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String parse = simpleDateFormat.format(dateTime);
        String date = parse.toString();
        StringBuffer dateSb = new StringBuffer(date);
        String month = dateSb.substring(5, 7);
        String day = dateSb.substring(8, 10);
        StringBuffer datefinal = new StringBuffer();
        datefinal.append(month).append("/").append(day);
        String result = datefinal.toString();
        return result;
    }
//获取视频图像识别最开始识别时间
    public static Integer  getImgocrStartTime(String img)  {
        int start = img.indexOf("img")+3;
        String imgs = img.substring(start, start+5);
        int seconds = Integer.parseInt(imgs);
        if (seconds%2!=0){
            seconds++;
        }
        seconds = seconds/2;
        /*SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String format = formatter.format(seconds*1000);
        String substring = format.substring(3, format.length());
        System.out.println(substring);*/
        return seconds;
    }
    public static String  getPercentage(int num1,int num2)  {

        if (num2==0){
            return 0.00+"%";
        }
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result+"%";
    }
    public static void main(String[] args) {

        int num1 = 100;

        int num2 = 100;

        // 创建一个数值格式化对象

        NumberFormat numberFormat = NumberFormat.getInstance();

        // 设置精确到小数点后2位

        numberFormat.setMaximumFractionDigits(2);

        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        System.out.println(Double.parseDouble(result));
        System.out.println("num1和num2的百分比为:" + result + "%");

        double d = 1.23;
        double d1 = 1.22;
        System.out.println((d+d1)/2);

    }
}
