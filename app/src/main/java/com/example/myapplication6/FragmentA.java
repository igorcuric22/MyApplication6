package com.example.myapplication6;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentA extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);

        // Find TextView
        TextView textViewData = view.findViewById(R.id.textViewData);

        // Retrieve stored data
        String ip = LocalStorage.getData(requireContext(), "IP");
        String name = LocalStorage.getData(requireContext(), "name");
        String password = LocalStorage.getData(requireContext(), "password"); // Not recommended for security
        String avatar = LocalStorage.getData(requireContext(), "avatar");


        // Show stored data
        String displayText = "IP: " + (ip != null ? ip : "Not found") +
                             "\nName: " + (name != null ? name : "Not found") +
                             "\nPassword: " + (password != null ? password : "Not found")+
                             "\nAvatar: " + (avatar != null ? avatar : "Not found");

        textViewData.setText(displayText);

        return view;
    }
}
