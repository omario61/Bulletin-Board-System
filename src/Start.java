import java.io.BufferedReader;
import java.io.FileReader;

public class Start {
	static String file_name;
	static String server_ip, server_port;
	static String[] readers, readers_ips, readers_passwords, writers, writers_ips, writers_passwords;
	static int number_of_accesses;

	public static void read_File() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file_name));
		server_ip = in.readLine().split("=")[1].trim();
		server_port = in.readLine().split("=")[1].trim();
		int number_of_readers = Integer.parseInt(in.readLine().split("=")[1].trim());
		readers = new String[number_of_readers];
		readers_ips = new String[number_of_readers];
		readers_passwords = new String[number_of_readers];
		for (int i = 0; i < number_of_readers; i++) {
			String[] line_split = in.readLine().split("=");
			readers[i] = line_split[1].trim();
			readers_ips[i] = line_split[2].trim();
			if(line_split.length>=4)
				readers_passwords[i] = line_split[3].trim();
		}
		int number_of_writers = Integer.parseInt(in.readLine().split("=")[1].trim());
		writers = new String[number_of_writers];
		writers_ips = new String[number_of_writers];
		writers_passwords = new String[number_of_writers];
		for (int i = 0; i < number_of_writers; i++) {
			String[] line_split = in.readLine().split("=");
			writers[i] = line_split[1].trim();
			writers_ips[i] = line_split[2].trim();
			if(line_split.length>=4)
				writers_passwords[i] = line_split[3].trim();
		}
		number_of_accesses = Integer.parseInt(in.readLine().split("=")[1]);
		in.close();
	}

	public static void main(String[] args) throws Exception {
		file_name = args[0];
		read_File();
		java.rmi.registry.LocateRegistry.createRegistry(1099);
		Thread[] readers_threads = new Thread[readers.length];
		Thread[] writers_threads = new Thread[writers.length];
		Process server_process = null;
		try {
			System.out.println("Server Initiated");
			server_process = Runtime.getRuntime().exec("java -classpath "+args[1]+" Server " + server_port + " "
					+ (number_of_accesses * (readers.length + writers.length)));
			
			for (int i = 0; i < readers.length; i++) {
				readers_threads[i] = new Thread(new Client_Initiator(readers_ips[i], readers[i], readers_passwords[i],
						server_ip, server_port, true, i + 1, number_of_accesses));
				readers_threads[i].start();
			}
			for (int i = 0; i < writers.length; i++) {
				writers_threads[i] = new Thread(new Client_Initiator(writers_ips[i], writers[i], writers_passwords[i],
						server_ip, server_port, false, i + 1 + readers.length, number_of_accesses));
				writers_threads[i].start();
			}
			
		} finally {
			for (int i = 0; i < readers_threads.length; i++) {
				readers_threads[i].join();
			}
			for (int i = 0; i < writers_threads.length; i++) {
				writers_threads[i].join();
			}
			System.out.println("Thread Joined.");
			Thread.sleep(Server.SLEEP_INTERVAL*20);
			server_process.destroy();
			System.out.println("Server has been Terminated.");
		}
	}

}
