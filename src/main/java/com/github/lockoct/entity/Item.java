package com.github.lockoct.entity;

import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.dao.interceptor.annotation.PrevUpdate;

import java.util.Date;

@Table("mr_item")
public class Item {
    @Name
    @Comment("主键")
    @Prev(els=@EL("uuid(32)"))
    @ColDefine(type = ColType.VARCHAR, width = 36)
    private String id;

    @Column
    @Comment("物品类型")
    @ColDefine(type = ColType.VARCHAR, width = 45)
    private String type;

    @Column
    @Comment("物品数量")
    @ColDefine(type = ColType.INT)
    private int amount;

    @Column
    @Comment("创建时间")
    @PrevInsert(now = true)
    @ColDefine(type = ColType.DATETIME)
    private Date createTime;

    @Column
    @Comment("修改时间")
    @PrevInsert(now = true)
    @PrevUpdate(now = true)
    @ColDefine(type = ColType.DATETIME)
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
