package model.process.fuzzOptions;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class SimulatedServer extends Thread {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(10030)) {
            try (Socket client = serverSocket.accept()) {
                handleClient(client);
            }
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) throws IOException {
        try (DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            out.write("response".getBytes());
            out.flush();
        }
    }
}
