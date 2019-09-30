package com.tianyi.datacenter.inspect.entity;

import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelTable;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liulele ${Date}
 * @version 0.1
 * 检查项批量处理导入
 **/
@Data
@ExcelTable(sheetName = "巡检项批量处理", rowAccessWindowSize = 100, useFieldNameAsTitle = true)
public class CheckitemImport implements Serializable {

    /**
     * 公司
     */
    @ExcelColumn(order = 0, title = "公司", index = 0)
    private String companyName;

    /**
     * 机种
     */
    @ExcelColumn(order = 1, title = "资产种类", index = 1)
    private String typeName;
    /**
     * 型号
     */
    @ExcelColumn(order = 2, title = "型号", index = 2)
    private String modelName;
    /**
     * 机号
     */
    @ExcelColumn(order = 3, title = "机号", index = 3)
    private String deviceNo;
    /**
     * 工位
     */
    @ExcelColumn(order = 4, title = "工位", index = 4)
    private String stationName;
    @ExcelColumn(order = 5, title = "巡检项类别编号", index = 5)
    private String checktypeNum;
    /**
     * 巡检项类别名称
     */
    @ExcelColumn(order = 6, title = "巡检项类别名称", index = 6)
    private String checktypeName;

    /**
     * 类别语音提示
     */
    @ExcelColumn(order = 7, title = "类别语音提示", index = 7)
    private String voice;
    /**
     * 类别显示顺序
     */
    @ExcelColumn(order = 8, title = "类别显示顺序", index = 8)
    private Double checkOrderType;
    /**
     * 巡检项编号
     */
    @ExcelColumn(order = 9, title = "巡检项编号", index = 9)
    private String checkitemNum;
    /**
     * 巡检项名称
     */
    @ExcelColumn(order = 10, title = "巡检项名称", index = 10)
    private String checkitemName;
    /**
     * 记录方式
     */
    @ExcelColumn(order = 11, title = "记录方式", index = 11)
    private String recodingModel;
    /**
     * 判断标准
     */
    @ExcelColumn(order = 12, title = "判断标准", index = 12)
    private String standard;
    /**
     * 检查项语音提示
     */
    @ExcelColumn(order = 13, title = "检查项语音提示", index = 13)
    private String voiceInfo;
    /**
     * 作业指导
     */
    @ExcelColumn(order = 14, title = "作业指导", index =14)
    private String guidance;
    /**
     * 显示顺序
     */
    @ExcelColumn(order = 15, title = "显示顺序", index = 15)
    private String checkOrderItem;
}
