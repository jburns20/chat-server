
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient extends JFrame implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -572467277434891252L;
	// GUI stuff
    private JEditorPane  enteredText = new JEditorPane();
    private JTextField typedText   = new JTextField(32);

    // socket for connection to chat server
    private Socket socket;

    // for writing to and reading from the server
    private Out out;
    private In in;

    public ChatClient(String hostName, String port) {
    	String template = "";
    	try {
			template = new Scanner(new FileReader("template.html")).useDelimiter("\\Z").next();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        enteredText.setContentType("text/html");
    	enteredText.setText(template.toString());
    	enteredText.getCaret().setVisible(false);
        try {
            socket = new Socket(hostName, Integer.parseInt(port));
            out    = new Out(socket);
            in     = new In(socket);
        }
        catch (Exception ex) { ex.printStackTrace(); }

        // close output stream  - this will cause listen() to stop and exit
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    out.close();
//                    in.close();
//                    try                   { socket.close();        }
//                    catch (Exception ioe) { ioe.printStackTrace(); }
                }
            }
        );


        // create GUI stuff
        enteredText.setEditable(false);
        enteredText.setBackground(Color.LIGHT_GRAY);
        typedText.addActionListener(this);

        Container content = getContentPane();
        content.add(new JScrollPane(enteredText), BorderLayout.CENTER);
        content.add(typedText, BorderLayout.SOUTH);


        // display the window, with focus on typing box
        setTitle("Chat Client 1.0: [" + hostName + ":" + port + "]");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        typedText.requestFocusInWindow();
        setVisible(true);

    }

    // process TextField after user hits Enter
    public void actionPerformed(ActionEvent e) {
        out.println(typedText.getText());
        typedText.setText("");
        typedText.requestFocusInWindow();
    }

    // listen to socket and print everything that server broadcasts
    public void listen() {
        String s;
        while ((s = in.readLine()) != null) {
        	if(s.charAt(0)==(char)7) {
        		//play sound
        		new EasySound("chime.wav").play();
        		s=s.substring(1);
        	}
        	try {
				((HTMLDocument)enteredText.getDocument()).insertBeforeEnd(((HTMLDocument)enteredText.getDocument()).getElement("BODY"), s + "\n");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
            //enteredText.setText(enteredText.getText()+s + "\n");
            enteredText.setCaretPosition(enteredText.getDocument().getLength());
        }
        out.close();
        in.close();
        try                 { socket.close();      }
        catch (Exception e) { e.printStackTrace(); }
        System.err.println("Closed client socket");
    }

    public static void main(String[] args)  {
        ChatClient client = new ChatClient(args[0], args[1]);
        client.listen();
    }
}
