package com.example.mealplannerapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealplannerapp.adapters.ChatAdapter;
import com.example.mealplannerapp.models.ChatMessage;
import com.example.mealplannerapp.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextInputLayout questionLayout;
    private EditText etQuestion;
    private ImageButton btnSend;

    private List<ChatMessage> messages;
    private ChatAdapter adapter;

    private TextToSpeech tts;

    private static final String API_KEY = "";
    private static final int REQ_CODE_SPEECH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        recyclerView = findViewById(R.id.recyclerChat);
        questionLayout = findViewById(R.id.questionLayout);
        etQuestion = findViewById(R.id.etQuestion);
        btnSend = findViewById(R.id.btnSend);
        etQuestion.setOnEditorActionListener((v, actionId, event) -> {
            btnSend.performClick();
            return true;
        });
        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        messages.add(new ChatMessage(
                "👋 Welcome!\n\n" +
                        "I'm your AI Food & Fitness Coach.\n\n" +
                        "Try asking:\n" +
                        "• Create a 1500 calorie meal plan\n" +
                        "• Best breakfast for weight loss\n" +
                        "• Home workout for beginners\n" +
                        "• High-protein vegetarian meals",
                false));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        questionLayout.setEndIconOnClickListener(v -> startVoiceInput());
        // 🔊 Text to Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        // Send button
        btnSend.setOnClickListener(v -> {
            String question = etQuestion.getText().toString().trim();

            if (!question.isEmpty()) {

                messages.add(new ChatMessage(question, true));

                adapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(messages.size() - 1);

                callGemini(question);

                etQuestion.setText("");
            }
        });
    }

    // 🎤 Voice input
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH && resultCode == RESULT_OK) {
            ArrayList<String> result =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String text = result.get(0);
            etQuestion.setText(text);
        }
    }

    // 🤖 GEMINI API CALL
    private void callGemini(String question) {

        OkHttpClient client = new OkHttpClient();
        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + API_KEY;
        try {

            JSONObject textPart = new JSONObject();
            textPart.put("text",
                    "You are a certified Food and Fitness Coach AI. " +
                            "Give short, clear, useful answers.\n\nUser: " + question);

            JSONArray parts = new JSONArray();
            parts.put(textPart);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            JSONObject bodyJson = new JSONObject();
            bodyJson.put("contents", contents);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        messages.add(new ChatMessage("NETWORK ERROR: " + e.getMessage(), false));
                        adapter.notifyDataSetChanged();
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String bodyString = response.body() != null ? response.body().string() : "";

                    if (!response.isSuccessful()) {

                        runOnUiThread(() -> {
                            messages.add(new ChatMessage(
                                    "API FAILED: " + response.code() + "\n" + bodyString,
                                    false));
                            adapter.notifyDataSetChanged();
                        });

                        return;
                    }

                    try {

                        JSONObject json = new JSONObject(bodyString);

                        String answer = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> {

                            typeMessage(answer);

                            tts.speak(answer,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "gemini");
                        });

                    } catch (Exception e) {

                        runOnUiThread(() -> {
                            messages.add(new ChatMessage(
                                    "PARSE ERROR: " + e.getMessage() + "\nRAW: " + bodyString,
                                    false));
                            adapter.notifyDataSetChanged();
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✨ Typing animation
    private void typeMessage(String fullText) {

        messages.add(new ChatMessage("", false));
        int position = messages.size() - 1;

        adapter.notifyItemInserted(position);

        new Thread(() -> {

            for (int i = 1; i <= fullText.length(); i++) {

                String current = fullText.substring(0, i);

                runOnUiThread(() -> {

                    messages.set(position, new ChatMessage(current, false));
                    adapter.notifyItemChanged(position);

                    recyclerView.scrollToPosition(position);
                });

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
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