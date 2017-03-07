package demo.tcp.data.push;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Soby Chacko
 */
@SpringBootApplication
public class TcpDataPushApplication {

	public static void main(String... args) throws Exception {
		byte[] data;
		String[] foobar = new String[]{"foo", "bar","foo","bar"};
		String messageCountText = args[0];
		int messageCount = Integer.valueOf(messageCountText);
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress("localhost", 29001), 5000);
			for (int i = 0; i <messageCount; i++) {
				ByteBuffer buffer = ByteBuffer.allocate(40);
				buffer.putInt(36); // length of the message
				//number of strings
				buffer.putInt(4);
				for (String fb : foobar) {
					buffer.putInt(fb.length());
					buffer.put(fb.getBytes());
				}
				//add a random number in the mix
				int value = new Random().nextInt(2000000);
				System.out.println("Random: " + value);
				buffer.putInt(value);
				data = buffer.array();
				socket.getOutputStream().write(data);
				socket.getOutputStream().flush();
			}
		}
		catch (IOException e) {
			System.out.println("Error occurred: " + e);
		}
	}
}
