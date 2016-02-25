import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class SSH {
	public static void main(String[] args) throws Exception {
		Process p = Runtime.getRuntime().exec("ssh Hisham");
		PrintStream out = new PrintStream(p.getOutputStream());
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

		out.println("ls -l /home");
		while (in.ready()) {
		  String s = in.readLine();
		  System.out.println(s);
		}
		out.println("exit");

		p.waitFor();
	}
}
