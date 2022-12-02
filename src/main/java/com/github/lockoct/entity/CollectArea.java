package com.github.lockoct.entity;

import org.nutz.dao.entity.annotation.*;

import java.util.List;

@Table("mr_collect_area")
public class CollectArea extends BaseEntity {
    @Name
    @Comment("主键")
    @Prev(els=@EL("uuid(32)"))
    @ColDefine(type = ColType.VARCHAR, width = 36)
    private String id;

    @Column
    @Comment("区域名称")
    @ColDefine(type = ColType.VARCHAR, width = 45, notNull = true)
    private String name;

    @Column
    @Comment("所在世界")
    @ColDefine(type = ColType.VARCHAR, width = 45, notNull = true)
    private String world;

    @Column
    @Comment("边界点1 x坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int x1;

    @Column
    @Comment("边界点1 y坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int y1;

    @Column
    @Comment("边界点1 z坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int z1;

    @Column
    @Comment("边界点2 x坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int x2;

    @Column
    @Comment("边界点2 y坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int y2;

    @Column
    @Comment("边界点2 z坐标")
    @ColDefine(type = ColType.INT, notNull = true)
    private int z2;

    @Column
    @Comment("是否启用(0: 禁用, 1:启用)")
    @ColDefine(type = ColType.BOOLEAN, notNull = true)
    private boolean enabled;

    @Column
    @Comment("是否已删除(0: 未删除, 1:已删除)")
    @ColDefine(type = ColType.BOOLEAN, notNull = true)
    private boolean deleted;

    @Many(field = "areaId")
    private List<CollectAreaChest> chests;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getZ1() {
        return z1;
    }

    public void setZ1(int z1) {
        this.z1 = z1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public int getZ2() {
        return z2;
    }

    public void setZ2(int z2) {
        this.z2 = z2;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<CollectAreaChest> getChests() {
        return chests;
    }

    public void setChests(List<CollectAreaChest> chests) {
        this.chests = chests;
    }
}
