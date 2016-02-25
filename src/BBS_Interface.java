import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BBS_Interface extends Remote {
	public int[] read(int client_id)throws RemoteException;
	public int[] write(int client_id)throws RemoteException;
}
