package com.tianyi.datacenter.inspect.entity;

import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelTable;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Data
@ExcelTable(sheetName = "检查项导出", rowAccessWindowSize = 100, useFieldNameAsTitle = true)
public class CheckitemExport implements Serializable {


//    @ExcelColumn(order = 0, title = "机种", index = 0)
//    private String modelName;
    /**
     * 机型
     */
    @ExcelColumn(order = 0, title = "机型", index = 0)
    private String modelName;

    /**
     * 检查项编号
     */
    @ExcelColumn(order = 1, title = "检查项类别编号", index = 1)
    private String checktypeNum;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 2, title = "检查项类别名称", index = 2)
    private String checktypeName;
    @ExcelColumn(order = 3, title = "类别语音提示", index = 3)
    private String voice;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 4, title = "类别显示顺序", index = 4)
    private Double checkTypeOrder;
    @ExcelColumn(order = 5, title = "检查项编号", index = 5)
    private String checkitemNum;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 6, title = "检查项名称", index = 6)
    private String checkitemName;
    /**
     * 类别语音提示  voice
     * 类别显示顺序 checkOrder
     * 检查项编号  checkitemNum
     * 检查项名称 checkitemName
     * 记录方式  recodingModelId
     * 判断标准
     * 解决方法
     * 判定方法

     */
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 7, title = "记录方式", index = 7)
    private String recodingModel;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 8, title = "拍照时间间隔(s)", index = 8)
    private Integer time;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 9, title = "判断标准", index = 9)
    private String standard;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 10, title = "解决方法", index = 10)
    private String solveMethod;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 11, title = "判定方法", index = 11)
    private String decisionMethod;

    /**
     * * 结果类型
     * * 结果区间
     * * 结果区间 大
     * * 检查项语音提示
     * * 显示顺序
     */
    @ExcelColumn(order = 12, title = "结果类型", index = 12)
    private String resultType;

    @ExcelColumn(order = 13, title = "结果区间(小)", index = 13)
    private String minResult;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 14, title = "结果区间(大)", index =14)
    private String maxResult;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 15, title = "检查项语音提示", index = 15)
    private String voiceInfo;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 16, title = "作业指导", index = 16)
    private String guidance;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 17, title = "显示顺序", index = 17)
    private Double checkOrder;


}
