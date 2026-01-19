package ro.pub.cs.systems.eim.practicaltest02v10.network;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final String word;
    private final TextView autocompleteTextView;

    private Socket socket;
    Context context;

    public ClientThread(String address, int port, String word, TextView autocompleteTextView, Context context) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.autocompleteTextView = autocompleteTextView;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            Log.d("ClientThread", "[CLIENT THREAD] Starting client thread...");
            socket = new Socket(address, port);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            printWriter.println(word);
            printWriter.flush();

            Log.d("ClientThread", "[CLIENT THREAD] Sent word: " + word);

            String anagram;

            autocompleteTextView.post(() -> autocompleteTextView.setText("")); // clear înainte
            while ((anagram = bufferedReader.readLine()) != null) {
                final String finalizedAnagram = anagram;
                autocompleteTextView.post(() ->
//                        autocompleteTextView.append(finalizedAnagram + "\n")
                        Toast.makeText(context , finalizedAnagram, Toast.LENGTH_LONG).show()
                );
            }

            // show anagrams in a toast

//            Toast.makeText(context, "Autocomplete results received", Toast.LENGTH_LONG).show();



        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + e.getMessage());
                }
            }
        }
    }

}
