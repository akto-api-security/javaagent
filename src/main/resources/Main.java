
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import sun.tools.jconsole.LocalVirtualMachine;
import com.sun.tools.attach.VirtualMachine;

public class Main {

    public static boolean started = false;

    public static synchronized void loadAgent(String pid, String kafkaIP) {
        System.out.println("classpath: " + System.getProperty("java.class.path"));

        if (!started) {
    final Map<Integer, LocalVirtualMachine> virtualMachines = LocalVirtualMachine.getAllVirtualMachines();
    for (final Entry<Integer, LocalVirtualMachine> entry : virtualMachines.entrySet()) {
        System.out.println(entry.getKey() + " : " + entry.getValue().displayName());
    }
            started = true;
            String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
            //int p = nameOfRunningVM.indexOf('@');
	    System.out.println("nameOfRunningVM: " + nameOfRunningVM);
            //String pid = nameOfRunningVM.substring(0, p);
            String jarFilePath = "/Users/ankushjain/akto_code/javaagent/target/javaagent-1.0-SNAPSHOT-jar-with-dependencies.jar";
	    System.out.println("pid: " + pid);
            try {
                VirtualMachine vm = VirtualMachine.attach(pid);
                vm.loadAgent(jarFilePath, kafkaIP);
                System.out.println("loaded VM");
                vm.detach();
            } catch (Exception e) {
                System.out.println("exception while loading VM");
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void main(String[] args) {
        loadAgent(args[1], args[0]);
        while(true) {
            try { 
		Thread.sleep(100);
	    } catch(Exception e) {

	    }
        }
    }

}
