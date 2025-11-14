package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment for the My Activity tab - displays completed study sessions
 * Currently shows empty state (will be implemented in future phases)
 */
public class MyActivityFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
        
        // Empty state is handled in XML layout
        // Future implementation will show completed sessions list
        
        return view;
    }
}

