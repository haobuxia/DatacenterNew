package com.tianyi.datacenter.inspect.entity;

import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelTable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Data
@ExcelTable(sheetName = "配车管理", rowAccessWindowSize = 100, useFieldNameAsTitle = true)
public class CarImport {
    /**
     * 公司
     */
    @ExcelColumn(order = 0, title = "公司", index = 0)
    private String companyName;
    @ExcelColumn(order = 1, title = "工位", index = 1)
    private String stationName;

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

    @ExcelColumn(order = 4, title = "头盔编号", index = 4)
    private String helmetNos;
    /**
     * 巡检项类别名称
     */
    @ExcelColumn(order = 5, title = "负责人", index = 5)
    private String userNames;

    /**
     * 类别语音提示
     */
    @ExcelColumn(order = 6, title = "结束时间",dateFormatPattern = "yyyy-MM-dd HH:mm:ss", index = 6)
    private LocalDateTime endTime;

}
