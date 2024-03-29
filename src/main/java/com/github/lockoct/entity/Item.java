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
    @Comment("是否可堆叠(0: 不可堆叠, 1: 可堆叠)")
    @Default("1")
    @ColDefine(type = ColType.BOOLEAN, notNull = true)
    private boolean stack;

    @Column
    @Comment("nbt标签")
    @ColDefine(type = ColType.TEXT)
    private String nbt;

    @Column
    @Comment("nbtMd5校验值")
    @ColDefine(type = ColType.VARCHAR, width = 45)
    private String nbtMd5;

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

    public boolean isStack() {
        return stack;
    }

    public void setStack(boolean stack) {
        this.stack = stack;
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String nbt) {
        this.nbt = nbt;
    }

    public String getNbtMd5() {
        return nbtMd5;
    }

    public void setNbtMd5(String nbtMd5) {
        this.nbtMd5 = nbtMd5;
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
