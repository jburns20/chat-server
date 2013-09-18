package com.hw.atcs.chatserver;

import java.util.LinkedList;
import java.awt.Color;
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
	
	private LinkedList<UserThread> userThreads;
	private ServerSocket listener;
	
	public ChatServer(int port) {
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
				new Thread(accepted).start();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Could not accept the request.");
			}
		}
	}
	
	public synchronized void sendMessage(UserThread sender, String message) {
		if(sender!=null) {
			Color c=sender.getColor();
			message="<p style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'><strong>" + sender.getName() + ":</strong> " + message + "</p>";
		}
		for(UserThread t:this.userThreads) {
			t.receiveMessage(message);
		}
	}
	
	public synchronized void whisperMessage(UserThread sender, String recipient, String message) {
		String message1 = sender.getName() + " whispers: " + message;
		boolean delivered = false;
		for (UserThread t : this.userThreads) {
			if (t.getName().toLowerCase().equals(recipient.toLowerCase())) {
				t.receiveMessage(message1);
				delivered = true;
			}
		}
		if (delivered) {
			sender.receiveMessage("You whisper to " + recipient + ": " + message);
		} else {
			sender.receiveMessage("There is no user with that nickname.");
		}
	}
	
	public synchronized void addUserThread(UserThread thread) {
		this.userThreads.addLast(thread);
		sendMessage(null, thread.getName()+" has joined the chat server.");
	}
	
	public synchronized void removeUserThread(UserThread thread) {
		this.userThreads.remove(thread);
		sendMessage(null, thread.getName()+" has left.");
	}
	
	public synchronized boolean nicknameExists(String name) {
		for (UserThread t : this.userThreads) {
			if (t.getName().toLowerCase().equals(name.toLowerCase())) return false;
		}
		return true;
	}
}

