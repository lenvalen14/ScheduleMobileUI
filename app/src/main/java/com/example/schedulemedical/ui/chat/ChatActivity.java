package com.example.schedulemedical.ui.chat;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class ChatActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        setupChatControls();
        handleIntentExtras();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

    private void setupChatControls() {
        // Send message button
//        ImageButton sendButton = findViewById(R.id.btnSend);
//        EditText messageInput = findViewById(R.id.etMessage);
//
//        if (sendButton != null && messageInput != null) {
//            sendButton.setOnClickListener(view -> {
//                String message = messageInput.getText().toString().trim();
//                if (!message.isEmpty()) {
//                    sendMessage(message);
//                    messageInput.setText("");
//                }
//            });
//        }

        // Attach file button
//        ImageButton attachButton = findViewById(R.id.btnAttach);
//        if (attachButton != null) {
//            attachButton.setOnClickListener(view -> {
//                // TODO: Implement file attachment
//                Toast.makeText(this, "File attachment feature coming soon", Toast.LENGTH_SHORT).show();
//            });
//        }
    }

    private void handleIntentExtras() {
        // Xử lý appointment ID nếu có
        int appointmentId = getIntent().getIntExtra(NavigationHelper.EXTRA_APPOINTMENT_ID, -1);
        if (appointmentId != -1) {
            // TODO: Load chat history for this appointment
            Toast.makeText(this, "Loading chat for appointment: " + appointmentId, Toast.LENGTH_SHORT).show();
        }
    }

//    private void sendMessage(String message) {
//        // TODO: Send message to server
//        RecyclerView chatRecyclerView = findViewById(R.id.rvChat);
//        if (chatRecyclerView != null) {
//            // TODO: Add message to chat adapter
//        }
//
//        // TODO: Update chat UI with new message
//        Toast.makeText(this, "Message sent: " + message, Toast.LENGTH_SHORT).show();
//    }
}