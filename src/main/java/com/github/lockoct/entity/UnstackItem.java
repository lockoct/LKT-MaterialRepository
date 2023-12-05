package com.github.lockoct.entity;

import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.dao.interceptor.annotation.PrevUpdate;

import java.util.Date;

@Table("mr_unstack_item")
public class UnstackItem {
    @Name
    @Comment("主键")
    @Prev(els = @EL("uuid(32)"))
    @ColDefine(type = ColType.VARCHAR, width = 36)
    private String id;

    @Column
    @Comment("物品归属Id")
    @ColDefine(type = ColType.VARCHAR, width = 36)
    private String itemId;

    @Column
    @Comment("nbt标签")
    @ColDefine(type = ColType.TEXT)
    private String nbt;

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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String nbt) {
        this.nbt = nbt;
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
