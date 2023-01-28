package com.github.lockoct.entity;

import org.nutz.dao.entity.annotation.*;

@Table("mr_collect_area_chest")
public class CollectAreaChest extends BaseEntity {
    @Name
    @Comment("主键")
    @Prev(els=@EL("uuid(32)"))
    @ColDefine(type = ColType.VARCHAR, width = 36)
    private String id;

    @Column
    @Comment("区域ID")
    @ColDefine(type = ColType.VARCHAR, width = 36, notNull = true)
    private String areaId;

    @Column
    @Comment("x坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int x;

    @Column
    @Comment("y坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int y;

    @Column
    @Comment("z坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int z;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
