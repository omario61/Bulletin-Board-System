import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	public static void main(String args[]) {
		int port = Integer.parseInt(args[0]);
		int num_of_access = Integer.parseInt(args[1]);
		try {
			String name = "BBS_obj";
			BBS_Impl impl = new BBS_Impl(num_of_access);
			BBS_Interface stub = (BBS_Interface) UnicastRemoteObject.exportObject(impl, port);
		    
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(name, (BBS_Interface)stub);
			System.out.println("Bulletin Board Server is ready to listen at port: " + port);
			System.out.print("Bulletin Board Server is ready to listen on ");
			System.out.println(InetAddress.getLocalHost().getHostName());
			while(!impl.is_shutdown());
			Thread.sleep(1000);
			impl.close();
			UnicastRemoteObject.unexportObject(stub, true);
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Server exception thrown: " + e.toString());
			e.printStackTrace();
		}
	}
}
