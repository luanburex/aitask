package com.ai.app.aitask;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Yummy {

    public static void main(String[] args) throws Exception {
        Process p = Runtime.getRuntime().exec("tasklist");
        System.out.println(p.toString());
        System.out.println(Arrays.toString(new int[] {}));
        System.out.println(new Boolean[]{});
        System.out.println(new int[] {});
        System.out.println(new String[] {});
        System.out.println(new Object[] {});
        System.out.println(String.class.getName());
        System.out.println(Integer.class.getName());
    }
}
