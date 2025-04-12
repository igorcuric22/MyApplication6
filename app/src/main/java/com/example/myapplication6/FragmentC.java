package com.example.myapplication6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.MenuInflater;


import android.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.URISyntaxException;
import java.time.LocalDate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.request.transition.Transition;

import java.util.regex.*;


public class FragmentC extends Fragment {
    private Socket socket;
    private EditText editText;
    private TextView textView;
    // private Button button;

    private ImageView imageView;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private String ip;

    
    private static final String TAG = "SocketIO";

    private ImageView avatarImage;

    private boolean isValidIP(String ip) {
        // Check if IP is in format: http://xxx.xxx.xxx.xxx OR http://localhost
        return ip.matches("^(http:\\/\\/localhost|http:\\/\\/\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})$");
    }
    
    private String extractIP(String text) {
        // Regex pattern to match valid IPv4 addresses
        String ipRegex = "\\b(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

        // Compile the pattern
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(text);

        // Return the first matching IP address found
        if (matcher.find()) {
            return matcher.group();
        }
        
        // Return null or an empty string if no IP is found
        return null; // or return "" for an empty string
    }
   

    public void addMessageBubble(JSONObject messageJson, String ime) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View bubbleView;
    
        boolean isUser = false;
        String imageUrl = "", avatarUrl = "";
    
        try {
            String newMessagex = messageJson.getString("name");
            isUser = !newMessagex.equals(ime);
            imageUrl = messageJson.optString("image", ""); // Get image URL if available
            avatarUrl = messageJson.optString("avatar", ""); // Get avatar URL if available
        } catch (JSONException e) {
            Log.e(TAG, "Error extracting message fields", e);
        }
    
        // Inflate the correct bubble layout
        bubbleView = inflater.inflate(isUser ? R.layout.bubble_user : R.layout.bubble_guest, null);
    
        // Set message text
        TextView messageText = bubbleView.findViewById(R.id.bubble_text);
        ImageView avatarImage = bubbleView.findViewById(R.id.bubble_avatar); // Avatar ImageView
        ImageView messageImage = bubbleView.findViewById(R.id.bubble_image); // Message ImageView
    
        try {
            String newMessage = messageJson.getString("message");
            String name = messageJson.getString("name");
            messageText.setText(newMessage + " - " + name+" - "+imageUrl);
        } catch (JSONException e) {
            Log.e(TAG, "Error extracting message fields", e);
            messageText.setText("Error: Invalid message format");
        }
    
        // Load avatar image
        loadImage(avatarUrl, avatarImage);
    
        // Load received image (only if available)
        if (!imageUrl.isEmpty()) {
            messageImage.setVisibility(View.VISIBLE);
            loadImage(imageUrl, messageImage);
        } else {
            messageImage.setVisibility(View.GONE);
        }
    
        // Set alignment dynamically
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = isUser ? Gravity.END : Gravity.START;
        bubbleView.setLayoutParams(params);
    
        // Add bubble to message container
        LinearLayout messageContainer = getView().findViewById(R.id.message_container);
        messageContainer.addView(bubbleView);
    
