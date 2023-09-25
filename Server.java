import java.net.*;
import java.io.*;

public class Server 
{
	ServerSocket serverSocket = null;
	Socket clientSocket = null;
	static FileWriter fileWriter;
	public Server()
	{
		try 
		{
            // Create server socket on port 8080
            serverSocket = new ServerSocket(8080);
            System.out.println("Server started, listening on port 8080");
			fileWriter = new FileWriter("Credentials.txt",true);
	
            while (true) 
			{
                		// Wait for client to connect
                		clientSocket = serverSocket.accept();
                		System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                		// Create new thread to handle client connection
                		Thread clientThread = new Thread(new ClientHandler(clientSocket));
                		clientThread.start();
            }
        } 
		catch (IOException e) 
		{
            System.out.println("Error Occured in Socket connection");
			e.printStackTrace();
        }

	}
	static void addCredentials(String cred)
	{
		try
		{
			fileWriter.write(cred);
			fileWriter.flush();
		}
		catch(IOException e)
		{
				System.out.println("Cannot ADD Credentials");
				e.printStackTrace();
		}
	}
	static boolean checkCredentials(String cred)
	{
		boolean flag = false;
		try 
		{
            		BufferedReader reader = new BufferedReader(new FileReader("Credentials.txt"));
            		String line;
            		while ((line = reader.readLine()) != null) 
					{
                			if(line.equals(cred))
							{
								flag = true;	
								break;
                			}
            		}
					reader.close();
		} 
		catch (IOException e) 
		{
            System.out.println("An error occurred while trying to read the file.");
            e.printStackTrace();
		}
		return flag;
	}
	public static void main(String[] args) 
	{
       		new Server();
	}
}

class ClientHandler implements Runnable 
{
	private Socket clientSocket;
	BufferedReader in = null;
	PrintWriter out = null;
	String request,ack;
	String user_id;
    public ClientHandler(Socket clientSocket) throws IOException
	{
        this.clientSocket = clientSocket;
		in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		out = new PrintWriter(this.clientSocket.getOutputStream(), true);
    }

    public void run() 
	{
		String cred[],uid=null,pwd;
		String fileName,fileDetails[];
		Long fileSize;
		try
        {
            while ((request = in.readLine()) != null) 
			{
				
					String[] parts = request.split(":", 2);
                	String command = parts[0].trim();
            		String arguments = "";
            		if (parts.length > 1) 
					{
            			arguments = parts[1].trim();
            		}
					switch(command)
					{
						case "register":
								{
									cred = arguments.split(",",2);
									uid = cred[0].trim();	
									pwd = cred[1].trim();
									
						
									File directory = new File(uid);
        							// create the directory
        							if (!directory.exists()) 
									{
           	 							boolean success = directory.mkdir();
            							if (success) 
										{
                							System.out.println("Directory created for user :" + uid);								
											ack = "ok";
											Server.addCredentials(uid+":"+pwd+"\n");
            							} 
										else 	
										{
                							System.out.println("Failed to create directory.");
            							}
        							} 
									else 
									{
            								System.out.println("Directory already exists.");
											ack = "fail";
        							}
									out.println(ack);
								}
									break;
						case "login":
									{
										cred = arguments.split(",",2);
										uid = cred[0].trim();	
										pwd = cred[1].trim();
						
										if(Server.checkCredentials(uid+":"+pwd))
										{
											out.println("ok");
											user_id = uid;
											File dir = new File(uid);
                							File[] files = dir.listFiles();
                					
											String[] fileNames = new String[files.length];
                							int i = 0;

                							for (File file : files) 
											{
                    							if (file.isFile()) 
												{
                        							fileNames[i++] = file.getName();
                    							}
                							}
                							ObjectOutputStream objout = new ObjectOutputStream(clientSocket.getOutputStream());
                							objout.writeObject(fileNames);
                							objout.flush();
											System.out.println("File list sharing completed");
										}
										else	
											out.println("fail");
						
									}
									break;
						case "upload":
									{
										try
										{
											fileDetails = arguments.split("@",2);
											fileName = fileDetails[0].trim();
											fileSize = Long.parseLong(fileDetails[1].trim());
										
											System.out.println("File : "+fileName);
											
											File file = new File(user_id,fileName);
											FileOutputStream fileOutputStream = new FileOutputStream(file);

											InputStream inputStream = clientSocket.getInputStream();

	
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

											System.out.println("File Received from Client");

											out.println("ok");

										}
										catch(IOException e)
										{
											e.printStackTrace();
										}
										
									}
									break;
						case "download":
									{
										String fname = arguments;
										File file = new File(user_id+"/"+fname);

										FileInputStream fileInputStream = new FileInputStream(file);
    									BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

										OutputStream outputStream = clientSocket.getOutputStream();

										out.println(file.length());
										byte[] buffer = new byte[4096];
    									int bytesRead = 0;
    									while ((bytesRead = bufferedInputStream.read(buffer)) != -1) 
										{
        									outputStream.write(buffer, 0, bytesRead);
    									}
										outputStream.flush();
    									bufferedInputStream.close();

										String response = in.readLine();
										if(response.equals("ok"))
										{
											System.out.println("File Sent Successfully");
										}
									    fileInputStream.close();

									}
									break;
						case "delete":
									{
										String fname = arguments;
										File file = new File(user_id+"/"+fname);

										if (file.exists()) 
										{
											// Delete the file
											boolean deleted = file.delete();
											
											// Check if the deletion was successful
											if (deleted) 
											{
												out.println("ok");
												System.out.println("File deleted successfully.");
											} 
											else 
											{
												out.println("fail");
												System.out.println("Failed to delete file.");
											}
										} 
										else 
										{
											out.println("fail");
											System.out.println("File does not exist.");
										}
									}
									break;
				}

                if (command.equals("exit")) 
				{
                    break; // break out of the loop if "exit" message is received
                }
            }
			System.out.println("Client disconnected.");
        } 
		catch (IOException e) 
		{
			System.err.println("Error: " + e.getMessage());
    	} 
		finally 
		{
			try
			{
        		// close the input stream, socket, and server socket
    			if (in != null) 
				{
					in.close();
            	}
        		if(clientSocket != null) 
				{	
        			clientSocket.close();
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}

