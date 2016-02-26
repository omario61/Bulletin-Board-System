import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server{
	public static final int SLEEP_INTERVAL = 500;
	public static final int MAX_IDLE = 5000/SLEEP_INTERVAL;
	public static void main(String args[]) {
		int port = Integer.parseInt(args[0]);
		int num_of_access = Integer.parseInt(args[1]);
		BBS_Interface stub = null;
		BBS_Impl impl = null;
		PrintStream out = System.out;
		try {
			String name = "BBS_obj";
			impl = new BBS_Impl(num_of_access);
			stub = (BBS_Interface) UnicastRemoteObject.exportObject(impl, port);
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(name, (BBS_Interface)stub);
			
			System.setOut(new PrintStream(new File("server_log")));
			System.out.println("Bulletin Board Server is ready to listen at port: " + port);
			System.out.print("Bulletin Board Server is ready to listen on ");
			System.out.println(InetAddress.getLocalHost().getHostName());
			int idle_count = 0;
			while(!impl.is_shutdown()){
				if(impl.is_idle()){
					idle_count++;
				}
				if(idle_count >MAX_IDLE){
					break;
				}
				Thread.sleep(SLEEP_INTERVAL);
			}
			Thread.sleep(SLEEP_INTERVAL*2);
			System.out.println("Server has Ended.");
		} catch (Exception e) {
			System.err.println("Server exception thrown: " + e.toString());
			e.printStackTrace();
		}
		finally {
			impl.close();
			System.setOut(out);
			try {
				UnicastRemoteObject.unexportObject(stub, true);
			} catch (NoSuchObjectException e) {
				e.printStackTrace();
			}
		}
	}
}