        // Auto-scroll to latest message
        ScrollView scrollView = getView().findViewById(R.id.message_scroll);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

  
    private void loadImage(String url, ImageView imageView) {
        if (url.contains("localhost")) {
            // url = url.replace("localhost", "10.0.2.2"); // Replace localhost for emulator compatibility
            String extractedIP = extractIP(ip);

            url = url.replace("localhost", extractedIP);

            Log.d(TAG, "Received: " + url);
            
        }
    
        Log.d(TAG, "Loading image from URL: " + url);
        Glide.with(this)
            .load(url)
            .apply(new RequestOptions()
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(imageView);
    }
   
    
    
    String name="";
    String password="";
    String avatar="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c, container, false);

        // textView = view.findViewById(R.id.about_text);
        editText = view.findViewById(R.id.about_edit_text);
        Button button = view.findViewById(R.id.about_button);

      

        View footer = view.findViewById(R.id.footer_layout);

        imageView = view.findViewById(R.id.imageView);



        Button buttonSelect = view.findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(v -> openImagePicker());

         // Clear the ImageView after uploading
         imageView.setImageDrawable(null); // Remove the displayed image
         imageUri = null; // Clear the image URI

        // Retrieve stored data
        ip = LocalStorage.getData(requireContext(), "IP");
        name = LocalStorage.getData(requireContext(), "name");
        password = LocalStorage.getData(requireContext(), "password"); // Not recommended 
        avatar = LocalStorage.getData(requireContext(), "avatar");

        if (ip == null || !isValidIP(ip)) { 
            ip = "http://10.0.2.2"; // Default local server IP
            LocalStorage.saveData(getContext(), "IP", ip);
        }

        String SERVER_URL = ip + ":3000";

        try {
            
            socket = IO.socket(SERVER_URL);
            socket.connect();

             // Listen for previous messages
             socket.on("previousMessages", args -> {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    try {

                        JSONArray messages = (JSONArray) args[0];
                        StringBuilder previousMessages = new StringBuilder();
                        for (int i = 0; i < messages.length(); i++) {
                            
                            addMessageBubble(messages.getJSONObject(i),name);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing previous messages: ", e);
                    }
                });
            });
            socket.on("messages", args -> {

                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    try {
                        // args[0] is a JSONObject, not a String
                        JSONObject messageJson = (JSONObject) args[0];

                        String newMessage = messageJson.getString("message");
            
                        imageView.setImageDrawable(null);
                        addMessageBubble(messageJson,name);

                        Log.d(TAG, "Received: " + newMessage);
                    } catch (Exception e) {
                        Log.e(TAG, "Error receiving new message: ", e);
                    }
                });
            });

            // Handle connection errors
            socket.on("connect_error", args -> {
                Log.e(TAG, "Socket connection failed: " + args[0]);
            });


            button.setOnClickListener(v -> {
                String message = editText.getText().toString();
                if (!message.isEmpty()) {
                    Toast.makeText(getActivity(), "Message Sent!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Received: " + socket.toString());
            
                    try {
                        JSONObject data = new JSONObject();
                        data.put("message", message);
                        data.put("name", name);
                        data.put("avatar", avatar);
            
                        if (imageUri != null) {
                            // If an image is selected, encode and send it
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();
                            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            data.put("image", encodedImage);
                        } else {
                            // No image selected, set "image" field to null
                            // data.put("image", JSONObject.NULL);
                            data.put("image", "");
                        }
            
                        socket.emit("sendMessages", data);
            
                        // Clear the image after sending
                        imageView.setImageDrawable(null);
                        imageUri = null;
                        imageView.setVisibility(View.GONE);
            
                    } catch (Exception e) {
                        Log.e(TAG, "JSON Exception: " + e.getMessage());
                    }
            
                    // Hide the keyboard after sending the message
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }
            });
            
            
        } catch (URISyntaxException e) {
            LocalStorage.saveData(requireContext(), "IP", "http://10.0.2.2");
            Log.e(TAG, "Socket Connection Error: " + e.getMessage());
            
        }

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    @Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PICK_IMAGE_REQUEST && resultCode == requireActivity().RESULT_OK && data != null) {
        imageUri = data.getData();
        try (InputStream imageStream = requireActivity().getContentResolver().openInputStream(imageUri)) {
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(selectedImage);
        } catch (Exception e) {
            Log.e(TAG, "Image Display Error: ", e);
        }
    }
}


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (socket != null) {
            socket.off("previousMessagess");
            socket.off("messages");
            socket.disconnect();
        }
    }

   

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Enable options menu in Fragment C
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); // Clear previous menu items
        inflater.inflate(R.menu.menu_fragment_c, menu); // Inflate Fragment C menu
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            LocalStorage.removeData(requireContext(), "IP");
            LocalStorage.removeData(requireContext(), "name");
            LocalStorage.removeData(requireContext(), "password"); // ⚠️ Not secure

            // Navigate back to Fragment B on logout
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new FragmentB(), "Fragment B", true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

