package tp6;

import java.io.ObjectOutputStream;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSimpleClient {
	
	public static void main(String[] args) throws Exception {
		//System.setProperty("javax.net.ssl.trustStore", "truststore.client");
		//System.setProperty("javax.net.ssl.trustStorePassword", "testes");
		SocketFactory sf = SSLSocketFactory.getDefault();
		SSLSocket s = (SSLSocket) sf.createSocket("127.0.0.1", 9096);
		
		ObjectOutputStream outStream = new ObjectOutputStream(s.getOutputStream());
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Insira o seu username");
		String user = sc.nextLine();
		
		System.out.println("Insira a sua password");
		String pass = sc.nextLine();
		
		outStream.writeObject(user);
		outStream.writeObject(pass);
		
		sc.close();
		outStream.close();
		s.close( );
	}
}
