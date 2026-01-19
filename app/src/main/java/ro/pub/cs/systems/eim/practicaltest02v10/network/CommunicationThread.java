package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }
    @Override
    public void run() {
        // Implement the communication logic here
        if (socket == null) {
            Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Socket is null!");
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            Log.d("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Waiting for word from client...");
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] Error receiving word from client!");
                return;
            }

            String result = getData(word);
            printWriter.print(result);
            printWriter.flush();

            bufferedReader.close();
            printWriter.close();
            socket.close();

        } catch (IOException e) {
            Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("COMMUNICATION THREAD", "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }

    private String getData(String word) {
        try {
            OkHttpClient httpClient = new OkHttpClient();

            String url =
                    "https://www.google.com/complete/search?client=chrome&q=" +
                            java.net.URLEncoder.encode(word, "UTF-8");



            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = httpClient.newCall(request).execute();
            String autocomplete = response.body().string();

            Log.d("COMMUNICATION THREAD",
                    "[COMMUNICATION THREAD] Received autocompletes: " + autocomplete);

            JSONArray rootArray = new JSONArray(autocomplete);

            JSONArray suggestionsArray = rootArray.getJSONArray(1);

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < suggestionsArray.length(); i++) {
                result.append(suggestionsArray.getString(i)).append("\n");
            }

            Log.d("COMMUNICATION THREAD",
                    "[COMMUNICATION THREAD] Sending autocompletes to client:\n" + result);

            return result.toString();

        } catch (Exception e) {
            Log.e("COMMUNICATION THREAD",
                    "[COMMUNICATION THREAD] Error: " + e.getMessage());
            return "";
        }
    }

}
