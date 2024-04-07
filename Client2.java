import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server.");

            // Thread to read messages from the server
            Thread readerThread = new Thread(() -> {
                String message;
                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readerThread.start();

            // Send messages to the server
            String message;
            while (true) {
                message = scanner.nextLine();
                writer.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
