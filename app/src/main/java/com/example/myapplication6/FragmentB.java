package com.example.myapplication6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Random;

public class FragmentB extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        
        // Find views
        Button btnGoToFragmentC = view.findViewById(R.id.btn_go_to_fragment_c);
        EditText editText1 = view.findViewById(R.id.editText1);
        EditText editText2 = view.findViewById(R.id.editText2);
        EditText editText3 = view.findViewById(R.id.editText3);

        // Set click listener for the button
        btnGoToFragmentC.setOnClickListener(v -> {
            // Get values from EditText fields
            String value1 = editText1.getText().toString().trim();
            String value2 = editText2.getText().toString().trim();
            String value3 = editText3.getText().toString().trim();

            // Check if all fields are filled
            if (value1.isEmpty() || value2.isEmpty() || value3.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return; // Stop execution if any field is empty
            }


            int randomId = new Random().nextInt(1000) + 1;
            String avatarUrl = "https://picsum.photos/id/" + randomId + "/200/300";

            // Save values to LocalStorage
            LocalStorage.saveData(requireContext(), "IP", value1);
            LocalStorage.saveData(requireContext(), "name", value2); 
            LocalStorage.saveData(requireContext(), "password", value3); // ⚠️ Not secure
            LocalStorage.saveData(requireContext(), "avatar", avatarUrl);

            // Display values in a Toast message
            String message = "Saved:\nIP: " + value1 + "\nName: " + value2 + "\nPassword: " + value3+ "\nAvatar: "+avatarUrl;
            
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

            // Navigate to Fragment C
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new FragmentC(), "Fragment C", false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Restore title and hamburger when returning to Fragment B
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragment(this, "Fragment B", true);
        }
    }
}
