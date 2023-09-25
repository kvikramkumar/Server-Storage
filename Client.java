import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.net.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
public class Client implements ActionListener,ItemListener
{
	JFrame loginFrame,signupFrame,fileFrame;
	
	//Sign Up Form Components
	
	JTextField userNameField1;
	JPasswordField passwordField1,passwordField2;
	JCheckBox showPassword1;
	JButton signup,log_in;

	//Login Form Components
	
	JTextField userNameField2;
	JPasswordField passwordField3;
	JCheckBox showPassword2;
	JButton login,signin;
	
	//FileWindow Form Components
	JButton exit,download,upload,delete;
	DefaultListModel<String> listModel;
	JList<String> fileList;


	JPanel p1,p2;	
	JScrollPane listScrollPane;	
	String fileNames[];

	PrintWriter out=null;
	BufferedReader in = null;
	InputStream inputStream = null;
	OutputStream outputStream = null;
	String message,response,selected = null;
	Socket socket = null;

	public Client()
	{
		try 
		{
            		// Connect to server on port 8080
            		socket = new Socket("localhost", 8080);
            		System.out.println("Connected to server");
	            	
					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					outputStream = socket.getOutputStream();
					inputStream = socket.getInputStream();

        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        }
		
		signUpWindow();

	}
	public void putFileNames(String s[])
	{
		for(String fname : s)
		{
			listModel = new DefaultListModel<>();
			listModel.addElement(fname);
		}
	}
	public String[] getFileNames()
	{
		return fileNames;	
	}
    public static void main(String[] args) 
	{
		new Client();	
    }
	void loginWindow()
	{
		loginFrame = new JFrame("Login Window");
		loginFrame.setSize(500,500);
		loginFrame.setLayout(null);
		loginFrame.setBackground(Color.BLUE);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel userLogin,userid,pwd;
		
		userLogin = new JLabel("USER LOGIN");
		userLogin.setBounds(220,100,100,20);
		loginFrame.add(userLogin);

		userid = new JLabel("User ID           :");
		userid.setBounds(120,150,100,20);
		loginFrame.add(userid);
		
		userNameField2 = new JTextField();
		userNameField2.setBounds(230,150,150,20);
		loginFrame.add(userNameField2);
		
		pwd = new JLabel("Password      :");
		pwd.setBounds(120,200,100,20);
		loginFrame.add(pwd);
		
		passwordField3 = new JPasswordField();
		passwordField3.setBounds(230,200,150,20);
		loginFrame.add(passwordField3);
		
		showPassword2 = new JCheckBox("Show Password");
		showPassword2.setBounds(230,230,200,20);
		loginFrame.add(showPassword2);
		showPassword2.addItemListener(this);
		

		login = new JButton("Login");
		login.setBounds(130,300,100,25);
		loginFrame.add(login);
		login.addActionListener(this);

		signin = new JButton("Sign in");
		signin.setBounds(270,300,100,25);
		loginFrame.add(signin);
		signin.addActionListener(this);
		
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setVisible(true);



		loginFrame.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent e) 
			{
                	// Display a confirmation dialog
                	int option = JOptionPane.showConfirmDialog(loginFrame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                	if (option == JOptionPane.YES_OPTION) 
					{
                			// Close the frame
						try
						{
							out.println("exit");
							socket.close();
						}
						catch(IOException ex)
						{
							ex.printStackTrace();
						}
                    	loginFrame.dispose();
                    	System.exit(0);
                	}
            }
        });

	}
	void fileWindow()
	{
		fileFrame = new JFrame("File Window");
		fileFrame.setSize(500,500);
		fileFrame.setLayout(new BorderLayout());

		JLabel title = new JLabel("FILES IN SERVER");
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 18));
		title.setForeground(Color.RED);
		

		exit = new JButton("Exit");
		exit.addActionListener(this);

		download = new JButton("Download");
		download.addActionListener(this);

		upload = new JButton("Upload");
		upload.addActionListener(this);

		delete = new JButton("Delete");	
		delete.addActionListener(this);

		fileList = new JList<>(listModel);

		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) 
			{
                // Get the selected item and display it in the label
                selected = fileList.getSelectedValue();
            }
        });

		listScrollPane = new JScrollPane(fileList);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fileFrame.add(listScrollPane, BorderLayout.CENTER);

		p1 = new JPanel();
		p1.setLayout(new GridLayout(1,3,10,20));
		p1.add(upload);
		p1.add(download);
		p1.add(delete);
		fileFrame.add(p1,BorderLayout.SOUTH);
		
		p2 = new JPanel(new BorderLayout());
		p2.add(exit,BorderLayout.EAST);
		p2.add(title,BorderLayout.CENTER);
		title.setHorizontalAlignment(JLabel.CENTER);
		fileFrame.add(p2,BorderLayout.NORTH);

		fileFrame.setLocationRelativeTo(null);
		fileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileFrame.setVisible(true);

		fileFrame.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent e) 
			{
                	// Display a confirmation dialog
                	int option = JOptionPane.showConfirmDialog(fileFrame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                	if (option == JOptionPane.YES_OPTION) 
					{
                			// Close the frame
						try
						{
							out.println("exit");
							socket.close();
						}
						catch(IOException ex)
						{
							ex.printStackTrace();
						}
                    	fileFrame.dispose();
                    	System.exit(0);
                	}
            }
        });


	}	
	public void signUpWindow()
	{
		signupFrame = new JFrame("Sign UP");
		signupFrame.setSize(500,500);
		signupFrame.setLayout(null);

		JLabel sign,userid,pwd1,pwd2;

		sign = new JLabel("SIGN UP");
		sign.setBounds(220,80,100,20);
		signupFrame.add(sign);

		userid = new JLabel("User ID                        :");
		userid.setBounds(100,130,150,20);
		signupFrame.add(userid);
		
		userNameField1 = new JTextField();
		userNameField1.setBounds(230,130,150,20);
		signupFrame.add(userNameField1);
		
		pwd1 = new JLabel("New Password         :");
		pwd1.setBounds(100,180,150,20);
		signupFrame.add(pwd1);
		
		passwordField1 = new JPasswordField();
		passwordField1.setBounds(230,180,150,20);
		signupFrame.add(passwordField1);
		
		pwd2 = new JLabel("Confirm Password   :");
		pwd2.setBounds(100,230,150,20);
		signupFrame.add(pwd2);
		
		passwordField2 = new JPasswordField();
		passwordField2.setBounds(230,230,150,20);
		signupFrame.add(passwordField2);
		
		
		showPassword1 = new JCheckBox("Show Password");
		showPassword1.setBounds(230,260,200,20);
		signupFrame.add(showPassword1);
		showPassword1.addItemListener(this);

		
		signup = new JButton("Sign up");
		signup.setBounds(110,320,100,25);
		signupFrame.add(signup);
		signup.addActionListener(this);

		log_in = new JButton("Login");
		log_in.setBounds(260,320,100,25);
		signupFrame.add(log_in);
		log_in.addActionListener(this);
		
		signupFrame.setLocationRelativeTo(null);
		signupFrame.setVisible(true);
		
		signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		signupFrame.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent e) 
			{
                		// Display a confirmation dialog
                	int option = JOptionPane.showConfirmDialog(signupFrame, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                	if (option == JOptionPane.YES_OPTION) 
					{
                    	// Close the frame
						try
						{
							out.println("exit");
							socket.close();
						}
						catch(IOException ex)
						{
							ex.printStackTrace();
						}
                    	signupFrame.dispose();
                		System.exit(0);
                	}
            	}
        });

	}
	public void actionPerformed(ActionEvent ae)
	{
		
		if(ae.getSource() == signup)
		{
			response="";
			if(!passwordField1.getText().equals("") && passwordField1.getText().equals(passwordField2.getText()))
			{	
				message = "register:"+userNameField1.getText()+","+passwordField1.getText();
			
				out.println(message);
				try
				{
					response = in.readLine();
				} catch(IOException e){ e.printStackTrace(); }
				if(response.equals("fail"))
				{
					JOptionPane.showMessageDialog(signupFrame,"USER ID Already Available","Alert",JOptionPane.WARNING_MESSAGE); 	
				}
				else
				{
					System.out.println("Sign IN Completed");
					signupFrame.dispose();
					loginWindow();
				}
			}
			else
			{
				JOptionPane.showMessageDialog(signupFrame,"Password Mismatch","Alert",JOptionPane.WARNING_MESSAGE);
			}
		}
		if(ae.getSource() == log_in)
		{
			signupFrame.dispose();
			loginWindow();
		}
		if(ae.getSource() == login)
		{
			message = "login:"+userNameField2.getText()+","+passwordField3.getText();
			out.println(message);

			try
			{
				response = in.readLine();
			} catch(IOException e){ e.printStackTrace(); }
			if(response.equals("fail"))
			{
				JOptionPane.showMessageDialog(loginFrame,"Invalid ID or Password!","Alert",JOptionPane.WARNING_MESSAGE); 
			}
			if(response.equals("ok"))
			{
				try
				{
						ObjectInputStream objin = new ObjectInputStream(socket.getInputStream());				
            			String[] files = (String[]) objin.readObject();
				
						listModel = new DefaultListModel<>();
						
            			for (String fileName : files) 
						{
							 listModel.addElement(fileName);
            			}
						System.out.println("Login Process Completed");
				} catch(Exception e) { e.printStackTrace(); }
				loginFrame.dispose();
				fileWindow();
			}
		}
		if(ae.getSource() == upload)
		{

			JFileChooser fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(fileFrame);

        	// check if the user selected a file
        	if (returnVal == JFileChooser.APPROVE_OPTION) 
			{
				try
				{
					File file = fileChooser.getSelectedFile();
					
					String fname = file.getName();
					Long fileSize = file.length();

					message = "upload:"+fname+"@"+fileSize;
					out.println(message);


					FileInputStream fileInputStream = new FileInputStream(file);
    				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

					byte[] buffer = new byte[4096];
    				int bytesRead = 0;
    				while ((bytesRead = bufferedInputStream.read(buffer)) != -1) 
					{
        				outputStream.write(buffer, 0, bytesRead);
    				}
					outputStream.flush();
    				bufferedInputStream.close();

					response = in.readLine();
					if(response.equals("ok"))
					{
						System.out.println("File Sent Successfully");
						JOptionPane.showMessageDialog(fileFrame,"File Uploaded Successfully");
						listModel.addElement(fname);
					}	
					
					
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
			}
				
		}
		if(ae.getSource() == delete)
		{
			if(selected != null)
			{
				int option = JOptionPane.showConfirmDialog(fileFrame, "Are you sure you want to delete?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) 
				{
					message = "delete:"+selected;
					out.println(message);
					try{
						response = in.readLine();
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
					if(response.equals("ok"))
					{
						System.out.println("File Deleted");
						JOptionPane.showMessageDialog(fileFrame,"File Deleted Successfully");
						listModel.removeElement(selected);
					} 
				
				}

			}
			else
			{
				JOptionPane.showMessageDialog(fileFrame,"Please select the File","Alert",JOptionPane.WARNING_MESSAGE);
			}

		}
		if(ae.getSource() == download)
		{
			try
			{
				if(selected != null)
				{
					message = "download:"+selected;
					out.println(message);
	
					File file = new File("./"+selected);
					FileOutputStream fileOutputStream = new FileOutputStream(file); 
					Long fileSize = Long.parseLong(in.readLine());
	
					byte[] buffer = new byte[4096];
					int bytesRead = 0;
					long totalBytesRead = 0;
					while (totalBytesRead < fileSize && (bytesRead = inputStream.read(buffer)) != -1) 
					{
							fileOutputStream.write(buffer, 0, bytesRead);
							totalBytesRead += bytesRead;
					}
					fileOutputStream.flush();
					fileOutputStream.close();

					System.out.println("File received from Server");
					JOptionPane.showMessageDialog(fileFrame,"File Downloaded Successfully");
					out.println("ok");
					
				}
				else
				{
					JOptionPane.showMessageDialog(fileFrame,"Please select the File","Alert",JOptionPane.WARNING_MESSAGE);
				}
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}	
		if(ae.getSource() == exit)
		{
			out.println("exit");
			try{
				out.close();
				in.close();
				inputStream.close();
				outputStream.close();
				socket.close();
			}
			catch(IOException e)
			{
				System.out.println("Error :" + e.getMessage());
			}
			System.exit(0);
		}
		if(ae.getSource() == signin)
		{
			loginFrame.dispose();
			signUpWindow();
		}
	}
	public void itemStateChanged(ItemEvent e) 
	{
		if(e.getSource() == showPassword1)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				passwordField1.setEchoChar((char) 0);
				passwordField2.setEchoChar((char) 0);
			} 	
			else
			{
                passwordField1.setEchoChar('\u2022');
				passwordField2.setEchoChar('\u2022');
			}
		}
		if(e.getSource() == showPassword2)
		{
            if (e.getStateChange() == ItemEvent.SELECTED) 
                passwordField3.setEchoChar((char) 0);
			else
                passwordField3.setEchoChar('\u2022');
        }
    }
	public void valueChanged(ListSelectionEvent event) 
	{
		// Get the selected item and display it in the label
		selected = fileList.getSelectedValue();
	}
}
