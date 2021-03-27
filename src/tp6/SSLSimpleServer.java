package tp6;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLSimpleServer extends Thread {

	private Socket sock;

	public SSLSimpleServer (Socket s) {
		this.sock = s;
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("javax.net.ssl.keyStore", "keystore.server");
		System.setProperty("javax.net.ssl.keyStorePassword", "testes");
		ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();	
		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(9096);
		while(true) {
			new SSLSimpleServer(ss.accept()).start();
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Aqui");
			ObjectInputStream inStream = new ObjectInputStream(sock.getInputStream());
			String user = null;
			String passwd = null;
			user = (String)inStream.readObject();
			passwd = (String)inStream.readObject();
			System.out.println(user + " " + passwd);
			inStream.close();
			System.out.println("Teste");
			sock.close( );
		} catch (IOException ioe) {
			// Client disconnected; exit this thread
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

}
