import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain  implements ReadWrite{

	public ServerMain() throws RemoteException {
		super();
	}
	@Override
	public int[] read(int ID) throws RemoteException {
		// TODO Auto-generated method stub
		return new int []{1,2,3};
	}

	@Override
	public int[] write(int ID) throws RemoteException {
		// TODO Auto-generated method stub
		return new int []{4,5,6};
	}
	public static void main(String[] args) {
		
        try {
            String name = "ReadWrite";
            ServerMain impl = new ServerMain();
            ReadWrite stub =
                    (ReadWrite) UnicastRemoteObject.exportObject(impl, 0);
            Registry registry = LocateRegistry.getRegistry(5000);
            registry.rebind(name, stub);
            System.out.println("ReadWrite bound 5000");
        } catch (Exception e) {
            System.err.println("ReadWrite exception:");
            e.printStackTrace();
        }
	}
}
