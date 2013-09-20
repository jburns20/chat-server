package com.hw.atcs.chatserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import java.io.*;

public class UserThread implements Runnable {
	
	private String name;
	private Socket socket;
	private ChatServer chatServer;
	private PrintWriter out;
	private BufferedReader in;
	private Color color;

	public UserThread(ChatServer server, Socket s) {
		socket = s;
		chatServer=server;
		int[] rgb=new int[3];
		int sum=(int)(Math.random()*200)+200;
		ArrayList<Integer> indexes=new ArrayList<Integer>(Arrays.asList(0,1,2));
		for(int i=0; i<2; i++) {
			int index=indexes.get((int)(Math.random()*indexes.size()));
			indexes.remove(new Integer(index));
			int component=Math.max(Math.min((int)(Math.random()*255), sum), sum-510);
			sum-=component;
			rgb[index]=component;
		}
		rgb[indexes.get(0)]=Math.min(255, sum);
		System.out.println(rgb[0] + " " + rgb[1] + " " + rgb[2]);
		color=new Color(rgb[0], rgb[1], rgb[2]);
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
				String newName = this.in.readLine();
				if (newName==null) return;
				newName = newName.replace(' ', '_');
				if (!chatServer.nicknameExists(newName)) this.name = newName;
				else out.println("Someone else is using that nickname. Please try again.");
			} catch (IOException e) {
				e.printStackTrace();
				out.println("There was a problem reading your nickname. Please try again.");
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
						String newName=message.substring(command.length()+2).replace(' ', '_');
						if (!chatServer.nicknameExists(newName)) {
							chatServer.sendMessage(null, name + " changed their nickname to "+newName+".");
							name = newName;
						} else if (name.equals(newName)) {
							out.println("You're already using that nickname.");
						} else {
							out.println("Someone else is using that nickname. Please try again.");
						}
					}
					else if(command.equals("disconnect")) {
						if(message.indexOf(" ") != -1)
							chatServer.sendMessage(null, name+" is disconnecting: "+message.substring(command.length()+2));
						chatServer.removeUserThread(this);
						out.close();
						in.close();
						return;
					} else if (command.equals("whisper")) {
						String user = message.substring(command.length()+2, message.indexOf(" ", command.length()+2));
						String text = message.substring(message.indexOf(" ", command.length()+2)+1);
						chatServer.whisperMessage(this, user, text);
					}
				} else {
					chatServer.sendMessage(this, message);
				}
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
	
	public Color getColor() {
		return color;
	}
}
