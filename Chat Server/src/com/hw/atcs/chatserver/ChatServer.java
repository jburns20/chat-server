package com.hw.atcs.chatserver;

import java.util.LinkedList;

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
	
	public ChatServer(int port) {
		this.port = port;
		this.userThreads = new LinkedList<UserThread>();
	}
}

