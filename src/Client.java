import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
	private int id,number_of_access;
	private final int READER = 0;
	private final int WRITER = 1;
	private BBS_Interface stub;
	int type;
	private final double MIN_INTERVAL = 250.0;
	private final double MAX_INTERVAL = 10000.0-MIN_INTERVAL;
	private PrintWriter out;
	public Client(BBS_Interface stub,int id,int number_of_access,int type){
		this.id = id;
		this.number_of_access = number_of_access;
		this.stub = stub;
		this.type = type;
		initiate_log();
	}
	public void initiate_log(){
		try {
			out = new PrintWriter(new FileWriter("log"+id));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(type == READER){
			out.println("Client type: Reader");
			out.println("Client Name: "+id);
			out.println("rSeq\tsSeq\toVal");
		}else if(type == WRITER){
			out.println("Client type: Writer");
			out.println("Client Name: "+id);
			out.println("rSeq\tsSeq");
		}else{System.out.println("Unkown Type of Clients");}
	}
	public void client_read(){
		int[] retVal = null;
		try {
			retVal = stub.read(id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(retVal[0]+"\t"+retVal[1]+"\t"+retVal[2]);
	}
	public void client_write(){
		int[] retVal = null;
		try {
			retVal = stub.write(id);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(retVal[0]+"\t"+retVal[1]);
	}
	public void run(){
		while(number_of_access-->0){
			if(type == READER){
				client_read();
			}else if(type == WRITER){
				client_write();
			}else{System.out.println("Unkown Type of Clients");}
			try {
				Thread.sleep(generate_random());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private int generate_random(){
		return (int) (MIN_INTERVAL+Math.random()*MAX_INTERVAL);
	}
	public void close_file(){
		out.close();
	}
	public static void main(String args[]) {
		BBS_Interface stub = null ;
		try {
			String name = "BBS_obj";
		
			Registry reg = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
			stub = (BBS_Interface) reg.lookup(name);
		} catch (Exception e) {
			System.err.println("exception:");
			e.printStackTrace();
		}
		int my_id = Integer.parseInt(args[2]);
    	int type = Integer.parseInt(args[3]);
    	int number_of_access = Integer.parseInt(args[4]);
    	Client my_client = new Client(stub,my_id, number_of_access, type);
    	System.out.println("Client "+my_id+" is initiated.");
    	my_client.run();
    	my_client.close_file();
    	System.out.println("Client "+my_id+" is terminated.");
	}
}
