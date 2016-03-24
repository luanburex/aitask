package com.ai.app.aitask.task.preparer.impl;

import java.util.Map;

import com.ai.app.aitask.task.preparer.IDataPreparer;

public class DefaultDataPreparer implements IDataPreparer {

    private Map<String, Object> exedata;

    public DefaultDataPreparer(Map<String, Object> exedata) {
        this.exedata = exedata;
    }

    @Override
    public void prepare(Map<String, Object> datamap) {
        // TODO Auto-generated method stub
        
    }

}
