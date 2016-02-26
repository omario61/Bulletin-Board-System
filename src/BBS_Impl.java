import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BBS_Impl implements BBS_Interface {
	private PrintWriter reader_out, writer_out;
	private int request_count, serve_count, max_access, news, reader_count;
	private HashMap<Integer, Integer> service_map, request_map, news_map;
	private ReentrantReadWriteLock lock;
	private final double MIN_INTERVAL = 0.0;
	private final double MAX_INTERVAL = 10000.0 - MIN_INTERVAL;
	private boolean system_shutdown;

	protected BBS_Impl(int max_access) throws RemoteException {
		system_shutdown = false;
		service_map = new HashMap<Integer, Integer>();
		request_map = new HashMap<Integer, Integer>();
		news_map = new HashMap<Integer, Integer>();
		try {
			reader_out = new PrintWriter(new FileWriter("Server_Readers"));
			writer_out = new PrintWriter(new FileWriter("Server_Writers"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		news = -1;
		reader_count = 0;
		serve_count = request_count = 1;
		this.max_access = max_access;
		lock = new ReentrantReadWriteLock(true);
		initiate_logs();
	}

	public void initiate_logs() {
		reader_out.println("sSeq\toVal\trID\trNum");
		writer_out.println("sSeq\toVal\twID");
	}

	public boolean is_shutdown() {
		check_server_shutdown();
		return system_shutdown;
	}

	@Override
	public int[] read(int client_id) {
		set_request(client_id, request_count++);
		System.out.println("Read  Request " + (request_count - 1) + " : Client " + client_id);
		// System.out.println("read client id = "+ client_id + " args 1 = "+
		// request_map.get(client_id));
		lock.readLock().lock();

		set_service(client_id, serve_count++);
		System.out.println("Read  Service " + (serve_count - 1) + " : Client " + client_id);
		set_news(client_id, news);
		reader_count++;
		reader_out.println(get_service(client_id) + "\t" + news + "\t" + client_id + "\t" + reader_count);
		try {
			Thread.sleep(generate_random());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Read  Ended     : Client " + client_id);
		// set_service(client_id, serve_count++);
		reader_count--;
		lock.readLock().unlock();
		// System.out.println("client id = "+ client_id + " args 1 = "+
		// request_map.get(client_id));
		// System.out.println("client id = "+ client_id + " args 2 = "+
		// service_map.get(client_id));
		try {
			return new int[] { get_request(client_id), get_service(client_id), get_news(client_id) };
		} finally {
			// System.out.println(serve_count);
			check_server_shutdown();
		}
	}

	private void check_server_shutdown() {
		if ((lock.getQueueLength() == 0) && (lock.getReadLockCount() == 0) && (lock.getWriteHoldCount() == 0)
				&& serve_count > max_access) {
			system_shutdown = true;
		}
	}

	public boolean is_idle() {
		if ((lock.getQueueLength() == 0) && (lock.getReadLockCount() == 0) && (lock.getWriteHoldCount() == 0)) {
			return true;
		}
		return false;
	}

	private int generate_random() {
		return (int) (MIN_INTERVAL + Math.random() * MAX_INTERVAL);
	}

	@Override
	public int[] write(int client_id) {
		set_request(client_id, request_count++);
		System.out.println("Write Request " + (request_count - 1) + " : Client " + client_id);
		// System.out.println("client id = "+ client_id + " args 1 = "+
		// request_map.get(client_id));

		lock.writeLock().lock();
		set_service(client_id, serve_count++);
		System.out.println("Write Service " + (serve_count - 1) + " : Client " + client_id);
		news = client_id;
		set_news(client_id, news);
		writer_out.println(get_service(client_id) + "\t" + news + "\t" + client_id);
		try {
			Thread.sleep(generate_random());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Write  Ended   : Client " + client_id);
		lock.writeLock().unlock();

		// System.out.println("client id = "+ client_id + " args 1 = "+
		// request_map.get(client_id));
		// System.out.println("client id = "+ client_id + " args 2 = "+
		// service_map.get(client_id));
		try {
			return new int[] { get_request(client_id), get_service(client_id) };
		} finally {
			// System.out.println(serve_count);
			check_server_shutdown();
		}
	}

	private synchronized int get_news(int client_id) {
		return news_map.get(client_id);
	}

	private synchronized int get_request(int client_id) {
		return request_map.get(client_id);
	}

	private synchronized int get_service(int client_id) {
		return service_map.get(client_id);
	}

	private synchronized void set_news(int client_id, int value) {
		news_map.put(client_id, value);
	}

	private synchronized void set_service(int client_id, int value) {
		service_map.put(client_id, value);
	}

	private synchronized void set_request(int client_id, int value) {
		request_map.put(client_id, value);
	}

	public void close() {
		reader_out.close();
		writer_out.close();
	}

}
