package com.ai.app.aitask.run;

import java.io.File;

import com.ai.app.aitask.common.ProcessWorker;

public class Python {
    public static void main(String[] args) {
        ProcessWorker w = new ProcessWorker("GBK");
        w.setDir(new File("C:/workspace/aitask/aiauto_ori/"));
        //        w.process("python", "test.py");
        //        w.process("cmd", "/c", "C:/workspace/aitask/aiauto_ori" + "/" + "run.bat", "C:/workspace/aitask/aiauto_ori" + "/"+"test.py");
        
        w.process("cmd", "/c", "python", "test.py");
        //        w.process("cmd", "/c", "start", "run.bat", "test.py");
        //        w.process("cmd", "/c", "start", "dir");
        System.out.println(w.getErrorOutput());
        System.out.println(w.getStandOutput());
    }
}
