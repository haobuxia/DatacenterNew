package com.tianyi.datacenter.resource.entity;

import java.time.LocalDateTime;

/**
 * 数据对象
 *
 * @author wenxinyan
 * @version 0.1
 */
public class DataObject {
    private int id;
    private int typeId;
    private String name;
    private String type;
    private String defined;
    private String isDic;
    private String description;
    private String oldDefined;// 不写入数据库，临时用

    private int pltCreator;
    private LocalDateTime pltCreatetime;
    private int pltLastmodifier;
    private LocalDateTime pltLastmodifytime;

    public int getPltCreator() {
        return pltCreator;
    }

    public void setPltCreator(int pltCreator) {
        this.pltCreator = pltCreator;
    }

    public LocalDateTime getPltCreatetime() {
        return pltCreatetime;
    }

    public void setPltCreatetime(LocalDateTime pltCreatetime) {
        this.pltCreatetime = pltCreatetime;
    }

    public int getPltLastmodifier() {
        return pltLastmodifier;
    }

    public void setPltLastmodifier(int pltLastmodifier) {
        this.pltLastmodifier = pltLastmodifier;
    }

    public LocalDateTime getPltLastmodifytime() {
        return pltLastmodifytime;
    }

    public void setPltLastmodifytime(LocalDateTime pltLastmodifytime) {
        this.pltLastmodifytime = pltLastmodifytime;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getOldDefined() {
        return oldDefined;
    }

    public void setOldDefined(String oldDefined) {
        this.oldDefined = oldDefined;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefined() {
        return defined;
    }

    public void setDefined(String defined) {
        this.defined = defined;
    }

    public String getIsDic() {
        return isDic;
    }

    public void setIsDic(String isDic) {
        this.isDic = isDic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
