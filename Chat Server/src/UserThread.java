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
		this.out.println("<h1>Welcome to the Chat Server!</h1>");
		do {
			this.out.println("<p class='message'>Enter your nickname to begin.</p>");
			try {
				String newName = this.in.readLine();
				if (newName==null) return;
				newName = newName.replace(' ', '_');
				if (!chatServer.nicknameExists(newName)) this.name = newName;
				else out.println("<p class='message'>Someone else is using that nickname. Please try again.</p>");
			} catch (IOException e) {
				e.printStackTrace();
				out.println("<p class='message'>There was a problem reading your nickname. Please try again.</p>");
			}
		} while (name == null);
		chatServer.addUserThread(this);
		Color color = this.getColor();
		this.out.println("<p class='message'><strong>Welcome, <span style='color: rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ");'>" + name + "</span>!</strong></p>");
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
					Color c = this.getColor();
					String command=message.substring(1, message.indexOf(" ")).toLowerCase();
					if(command.equals("nick")) {
						String newName=message.substring(command.length()+2).replace(' ', '_');
						if (!chatServer.nicknameExists(newName)) {
							chatServer.sendMessage(null, "<p class='message'><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + name + "</strong> changed their nickname to <strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + newName+"</strong>.</p>");
							name = newName;
						} else if (name.equals(newName)) {
							out.println("<p class='message'>You're already using that nickname.</p>");
						} else {
							out.println("<p class='message'>Someone else is using that nickname. Please try again.</p>");
						}
					} else if(command.equals("disconnect")) {
						if(message.indexOf(" ") != -1)
							chatServer.sendMessage(null, "<p class='message'><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + name + "</strong> is disconnecting: "+message.substring(command.length()+2) + "</p>");
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
				out.println("<p class='message'>There was a problem reading your message. Please try again.</p>");
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
