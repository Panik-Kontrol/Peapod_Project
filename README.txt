Peapod project
There are two main() executables, Sender.java and Receiver.java. After compiling, simply run Sender and then Receiver.
Currently, the sender will deposit a message to the server and it's located under the folder server/ in a file named after the sender.
This contains the senders name along with all the entries associated with the message, including m, g^r, tuple messages (keys encrypted) and the g^r tuple.

Then you can excecute Receiver and it will attempt to get the message from the server. It's all hard coded right now to sender "Alice" and receiver "Bob".
If it was complete and working passing the message, I would have programmed it to be whatever you want and take some input from the console or something, but I didn't get this far.

The senders/ folder is used by CertAuth to store the x and s. The first line has the senders name and the two keys, x then s. This is actually used for both sender and receiver to save time.
The original intent was to have the sender file with a list of receivers that have "subscribed" to their list, but for simplicity I didn't actually get this working.

The server also drops a server_params file that stores the p, g, private key and public key. All of this is assumed to be used by an account specific to the server and only accessible by the server.
Since we didn't get the completely separate processes going, this is simply within the working directory and under control of the person executing the code. No security is built in here.

The decryption didn't fully work, so this is not a finished product. You will see garbage printed even though the policies and credentials should align. I did not have time to finish debug.
However, the program does run and a simple el-gamal test was ran to see if it decoded with basic el-gamal and this portion works, so the parameter generation is working.