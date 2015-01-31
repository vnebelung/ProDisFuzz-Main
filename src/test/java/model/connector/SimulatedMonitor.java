package model.connector;

import model.helper.Constants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class SimulatedMonitor extends Thread {

    private int port;
    private Map<String, String> parameters;

    public SimulatedMonitor(int port) {
        super();
        this.port = port;
        parameters = new HashMap<>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!isInterrupted()) {
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                } catch (EOFException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) throws IOException {
        try (DataInputStream in = new DataInputStream(client.getInputStream()); DataOutputStream out = new
                DataOutputStream(client.getOutputStream())) {
            while (!client.isClosed()) {
                StringBuilder stringBuilder = new StringBuilder(3);
                for (int i = 0; i < 3; i++) {
                    stringBuilder.append((char) in.readByte());
                }
                String header = stringBuilder.toString();
                in.readByte();
                stringBuilder = new StringBuilder();
                while (true) {
                    char c = (char) in.readByte();
                    if (c == ' ') {
                        break;
                    } else {
                        stringBuilder.append(c);
                    }
                }
                int length = Integer.valueOf(stringBuilder.toString());
                stringBuilder = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    char c = (char) in.readByte();
                    stringBuilder.append(c);
                }
                String body = stringBuilder.toString();
                switch (header) {
                    case "AYT":
                        out.write(("ROK " + String.valueOf(Constants.RELEASE_NUMBER).length() + " " + Constants
                                .RELEASE_NUMBER).getBytes());
                        out.flush();
                        break;
                    case "GFP":
                        if (parameters.containsKey(body)) {
                            String p = parameters.get(body);
                            out.write(("ROK " + p.length() + " " + p).getBytes());
                            out.flush();
                        } else {
                            out.write(("ERR 3 n/a").getBytes());
                            out.flush();
                        }
                        break;
                    case "SFP":
                        boolean bad = false;
                        String[] pairs = body.split(",");
                        for (String each : pairs) {
                            String[] param = each.split("=");
                            if (param[0].isEmpty()) {
                                bad = true;
                            } else if (param.length == 1) {
                                parameters.remove(param[0]);
                            } else {
                                parameters.put(param[0], param[1]);
                            }
                        }
                        if (bad) {
                            out.write("ERR 21 Not testkey=testvalue".getBytes());
                            out.flush();
                        } else {
                            out.write("ROK 0 ".getBytes());
                            out.flush();
                        }
                        break;
                    case "CTD":
                        Instant now = Instant.now();
                        String newBody;
                        switch (body.length()) {
                            case 1:
                                newBody = "crashed=no,time=" + now;
                                out.write(("ROK " + newBody.length() + " " + newBody).getBytes());
                                out.flush();
                                break;
                            default:
                                newBody = "crashed=yes,time=" + now + "crashcause=test";
                                out.write(("ROK " + newBody.length() + " " + newBody).getBytes());
                                out.flush();
                                break;
                        }
                    default:
                }
            }
        }
    }
}
