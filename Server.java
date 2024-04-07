import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter writer;
        private String username;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                writer.println("Enter your username:");
                username = reader.readLine();
                clients.put(username, writer);

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.startsWith("@")) {
                        String[] parts = message.split(" ", 2);
                        String recipient = parts[0].substring(1);
                        String personalMessage = parts[1];
                        sendPersonalMessage(recipient, personalMessage);
                    } else {
                        broadcastMessage(username + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    clients.remove(username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            for (PrintWriter writer : clients.values()) {
                writer.println(message);
            }
        }

        private void sendPersonalMessage(String recipient, String message) {
            PrintWriter writer = clients.get(recipient);
            if (writer != null) {
                writer.println("Personal message from " + username + ": " + message);
            } else {
                writer.println("User " + recipient + " not found!");
            }
        }
    }
}
