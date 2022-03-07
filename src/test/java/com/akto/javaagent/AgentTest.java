package com.akto.javaagent;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.tools.attach.VirtualMachine;

import org.junit.BeforeClass;

public class AgentTest {

    public static boolean started = false;

    public static synchronized void loadAgent() {
        if (!started) {
            started = true;
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            int p = nameOfRunningVM.indexOf('@');
            String pid = nameOfRunningVM.substring(0, p);
            String jarFilePath = "target/javaagent-1.0-SNAPSHOT-jar-with-dependencies.jar";
            try {
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent(jarFilePath, "");
                System.out.println("loaded VM");
                vm.detach();
            } catch (Exception e) {
                System.out.println("exception while loading VM");
                throw new RuntimeException(e);
            }
        }
    }
    
    @BeforeClass
    public static void init() {
        loadAgent();
    }

}
