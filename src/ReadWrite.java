import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReadWrite extends Remote{
	int [] read (int ID) throws RemoteException;
	int [] write(int ID) throws RemoteException;
}
