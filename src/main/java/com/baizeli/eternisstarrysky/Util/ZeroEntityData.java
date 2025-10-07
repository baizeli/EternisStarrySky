package com.baizeli.eternisstarrysky.Util;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;

/**
 * 类：零值实体同步数据
 * 类名缩写：ZED
 * 用于替换实体原本的数据以实现全部数据归 0 的效果
 */


public class ZeroEntityData extends SynchedEntityData {
    SynchedEntityData synchedEntityData;
    public ZeroEntityData(Entity p_135351_,SynchedEntityData synchedEntityData) {
        super(p_135351_);
        this.synchedEntityData = synchedEntityData;
    }


    /*public <T> DataItem<T> getItem(EntityDataAccessor<T> p_135380_) {
        DataItem<T> di = synchedEntityData.getItem(p_135380_);
        T value = di.getValue();
        if (value instanceof Integer)
            di.setValue((T)(Object)0);
        if (value instanceof Long)
            di.setValue((T)(Object)0L);
        if (value instanceof Float)
            di.setValue(((T)(Object)0.0F));
        if (value instanceof Double)
            di.setValue(((T)(Object)0.0D));
        if (value instanceof Boolean)
            di.setValue((T)(Object)false);
        return di;
    }*/

    @Override
    public <T> T get(EntityDataAccessor<T> p_135371_) {
        T value = super.get(p_135371_);
        if (value instanceof Integer)
            return (T)(Object)0;
        if (value instanceof Byte)
            return (T)(Object)(byte)0;
        if (value instanceof Long)
            return (T)(Object)0;
        if (value instanceof Float)
            return (((T)(Object)0.0F));
        if (value instanceof Double)
            return (((T)(Object)0.0D));
        if (value instanceof Boolean)
            return (T)(Object)false;
        return value;
    }
}
