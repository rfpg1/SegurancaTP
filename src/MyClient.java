import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MyClient {
	
	private final static String FILE = "users.txt";
	private final static int MEGABYTE = 1024;
	
	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", 23456);
			ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
			Scanner sc = new Scanner(System.in);
			
			System.out.println("Insira o seu username");
			String user = sc.nextLine();
			
			System.out.println("Insira a sua password");
			String pass = sc.nextLine();
			
			outStream.writeObject(user);
			outStream.writeObject(pass);
			
			boolean b = (boolean) inStream.readObject();
			
			if(b) {
				File file = new File(FILE);
				FileInputStream fis = new FileInputStream(file);
				int filesize = (int) file.length();
				outStream.writeObject(filesize);
				byte[] buffer = new byte[MEGABYTE];
				while(fis.read(buffer, 0, buffer.length)> 0) {
					outStream.write(buffer, 0, buffer.length);
				}
				fis.close();
			}
			//inStream.close();
			//outStream.close();
			sc.close();
			socket.close();
			
		} catch(UnknownHostException | ClassNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
