package model.process.fuzzing;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("HardCodedStringLiteral")
public class SimulatedServer extends Thread {

    private static void handleClient(Socket client) throws IOException {
        try (DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            out.write("response".getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    @SuppressWarnings("RefusedBequest")
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(10020); Socket client = serverSocket.accept()) {
            handleClient(client);
        } catch (EOFException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
