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
			this.out.println("Enter your nickname to begin.");
			try {
				name = this.in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("There was a problem reading your nickname. Please try again.");
			}
		} while (name == null);
		chatServer.addUserThread(this);
		this.out.println("Welcome, " + name + "!");
		//Extra spacing, etc. for username separation would go here.
		while (true) {
			try {
				String message = this.in.readLine();
				if(message==null) {
					chatServer.removeUserThread(this);
					return;
				}
				else if(message.equals(""))
					return;
				else if(message.charAt(0)=='/') {
					String command=message.substring(1, message.indexOf(" ")).toLowerCase();
					if(command.equals("nick")) {
						name=message.substring(command.length()+2);
						out.println("Nickname changed to "+name+".");
					}
					else if(command.equals("disconnect")) {
						if(command.indexOf(" ")!=-1)
							chatServer.sendMessage(null, name+" is disconnecting: "+command.substring(command.indexOf(" ")+1));
						chatServer.removeUserThread(this);
					}
					return;
				}
				chatServer.sendMessage(this, message);
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
