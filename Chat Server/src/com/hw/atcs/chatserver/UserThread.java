package com.hw.atcs.chatserver;

import java.net.*;
import java.io.*;
import java.util.ArrayDeque;

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
            System.out.println("IOException: " + e);
        }
	}
	public void run() {
		while (true) {
			try {
				if (name == null) {
					this.out.println("Welcome to the Chat Server");
					this.out.println("Enter your username to begin.");
					name = this.in.readLine();
				} else {
					String message = this.in.readLine();
				}
			} catch (IOException e) {
				//stuff
			}
		}
	}
}
