import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BBS_Impl implements BBS_Interface {
	private PrintWriter reader_out, writer_out;
	private int request_count, serve_count, max_access, news;
	private HashMap<Integer, Integer> service_map, request_map, news_map;
	private ReentrantReadWriteLock lock;
	private final double MIN_INTERVAL = 0.0;
	private final double MAX_INTERVAL = 10000.0 - MIN_INTERVAL;
	private boolean system_shutdown;

	protected BBS_Impl(int max_access) throws RemoteException {
		system_shutdown = false;
		service_map = new HashMap<Integer,Integer>();
		request_map = new HashMap<Integer,Integer>();
		news_map = new HashMap<Integer,Integer>();
		try {
			reader_out = new PrintWriter(new FileWriter("Server_Readers"));
			writer_out = new PrintWriter(new FileWriter("Server_Writers"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		news = -1;
		serve_count = request_count = 1;
		this.max_access = max_access;
		lock = new ReentrantReadWriteLock(true);
		initiate_logs();
	}

	public void initiate_logs() {
		reader_out.println("sSeq\toVal\trID\trNum");
		writer_out.println("sSeq\toVal\twID");
	}
	public boolean is_shutdown(){
		return system_shutdown;
	}
	@Override
	public int[] read(int client_id) {
		request_map.put(client_id, request_count++);
		lock.readLock().lock();
		service_map.put(client_id, serve_count++);
		try {
			Thread.sleep(generate_random());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		news_map.put(client_id, news);
		reader_out
				.println(service_map.get(client_id) + "\t" + news + "\t" + client_id + "\t" + lock.getReadLockCount());
		lock.readLock().unlock();
		check_server_shutdown();
		return new int[] { request_map.get(client_id), service_map.get(client_id), news_map.get(client_id) };
	}

	private void check_server_shutdown() {
		if ((lock.getQueueLength() == 0) && (lock.getReadLockCount() == 0) && (lock.getWriteHoldCount() == 0)
				&& serve_count == max_access) {
			system_shutdown = true;
		}
	}

	private int generate_random() {
		return (int) (MIN_INTERVAL + Math.random() * MAX_INTERVAL);
	}

	@Override
	public int[] write(int client_id) {
		request_map.put(client_id, request_count++);
		lock.writeLock().lock();
		service_map.put(client_id, serve_count++);
		try {
			Thread.sleep(generate_random());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		news = client_id;
		news_map.put(client_id, news);
		writer_out
				.println(service_map.get(client_id) + "\t" + news + "\t" + client_id);
		lock.writeLock().unlock();
		check_server_shutdown();
		return new int[] { request_map.get(client_id), service_map.get(client_id)};
	}

	public void close() {
		reader_out.close();
		writer_out.close();
	}

}
