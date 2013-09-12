package com.hw.atcs.chatserver;

import java.util.LinkedList;
import java.io.IOException;
import java.net.ServerSocket;

public class ChatServer {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: ChatServer <port>");
			return;
		}
		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Usage: ChatServer <port>");
			return;
		}
		new ChatServer(port);
	}
	
	private int port;
	private LinkedList<UserThread> userThreads;
	private ServerSocket listener;
	
	public ChatServer(int port) {
		this.port = port;
		this.userThreads = new LinkedList<UserThread>();
		try {
			this.listener = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: could not open ServerSocket.");
			System.exit(1);
		}
		//now run the server
		while(true) {
			try {
				UserThread accepted=new UserThread(this, listener.accept());
				sendMessage(null, accepted.getName()+" has joined the chat server.");
				this.userThreads.addLast(accepted);
				new Thread(accepted).start();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not accept the request.");
			}
		}
	}
	
	public synchronized void sendMessage(UserThread sender, String message) {
		if(sender!=null)
			message=sender.getName()+": "+message;
		for(UserThread t:this.userThreads) {
			t.receiveMessage(message);
		}
	}
	
	public synchronized void removeUserThread(UserThread thread) {
		this.userThreads.remove(thread);
		sendMessage(null, thread.getName()+" has left.");
	}
}

