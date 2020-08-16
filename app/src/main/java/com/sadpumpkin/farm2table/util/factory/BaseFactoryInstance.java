package com.sadpumpkin.farm2table.util.factory;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.sadpumpkin.farm2table.util.factory.definition.BaseFactoryDefinition;

@IgnoreExtraProperties
public abstract class BaseFactoryInstance {

    @Exclude
    private BaseFactoryDefinition _definition;

    private String _factoryType;
    private Timestamp _lastStart;
    private Long _count;

    protected BaseFactoryInstance() {
        _factoryType = "ERROR";
        _lastStart = Timestamp.now();
        _count = 1L;
    }

    protected BaseFactoryInstance(String factoryType, Timestamp lastStart, Long count) {
        _factoryType = factoryType;
        _lastStart = lastStart;
        _count = count;
    }

    public String getFactoryType() {
        return _factoryType;
    }

    public void setFactoryType(String _factoryType) {
        this._factoryType = _factoryType;
    }

    public Timestamp getLastStart() {
        return _lastStart;
    }

    public void setLastStart(Timestamp _lastStart) {
        this._lastStart = _lastStart;
    }

    public Long getCount() {
        return _count;
    }

    public void setCount(Long _count) {
        this._count = _count;
    }

    @Exclude
    public BaseFactoryDefinition getDefinition() {
        return _definition;
    }

    @Exclude
    public void setDefinition(BaseFactoryDefinition definition) {
        _definition = definition;
    }
}
