import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Set<ClientHandler> clientHandlers;
    private String clientName;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter your name: ");
            this.clientName = in.readLine();
            broadcast("🟢 " + clientName + " joined the chat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clientHandlers) {
            if (client != this) {
                client.out.println(message);
            }
        }
    }

    @Override
    public void run() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                if (msg.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println(clientName + ": " + msg);
                broadcast(clientName + ": " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                broadcast("🔴 " + clientName + " left the chat.");
                clientHandlers.remove(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

