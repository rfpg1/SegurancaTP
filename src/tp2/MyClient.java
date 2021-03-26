package tp2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MyClient {
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
				//Vai buscar o ficheiro que está no servidor!
				String f = (String)inStream.readObject();
				String[] t = f.split("\\.");
				StringBuilder bob = new StringBuilder();
				bob.append(t[0].substring(0, t[0].length() - 6) + "." + t[1]);
				String fileName = bob.toString();
				FileOutputStream fos = new FileOutputStream(fileName);
				int filesize = (int) inStream.readObject();
				byte[] buffer = new byte[MEGABYTE];
				int read = 0;
				int remaining = filesize;
				while((read = inStream.read(buffer, 0, remaining)) > 0) {
					remaining -= read;
					fos.write(buffer, 0, read);
				}
				fos.close();
			} else {
				boolean regista = (boolean) inStream.readObject();
				if(regista) {
					//Envia o ficheiro para o servidor
					String s = (String) inStream.readObject();
					System.out.println(s);
					System.out.println("Diga o caminho para o ficheiro que quer guardar no servidor");
					String fileName = sc.nextLine();
					
					//File file = new File(FILE);
					File file = new File(fileName);
					outStream.writeObject(file.getName());
					
					int filesize = (int) file.length();
					outStream.writeObject(filesize);
					FileInputStream fis = new FileInputStream(file);
					byte[] buffer = new byte[MEGABYTE];
					while(fis.read(buffer, 0, buffer.length)> 0) {
						outStream.write(buffer, 0, buffer.length);
					}
					fis.close();
				} else {
					String s = (String) inStream.readObject();
					System.out.println(s);
				}
			}
			sc.close();
			socket.close();
			
		} catch(UnknownHostException | ClassNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
