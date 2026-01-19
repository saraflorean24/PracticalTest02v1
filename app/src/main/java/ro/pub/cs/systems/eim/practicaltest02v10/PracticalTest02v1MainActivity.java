package ro.pub.cs.systems.eim.practicaltest02v10;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practicaltest02v10.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v10.network.ServerThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {
    private EditText serverPortEditText;
    private EditText clientPortEditText;
    private EditText clientAddressEditText;
    private EditText wordEditText;
    private Button serverConnectButton;
    private TextView autocompleteTextView;
    private ServerThread serverThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(android.view.View view) {
            String serverPort = serverPortEditText.getText().toString();
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Connecting to server...", Toast.LENGTH_SHORT).show();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Could not create server thread!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v1_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        wordEditText = findViewById(R.id.client_word_edit_text);

        serverConnectButton = findViewById(R.id.connect_button);
        serverConnectButton.setOnClickListener(connectButtonClickListener);

//        getAnagramsButton = findViewById(R.id.get_anagrams_button);
//        getAnagramsButton.setOnClickListener(getAnagramsButtonClickListener);

        autocompleteTextView = findViewById(R.id.autocomplete_text_view);
        wordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    new ClientThread(
                            clientAddressEditText.getText().toString(),
                            Integer.parseInt(clientPortEditText.getText().toString()),
                            query,
                            autocompleteTextView
                    ).start();
                } else {
                    autocompleteTextView.setText("");
                }
            }
        });
    }
}