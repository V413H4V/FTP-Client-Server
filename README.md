# FTP-Client-Server

#### Multi-threaded server with FTP GUI client, RPC and Dropbox like auto-sync service.
===============================================================================
#### This server supports 2 modes of operation:
##### •	Single Threaded
##### •	Multi Threaded

  - ##### To Run the program in single threaded mode, specify the following parameters:
  - ` java -jar Server.jar 4444 singleThread `
  #####
  - ##### For Multi-threaded mode, use following command: parameters:
  - ` java -jar Server.jar 4444 multiThread `
This command starts server on Port 4444
  #####

- ##### In multithreaded mode server creates a new thread for each new client. Once a new thread is assigned to the client, servers keeps listening on the same port for more incoming connections.
######
- ##### Client GUI is built with Java and being platform-independent, it can run on any system (Linux, Windows, Mac). To run the client software, you can just double click the jar file: Client.jar…. or You can run it through the terminal with following command:

- `	java -jar Client.jar`
######
![Client GUI](Client-GUI.PNG?raw=true)
######
- ##### 	In the GUI, first enter the address of server (ex.  0.0.0.0) and the port number (ex. 4444)
######
- ##### 	Click on “Connect” button to connect to the server. As soon as you connect to the server, the file list from the server will be displayed.
######
- ##### 	To download a file, select the file from the file-list and Click on “Download” button.
######
- ##### 	To upload a file, click on “Upload” button. It will open the local file-browser. Select the file to be uploaded and it will upload the file.
- Once the file is uploaded, you can click on “Refresh” button, to refresh the file-list.
######
- ##### 	To delete a file, select a file in the file-list and click on “Delete” button.
 ######
- ##### 	To Rename a file, select the file from the file-list and click on “Rename” button. It will open up a pop-up window. Put the new name and click Ok. It will rename the selected file to the new name.
######
- ##### 	“Refresh” button will refresh the file-list and show all the file present on the server and "Disconnect” button will disconnect the client from the server.
######
-For all the RPCs, the parameters need to be specified the Text Box provided at the bottom of the GUI.
######
- ##### 	CALCULATE_PI: This button will fetch the calculated value of Pi with 100000 iterations (based on Leibniz formula for π), from the server and display it in the result textbox.
######
- ##### 	ADD (i , j ): specify the parameters i and j as 2 comma separated values, in the textbox. Ex. 2,3
######
- ##### 	SORT(Array): Specify the elements of the array as comma separated values in the textbox. Ex. 4,2,0,9,1,5
######
- ##### 	Matrix Multiplication: Specify the parameters as:
- All the element in a row should be separated by a “space”.
- All rows should be separated by a “comma”.
- Each Matrix should be separated by a “Semicolon”.
- Ex. For two 3x3 matrices:
	5 2 4,6 4 9,1 8 6;4 2 3,5 4 1,8 6 9
- ##### This Matrix Multiplication supports any number of matrices.
######
- ##### The File-syncing thread runs in the background and checks for any newly added or recently modified file in the Client’s home directory and syncs it with the server.
Please note that, the file-syncing operation is scheduled to run every 10 seconds. Also, the file-syncing feature will only be available for the server running in Multi-Threaded mode.
