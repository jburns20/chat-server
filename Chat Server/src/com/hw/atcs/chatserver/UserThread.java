package com.hw.atcs.chatserver;

import java.net.Socket;
import java.io.*;

public class UserThread implements Runnable {
	
	private String name;
	protected Socket socket;
	protected ChatServer chatServer;
	protected PrintWriter out;
	protected BufferedReader in;

	public UserThread(ChatServer server, Socket s) {
		socket = s;
		chatServer=server;
		try {
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	public void run() {
		this.out.println("Welcome to the Chat Server!");
		do {
			this.out.println("Enter your username to begin.");
			try {
				name = this.in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("There was a problem reading your username. Please try again.");
			}
		} while (name == null);
		chatServer.addUserThread(this);
		//Extra spacing, etc. for username separation would go here.
		while (true) {
			try {
				String message = this.in.readLine();
				if(message==null) {
					chatServer.removeUserThread(this);
					return;
				}
				chatServer.sendMessage(this, message+"\n");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("There was a problem reading your message. Please try again.");
			}
		}
	}
	
	public void receiveMessage(String message) {
		this.out.println(message);
	}
	
	public String getName() {
		return name;
	}
}
