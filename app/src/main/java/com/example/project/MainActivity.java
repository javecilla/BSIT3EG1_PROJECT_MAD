package com.example.project;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnShowBottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnShowBottomSheet = findViewById(R.id.setFocusGoalButton);

        btnShowBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
    }

    private void showBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_card_create, null);
        bottomSheetDialog.setContentView(sheetView);

        TextInputEditText etSubject = sheetView.findViewById(R.id.etSubject);
        TextInputEditText etTask = sheetView.findViewById(R.id.etTask);
        TextInputEditText etDuration = sheetView.findViewById(R.id.etDuration);
        MaterialButton btnClose = sheetView.findViewById(R.id.btnClose);
        MaterialButton btnConfirmGoal = sheetView.findViewById(R.id.btnConfirmGoal);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnConfirmGoal.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String task = etTask.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            if (subject.isEmpty() || task.isEmpty() || duration.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            bottomSheetDialog.dismiss(); // Close first sheet

            // Show second BottomSheet with read-only fields
            showReadOnlyBottomSheet(subject, task, duration);
        });

        bottomSheetDialog.show();
    }

    private void showReadOnlyBottomSheet(String subject, String task, String duration) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_card_active, null);
        bottomSheetDialog.setContentView(sheetView);

        TextInputEditText etSubjectReadonly = sheetView.findViewById(R.id.etSubject);
        TextInputEditText etTaskReadonly = sheetView.findViewById(R.id.etTask);
        TextInputEditText etDurationReadonly = sheetView.findViewById(R.id.etDuration);
        MaterialButton btnMarkDoneCreateNew = sheetView.findViewById(R.id.btnMarkDoneCreateNew);

        // Set the fields
        etSubjectReadonly.setText(subject);
        etTaskReadonly.setText(task);
        etDurationReadonly.setText(duration);

        btnMarkDoneCreateNew.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Optionally: reopen the first bottom sheet to create a new goal
            showBottomSheet();
        });

        bottomSheetDialog.show();
    }
}
