import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Start {
	static String file_name;
	static String server_ip, server_port;
	static String[] readers, readers_ips, readers_passwords, writers, writers_ips, writers_passwords;
	static int number_of_accesses;

	public static void read_File() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file_name));
		server_ip = in.readLine().split("=")[1];
		server_port = in.readLine().split("=")[1];
		int number_of_readers = Integer.parseInt(in.readLine().split("=")[1]);
		readers = new String[number_of_readers];
		readers_ips = new String[number_of_readers];
		readers_passwords = new String[number_of_readers];
		for (int i = 0; i < number_of_readers; i++) {
			String[] line_split = in.readLine().split("=");
			readers[i] = line_split[1];
			readers_ips[i] = line_split[2];
			readers_passwords[i] = line_split[3];
		}
		int number_of_writers = Integer.parseInt(in.readLine().split("=")[1]);
		writers = new String[number_of_writers];
		writers_ips = new String[number_of_writers];
		writers_passwords = new String[number_of_writers];
		for (int i = 0; i < number_of_writers; i++) {
			String[] line_split = in.readLine().split("=");
			writers[i] = line_split[1];
			writers_ips[i] = line_split[2];
			writers_passwords[i] = line_split[3];
		}
		number_of_accesses = Integer.parseInt(in.readLine().split("=")[1]);
		in.close();
	}
	public static void main(String[] args) throws Exception {
		file_name = args[0];
		read_File();

		// System.out.println(number_of_accesses);
		// System.out.println("server ip: " + server_ip + " server port: " +
		// server_port);
		// for (int i = 0; i < readers.length; i++) {
		// System.out.println("reader number " + i + " is " + readers[i] + " his
		// password " + readers_passwords[i]);
		// }
		// for (int i = 0; i < writers.length; i++) {
		// System.out.println("writer number " + i + " is " + writers[i] + " his
		// password " + writers_passwords[i]);
		// }
		
//		Process p = Runtime.getRuntime().exec("rmiregistry");
//		Process a = Runtime.getRuntime().exec("java Server "+server_port+" "+number_of_accesses);
//		PrintStream out = new PrintStream(a.getOutputStream());
//		BufferedReader in = new BufferedReader(new InputStreamReader(a.getInputStream()));
//
//		out.println("cd \n ");
//		Thread.sleep(10000);
//		while(in.ready()){
//			System.out.println(in.readLine());
//		}
		
		for (int i = 0; i < readers.length; i++) {
			new Thread(new Client_Initiator(readers_ips[i], readers[i], readers_passwords[i], server_ip, server_port,
					true, i + 1,number_of_accesses)).start();
		}
//		for (int i = 0; i < writers.length; i++) {
//			new Thread(new Client_Initiator(writers_ips[i], writers[i], writers_passwords[i], server_ip, server_port,
//					false, i + 1 + readers.length,number_of_accesses)).start();
//		}
	}

}
