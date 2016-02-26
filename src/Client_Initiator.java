import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Client_Initiator implements Runnable {
	private String host, user, password, server_ip, server_port;
	int client_id, number_of_access;
	boolean is_reader = false;

	public Client_Initiator(String host, String user, String password, String server_ip, String server_port,
			boolean is_reader, int client_id, int number_of_access) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.server_ip = server_ip;
		this.server_port = server_port;
		this.client_id = client_id;
		this.is_reader = is_reader;
		this.number_of_access = number_of_access;
	}

	@Override
	public void run() {
		intiate_client();
	}

	public void intiate_client() {
		// String host = "192.168.0.118";
		// String user = "hisham";
		// String password = "12345";
		String command;
		if (is_reader)
			command = "java" + " Client " + server_ip + " " + server_port + " " + client_id + " " + "0 "
					+ number_of_access;
		else
			command = "java" + " Client " + server_ip + " " + server_port + " " + client_id + " " + "1 "
					+ number_of_access;
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);

			session.connect();
			System.out.println("ssh conncection to client "+client_id);

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("Client "+client_id+" exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("ssh session of client "+client_id+" has been terminated.");
		} catch (Exception e) {
			System.out.println("ssh session error: "+e.toString()+" for client "+client_id);
		}
	}
}
