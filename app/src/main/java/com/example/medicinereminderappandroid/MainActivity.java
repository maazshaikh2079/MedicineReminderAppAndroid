package com.example.medicinereminderappandroid;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.text.format.Time;

public class MainActivity extends AppCompatActivity {

    private EditText medicineNameEditText;
    private TimePicker timePicker;
    private Button setReminderButton;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable checkTimeRunnable = new Runnable() {
        @Override
        public void run() {
            // Get the current time
            Time currentTime = new Time();
            currentTime.setToNow();

            int currentHour = currentTime.hour;
            int currentMinute = currentTime.minute;

            // Check if the current time matches the time set in the TimePicker
            if (timePicker.getCurrentHour() == currentHour && timePicker.getCurrentMinute() == currentMinute) {
                playAlarm();
                showOkDialog(medicineNameEditText.getText().toString());
            } else {
                // Check again after some time
                handler.postDelayed(this, 60000); // Check every minute
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        medicineNameEditText = findViewById(R.id.medicineNameEditText);
        timePicker = findViewById(R.id.timePicker);
        setReminderButton = findViewById(R.id.setReminderButton);

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicineName = medicineNameEditText.getText().toString();
                if (medicineName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
                } else {
                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();
                    Toast.makeText(MainActivity.this, "Reminder set for " + hour + ":" + minute + " for " + medicineName, Toast.LENGTH_SHORT).show();
                    handler.post(checkTimeRunnable); // Start the time checks
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkTimeRunnable); // Stop the time checks
    }

    private void playAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.tone); // Assuming "tone.mp3" is in the raw folder
        mediaPlayer.start();
    }

    private void showOkDialog(String medicineName) {
        final Dialog okDialog = new Dialog(this);
        okDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        okDialog.setContentView(R.layout.dialog_ok);

        TextView medicineNameTextView = okDialog.findViewById(R.id.medicineNameTextView);
        Button okButton = okDialog.findViewById(R.id.okButton);

        // Display the medicine name in the TextView
        medicineNameTextView.setText("Take " + medicineName);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
                okDialog.dismiss();
            }
        });

        okDialog.show();
    }

    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
