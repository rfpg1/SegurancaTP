import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

	private final static int MEGABYTE = 1024;
	
	public static void main(String[] args) {
		System.out.println("servidor: main");
		MyServer server = new MyServer();
		server.startServer();
	}

	@SuppressWarnings("resource")
	public void startServer (){
		ServerSocket sSoc = null;

		try {
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
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		//sSoc.close();
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

				try {
					user = (String)inStream.readObject();
					passwd = (String)inStream.readObject();
					System.out.println("thread: depois de receber a password e o user");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

				//TODO: refazer
				//este codigo apenas exemplifica a comunicacao entre o cliente e o servidor
				//nao faz qualquer tipo de autenticacao
				if (user.length() != 0){
					outStream.writeObject(true);
					FileOutputStream fos = new FileOutputStream("usersServer.txt");
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
				else {
					outStream.writeObject(false);
				}

				outStream.close();
				inStream.close();

				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
