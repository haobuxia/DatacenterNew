package com.tianyi.datacenter.inspect.entity;

import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import com.github.liaochong.myexcel.core.annotation.ExcelTable;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liaochong
 * @version 1.0
 * 工位检查项配置导出
 */
@Data
@ExcelTable(sheetName = "工位检查项配置", rowAccessWindowSize = 100, useFieldNameAsTitle = true)
public class StationCheck implements Serializable {
    /**
     * 机种
     */
    @ExcelColumn(order = 0, title = "机种", index = 0)
    private String modelName;
    /**
     * 机型
     */
    @ExcelColumn(order = 1, title = "机型", index = 1)
    private String typeName;
    /**
     * 工位
     */
    @ExcelColumn(order = 2, title = "工位", index = 2)
    private String stationName;

    /**
     * 检查项编号
     */
    @ExcelColumn(order = 3, title = "检查项类别编号", index = 3)
    private String checktypeNum;
    /**
     * 检查项名称
     */
    @ExcelColumn(order = 4, title = "检查项类别名称", index = 4)
    private String checktypeName;

    /**
     * 显示顺序
     */
    @ExcelColumn(order = 5, title = "显示顺序", index = 5)
    private Double checkOrder;

}
