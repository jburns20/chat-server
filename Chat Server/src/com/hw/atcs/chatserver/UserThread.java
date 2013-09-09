package com.hw.atcs.chatserver;

import java.net.*;
import java.io.*;

public class UserThread implements Runnable {
	
	private String name;
	protected Socket socket;
	protected PrintWriter out;
	protected BufferedReader in;

	public UserThread(Socket s) {
		socket = s;
		try {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            
            /* Say hi to the client */
            this.out.println("Username: ");
            name = this.in.readLine();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
