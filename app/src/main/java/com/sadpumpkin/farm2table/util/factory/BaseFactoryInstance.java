package com.sadpumpkin.farm2table.util.factory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.sadpumpkin.farm2table.util.factory.definition.BaseFactoryDefinition;

@IgnoreExtraProperties
public abstract class BaseFactoryInstance {

    @Exclude
    protected BaseFactoryDefinition _definition;

    private String _factoryType;
    private Timestamp _lastStart;
    private Long _count;

    private MutableLiveData<Double> _mutableProgress = null;
    private MutableLiveData<Long> _mutableCount = null;

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

    public void init() {
        _mutableProgress = new MutableLiveData<>();
        _mutableCount = new MutableLiveData<>(_count);
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

    @Exclude
    public LiveData<Long> getCountLive() {
        if (_mutableCount == null) {
            _mutableCount = new MutableLiveData<>(_count);
            _mutableCount.postValue(_count);
        }
        return _mutableCount;
    }

    public long getCount(){
        return _count;
    }

    public void setCount(Long count) {
        _count = count;
        if (_mutableCount != null) {
            _mutableCount.postValue(count);
        }
    }

    @Exclude
    public LiveData<Double> getProgressLive() {
        if (_mutableProgress == null) {
            _mutableProgress = new MutableLiveData<>(0D);
            _mutableProgress.postValue(0D);
        }
        return _mutableProgress;
    }

    @Exclude
    public void setDefinition(BaseFactoryDefinition definition) {
        _definition = definition;
    }

    public void updateMutableProgress() {
        long startMills = _lastStart.getSeconds() * 1000;
        long nowMills = Timestamp.now().getSeconds() * 1000;
        long progressMills = nowMills - startMills;
        long durationMills = _definition.getDurationMills();
        if(_mutableProgress == null){
            _mutableProgress = new MutableLiveData<>(0D);
        }
        _mutableProgress.postValue(progressMills / (double) durationMills);
    }
}
