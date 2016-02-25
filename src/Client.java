import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	 public static void main(String args[]) {
	       
	        try {
	            String name = "ReadWrite";
	            Registry registry = LocateRegistry.getRegistry("localhost");
	            ReadWrite read = (ReadWrite) registry.lookup(name);
	            System.out.println(read.read(1)[1]);
	        } catch (Exception e) {
	            System.err.println("ComputePi exception:");
	            e.printStackTrace();
	        }
	    }    
}
