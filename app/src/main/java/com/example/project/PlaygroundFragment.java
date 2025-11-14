package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Fragment for the PlayGround tab - displays study technique selection cards
 */
public class PlaygroundFragment extends Fragment {
    
    // Technique card views
    private MaterialCardView cardTechnique1, cardTechnique2, cardTechnique3, cardTechnique4, cardTechnique5;
    private MaterialButton btnShowBottomSheet;
    
    // Interface to communicate with MainActivity
    public interface PlaygroundListener {
        void onTechniqueSelected(String technique);
        void onShowBottomSheet();
    }
    
    private PlaygroundListener listener;
    
    public void setPlaygroundListener(PlaygroundListener listener) {
        this.listener = listener;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playground, container, false);
        
        // Initialize technique cards
        cardTechnique1 = view.findViewById(R.id.cardTechnique1);
        cardTechnique2 = view.findViewById(R.id.cardTechnique2);
        cardTechnique3 = view.findViewById(R.id.cardTechnique3);
        cardTechnique4 = view.findViewById(R.id.cardTechnique4);
        cardTechnique5 = view.findViewById(R.id.cardTechnique5);
        btnShowBottomSheet = view.findViewById(R.id.setFocusGoalButton);
        
        // Set click listeners for technique cards
        if (cardTechnique1 != null) {
            cardTechnique1.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechniqueSelected("Pomodoro");
                }
            });
        }
        
        if (cardTechnique2 != null) {
            cardTechnique2.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechniqueSelected("52/17");
                }
            });
        }
        
        if (cardTechnique3 != null) {
            cardTechnique3.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechniqueSelected("90-Minute");
                }
            });
        }
        
        if (cardTechnique4 != null) {
            cardTechnique4.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechniqueSelected("Sprint");
                }
            });
        }
        
        if (cardTechnique5 != null) {
            cardTechnique5.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechniqueSelected("Deadline");
                }
            });
        }
        
        // Set click listener for "Set New Focus Goal" button
        if (btnShowBottomSheet != null) {
            btnShowBottomSheet.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShowBottomSheet();
                }
            });
        }
        
        return view;
    }
    
    /**
     * Gets technique card views (for highlighting selected card from MainActivity)
     */
    public MaterialCardView getCardTechnique1() { return cardTechnique1; }
    public MaterialCardView getCardTechnique2() { return cardTechnique2; }
    public MaterialCardView getCardTechnique3() { return cardTechnique3; }
    public MaterialCardView getCardTechnique4() { return cardTechnique4; }
    public MaterialCardView getCardTechnique5() { return cardTechnique5; }
}

