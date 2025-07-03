package com.example.schedulemedical.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.utils.AuthManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    
    // Notification constants
    private static final String CHANNEL_ID = "schedule_medical_notifications";
    private static final String CHANNEL_NAME = "Medical Notifications";
    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    
    // Broadcast action constants
    public static final String ACTION_NOTIFICATION_RECEIVED = "com.example.schedulemedical.NOTIFICATION_RECEIVED";
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";
    public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";
    public static final String EXTRA_NOTIFICATION_MESSAGE = "notification_message";
    public static final String EXTRA_NOTIFICATION_DATA = "notification_data";
    
    // Socket connection
    private Socket socket;
    private AuthManager authManager;
    private NotificationManager notificationManager;
    private boolean isConnected = false;
    
    // WebSocket URL (should match backend)
    private static final String SOCKET_URL = "http://192.168.1.100:3000"; // Change to your backend URL

    @Override
    public void onCreate() {
        super.onCreate();
        
        authManager = new AuthManager(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        createNotificationChannel();
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification());
        
        Log.d(TAG, "NotificationService created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!authManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, stopping service");
            stopSelf();
            return START_NOT_STICKY;
        }
        
        connectToSocket();
        
        return START_STICKY; // Restart if killed
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null; // This is a started service, not a bound service
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectSocket();
        Log.d(TAG, "NotificationService destroyed");
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for medical appointments and updates");
            channel.enableLights(true);
            channel.enableVibration(true);
            
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Medical Schedule")
                .setContentText("Listening for notifications...")
                .setSmallIcon(R.drawable.ic_notification_bell)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    private void connectToSocket() {
        if (socket != null && socket.connected()) {
            Log.d(TAG, "Socket already connected");
            return;
        }
        
        try {
            // Configure socket options
            IO.Options options = new IO.Options();
            options.timeout = 10000;
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 1000;
            
            // Add authentication
            String accessToken = authManager.getAccessToken();
            if (accessToken != null) {
                options.auth.put("token", accessToken);
            }
            
            // Create socket connection
            socket = IO.socket(SOCKET_URL, options);
            
            setupSocketListeners();
            socket.connect();
            
            Log.d(TAG, "Attempting to connect to socket: " + SOCKET_URL);
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid socket URL", e);
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to socket", e);
        }
    }
    
    private void setupSocketListeners() {
        if (socket == null) return;
        
        socket.on(Socket.EVENT_CONNECT, args -> {
            isConnected = true;
            Log.d(TAG, "Socket connected successfully");
            
            // Join user room for personalized notifications
            Integer userId = authManager.getUserId();
            if (userId != null) {
                socket.emit("join", userId.toString());
                Log.d(TAG, "Joined user room: " + userId);
            }
        });
        
        socket.on(Socket.EVENT_DISCONNECT, args -> {
            isConnected = false;
            Log.d(TAG, "Socket disconnected");
        });
        
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            isConnected = false;
            Log.e(TAG, "Socket connection error: " + (args.length > 0 ? args[0].toString() : "Unknown"));
        });
        
        socket.on("reconnect", args -> {
            Log.d(TAG, "Socket reconnected");
        });
        
        // Listen for appointment notifications
        socket.on("appointment_notification", args -> {
            if (args.length > 0) {
                handleNotification("appointment", args[0]);
            }
        });
        
        // Listen for payment notifications
        socket.on("payment_notification", args -> {
            if (args.length > 0) {
                handleNotification("payment", args[0]);
            }
        });
        
        // Listen for general notifications
        socket.on("general_notification", args -> {
            if (args.length > 0) {
                handleNotification("general", args[0]);
            }
        });
        
        // Listen for chat messages
        socket.on("message_notification", args -> {
            if (args.length > 0) {
                handleNotification("message", args[0]);
            }
        });
    }
    
    private void handleNotification(String type, Object data) {
        try {
            Log.d(TAG, "Received notification - Type: " + type + ", Data: " + data.toString());
            
            // Parse notification data
            NotificationData notificationData = parseNotificationData(type, data);
            
            if (notificationData != null) {
                // Show system notification
                showSystemNotification(notificationData);
                
                // Broadcast to app
                broadcastNotification(notificationData);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling notification", e);
        }
    }
    
    private NotificationData parseNotificationData(String type, Object data) {
        // For simplicity, assuming data is a string with format "title|message"
        // In a real implementation, you would parse JSON data
        
        String dataStr = data.toString();
        String[] parts = dataStr.split("\\|");
        
        String title = "Medical Schedule";
        String message = dataStr;
        
        if (parts.length >= 2) {
            title = parts[0];
            message = parts[1];
        }
        
        // Customize based on notification type
        switch (type) {
            case "appointment":
                title = "Appointment Update";
                break;
            case "payment":
                title = "Payment Notification";
                break;
            case "message":
                title = "New Message";
                break;
            case "general":
            default:
                title = "Medical Schedule";
                break;
        }
        
        return new NotificationData(type, title, message, dataStr);
    }
    
    private void showSystemNotification(NotificationData data) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_NOTIFICATION_TYPE, data.type);
        intent.putExtra(EXTRA_NOTIFICATION_DATA, data.rawData);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            (int) System.currentTimeMillis(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(data.title)
                .setContentText(data.message)
                .setSmallIcon(R.drawable.ic_notification_bell)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build();
        
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
    
    private void broadcastNotification(NotificationData data) {
        Intent broadcastIntent = new Intent(ACTION_NOTIFICATION_RECEIVED);
        broadcastIntent.putExtra(EXTRA_NOTIFICATION_TYPE, data.type);
        broadcastIntent.putExtra(EXTRA_NOTIFICATION_TITLE, data.title);
        broadcastIntent.putExtra(EXTRA_NOTIFICATION_MESSAGE, data.message);
        broadcastIntent.putExtra(EXTRA_NOTIFICATION_DATA, data.rawData);
        
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
    
    private void disconnectSocket() {
        if (socket != null) {
            socket.off(); // Remove all listeners
            socket.disconnect();
            socket = null;
        }
        isConnected = false;
    }
    
    // Helper method to check connection status
    public boolean isConnected() {
        return isConnected && socket != null && socket.connected();
    }
    
    // Static method to start the service
    public static void startService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
    
    // Static method to stop the service
    public static void stopService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        context.stopService(intent);
    }
    
    // Inner class for notification data
    private static class NotificationData {
        String type;
        String title;
        String message;
        String rawData;
        
        NotificationData(String type, String title, String message, String rawData) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.rawData = rawData;
        }
    }
} 