package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mealplannerapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    CardView cardMeal, cardFitness, cardReminder;

    FloatingActionButton voiceBtn;
    Animation pulse;
    ImageView reminderIcon;
    BottomSheetDialog bottomSheet;
    CardView cardAI;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Cards
        cardMeal = findViewById(R.id.cardMeal);
        cardFitness = findViewById(R.id.cardFitness);
        reminderIcon = findViewById(R.id.reminderIcon);
        cardAI = findViewById(R.id.cardAI);

        // Navigation
        cardMeal.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        cardFitness.setOnClickListener(v ->
                startActivity(new Intent(this, FitnessActivity.class)));

        reminderIcon.setOnClickListener(v ->
                startActivity(new Intent(this, ReminderActivity.class)));
        cardAI.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            HomeActivity.this,
                            AiChatActivity.class
                    )
            );
        });
        // Voice Button
        voiceBtn = findViewById(R.id.voiceBtn);

        // Animation
        pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);

        voiceBtn.setOnClickListener(v -> {
            speak("What can I help you with?");
            showListeningSheet();
            voiceBtn.startAnimation(pulse);

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());

            startActivityForResult(intent, 100);
        });

        // TTS INIT
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    // 🎤 Bottom Sheet
    private void showListeningSheet() {
        bottomSheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_listening, null);
        bottomSheet.setContentView(view);
        bottomSheet.show();
    }

    private void hideListeningSheet() {
        if (bottomSheet != null) bottomSheet.dismiss();
    }

    // 🧠 Voice Commands
    private void handleVoiceCommand(String text) {

        text = text.toLowerCase().trim();

        int dietScore = 0;
        int fitnessScore = 0;
        int reminderScore = 0;

        // 🍽️ DIET INTENT
        if (text.contains("diet") || text.contains("meal") || text.contains("food")) dietScore += 2;
        if (text.contains("plan") || text.contains("today") || text.contains("eat")) dietScore += 1;

        // 💪 FITNESS INTENT
        if (text.contains("fitness") || text.contains("workout") || text.contains("exercise")) fitnessScore += 2;
        if (text.contains("gym") || text.contains("run")) fitnessScore += 1;

        // 🔔 REMINDER INTENT
        if (text.contains("reminder") || text.contains("notify") || text.contains("alert")) reminderScore += 2;
        if (text.contains("set") || text.contains("schedule")) reminderScore += 1;

        // 🎯 DECIDE BEST INTENT
        if (dietScore >= fitnessScore && dietScore >= reminderScore && dietScore > 0) {

            speak("Preparing your diet plan for today");
            startActivity(new Intent(this, MainActivity.class)
                    .putExtra("mode", "today_diet"));

        } else if (fitnessScore >= dietScore && fitnessScore >= reminderScore && fitnessScore > 0) {

            speak("Opening your fitness plan");
            startActivity(new Intent(this, FitnessActivity.class));

        } else if (reminderScore > 0) {

            speak("Opening reminders");
            startActivity(new Intent(this, ReminderActivity.class));

        } else {

            speak("I’m not sure what you mean. Try saying: plan my diet for today");
        }
    }

    // 🔊 Speak
    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // 🎤 Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        hideListeningSheet();
        voiceBtn.clearAnimation();

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = result.get(0);
            speak("Let me check that for you...");
            handleVoiceCommand(spokenText);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}