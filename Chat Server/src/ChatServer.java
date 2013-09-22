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
		String everyoneMessage = message;
		String senderMessage = message;
		if(sender!=null) {
			Color c=sender.getColor();
			everyoneMessage = new String(new char[]{7}) + "<p><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + sender.getName() + ":</strong> " + message + "</p>";
			senderMessage = "<p class='from-me'><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + sender.getName() + ":</strong> " + message + "</p>";
		}
		for(UserThread t:this.userThreads) {
			if (t==sender) t.receiveMessage(senderMessage);
			else t.receiveMessage(everyoneMessage);
		}
	}
	
	public synchronized void whisperMessage(UserThread sender, String recipient, String message) {
		if(sender.getName().toLowerCase().equals(recipient.toLowerCase())) {
			sender.receiveMessage("<p class='message'>You can't whisper to yourself!</p>");
			return;
		}
		Color c=sender.getColor();
		String message1 = "<p><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + sender.getName() + "</strong> whispers: " + message + "</p>";
		boolean delivered = false;
		for (UserThread t : this.userThreads) {
			if (t.getName().toLowerCase().equals(recipient.toLowerCase())) {
				t.receiveMessage(new String(new char[]{7}) + message1);
				delivered = true;
				break;
			}
		}
		if (delivered) {
			sender.receiveMessage("<p class='from-me'>You whisper to <strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + recipient + "</strong>: " + message + "</p>");
		} else {
			sender.receiveMessage("<p class='message'>There is no user with that nickname.</p>");
		}
	}
	
	public synchronized void addUserThread(UserThread thread) {
		this.userThreads.addLast(thread);
		Color c = thread.getColor();
		sendMessage(null, "<p class='message'><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + thread.getName()+"</strong> has joined the chat server.</p>");
	}
	
	public synchronized void removeUserThread(UserThread thread) {
		this.userThreads.remove(thread);
		Color c = thread.getColor();
		sendMessage(null, "<p class='message'><strong style='color: rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ");'>" + thread.getName()+"</strong> has left.</p>");
	}
	
	public synchronized boolean nicknameExists(String name) {
		for (UserThread t : this.userThreads) {
			if (t.getName().toLowerCase().equals(name.toLowerCase())) return true;
		}
		return false;
	}
}

