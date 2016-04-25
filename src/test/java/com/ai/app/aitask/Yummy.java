package com.ai.app.aitask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ai.app.aitask.common.Caster;
import com.ai.app.aitask.common.Config;
import com.ai.app.aitask.common.Constants;
import com.ai.app.aitask.common.OrderedProperties;

public class Yummy implements Constants {
    boolean good;
    public static void main(String[] args) throws Exception {
        List<Object> obj = new LinkedList<Object>();
        obj.add(1);
        List<Integer> inte = Caster.cast(obj);
        System.out.println(inte.get(0));
    }
}
