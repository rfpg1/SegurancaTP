package tp2;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class MyServer {

	private final static int MEGABYTE = 1024;
	private final static String FILE = "users.txt";
	
	private HashMap<String, String> users = new HashMap<>();
	private HashMap<String, String> files = new HashMap<>();
	
	public static void main(String[] args) {
		System.out.println("servidor: main");
		MyServer server = new MyServer();
		server.startServer();
	}
	
	@SuppressWarnings("resource")
	public void startServer (){
		ServerSocket sSoc = null;

		try {
			loadUsers();
			sSoc = new ServerSocket(23456);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			} catch(FileNotFoundException e) {
				System.out.println("Erro no ficheiro");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		//sSoc.close();
	}


	private void loadUsers() throws FileNotFoundException {
		Scanner sc = new Scanner(new File(FILE));
		while(sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] credencias = line.split(":");
			users.put(credencias[0], credencias[1]);
			files.put(credencias[0], credencias[2]);
		}
		
		sc.close();
		
	}


	//Threads utilizadas para comunicacao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}

		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String user = null;
				String passwd = null;


				user = (String)inStream.readObject();
				passwd = (String)inStream.readObject();
				System.out.println("thread: depois de receber a password e o user");

				if (autenticado(user, passwd)){
					//Envia o ficheiro que se encontra no servidor relativamente ao user autenticado
					outStream.writeObject(true);
					File file = new File(files.get(user));
					FileInputStream fis = new FileInputStream(file);
					outStream.writeObject(files.get(user));
					int filesize = (int) file.length();
					outStream.writeObject(filesize);
					byte[] buffer = new byte[MEGABYTE];
					while(fis.read(buffer, 0, buffer.length)> 0) {
						outStream.write(buffer, 0, buffer.length);
					}
					fis.close();
				} else if(users.get(user) != null) {
					outStream.writeObject(false);
					outStream.writeObject(false);
					outStream.writeObject("password inválida");
				} else {
					//Recebe um ficheiro de um user que acabou de criar conta
					System.out.println("User não reconhecido");
					outStream.writeObject(false);
					outStream.writeObject(true);
					outStream.writeObject("User registado");
					String fileName = (String)inStream.readObject();
					//String[] t = fileName.split("\\.");
					//StringBuilder bob = new StringBuilder(t[0] + "Server." + t[1]);
					//registaUser(user, passwd, bob.toString());
					registaUser(user, passwd, fileName);
					//FileOutputStream fos = new FileOutputStream(bob.toString());
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
				}

				outStream.close();
				inStream.close();

				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		private void registaUser(String user, String passwd, String fileName) throws FileNotFoundException {
			PrintWriter pw = new PrintWriter(FILE);
			for(String s : users.keySet()) {
				pw.println(s + ":" + users.get(s) + ":" + files.get(s));
			}
			users.put(user, passwd);
			files.put(user, fileName);
			pw.println(user + ":" + passwd + ":" + fileName);
			pw.close();
			
		}

		private boolean autenticado(String user, String passwd) {
			String pw = users.get(user);
			if(pw != null) {
				return pw.equals(passwd);
			} else {
				return false;
			}
		}

	}
}
