package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;

    public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("SERVER THREAD", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }


    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("SERVER THREAD", "[SERVER THREAD] Waiting for a client invocation...");
                // Accept client connections and handle them
                Socket socket = serverSocket.accept();
                Log.i("SERVER THREAD", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e("SERVER THREAD", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("SERVER THREAD", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
