package Peapod_Project;


public class Sender {
	public static void main(String[] args) {
		Server server = new Server();
		System.out.println(server.booger);
		server.connect("Alice");
	}
}
