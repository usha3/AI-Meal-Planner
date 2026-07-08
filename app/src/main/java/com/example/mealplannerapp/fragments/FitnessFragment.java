package com.example.mealplannerapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mealplannerapp.R;
import com.example.mealplannerapp.adapters.FitnessAdapter;
import com.example.mealplannerapp.models.FitnessItem;

import java.util.ArrayList;
import java.util.List;

public class FitnessFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    private String type;

    private RecyclerView recyclerFitness;
    private FitnessAdapter adapter;
    private List<FitnessItem> fitnessList;

    // ---------------- NEW INSTANCE ----------------

    public static FitnessFragment newInstance(String type) {

        FitnessFragment fragment = new FitnessFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);

        fragment.setArguments(args);

        return fragment;
    }

    // ---------------- ON CREATE ----------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    // ---------------- ON CREATE VIEW ----------------

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_fitness,
                container,
                false
        );
    }

    // ---------------- ON VIEW CREATED ----------------

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerFitness = view.findViewById(R.id.recyclerFitness);

        // 2 items per row
        recyclerFitness.setLayoutManager(
                new GridLayoutManager(getContext(), 2)
        );

        fitnessList = new ArrayList<>();

        // ---------------- EXERCISES ----------------

        if (type.equals("Exercise")) {

            fitnessList.add(new FitnessItem(
                    "Push Ups",
                    "https://images.unsplash.com/photo-1517836357463-d25dfeac3438",
                    "10 mins",
                    "Exercise",
                    "IODxDxX7oi4",
                    "Improves chest, shoulder and arm strength.",
                    "Start in plank position. Lower your body slowly and push back upward."
            ));

            fitnessList.add(new FitnessItem(
                    "Running",
                    "https://images.unsplash.com/photo-1483721310020-03333e577078",
                    "30 mins",
                    "Exercise",
                    "brFHyOtTwH4",
                    "Burns calories and improves heart health.",
                    "Run at a comfortable pace while maintaining proper posture."
            ));

            fitnessList.add(new FitnessItem(
                    "Squats",
                    "https://images.unsplash.com/photo-1518611012118-696072aa579a",
                    "15 mins",
                    "Exercise",
                    "aclHkVaku9U",
                    "Strengthens legs and glutes.",
                    "Stand straight, lower hips slowly, then return to standing."
            ));

            fitnessList.add(new FitnessItem(
                    "Plank",
                    "https://images.unsplash.com/photo-1517963879433-6ad2b056d712",
                    "5 mins",
                    "Exercise",
                    "pSHjTRCQxIw",
                    "Builds core strength.",
                    "Keep your body straight while balancing on forearms and toes."
            ));
        }

        // ---------------- YOGA ----------------

        else {

            fitnessList.add(new FitnessItem(
                    "Morning Yoga",
                    "https://images.unsplash.com/photo-1506126613408-eca07ce68773",
                    "20 mins",
                    "Yoga",
                    "v7AYKMP6rOE",
                    "Improves flexibility and relaxation.",
                    "Sit comfortably and begin with breathing exercises."
            ));

            fitnessList.add(new FitnessItem(
                    "Meditation Yoga",
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb",
                    "15 mins",
                    "Yoga",
                    "inpok4MKVLM",
                    "Reduces stress and improves focus.",
                    "Sit in a calm place and focus on deep breathing."
            ));

            fitnessList.add(new FitnessItem(
                    "Sun Salutation",
                    "https://images.unsplash.com/photo-1518611012118-fb0b1b4976f7",
                    "25 mins",
                    "Yoga",
                    "8AakYeM23sI",
                    "Improves flexibility and blood circulation.",
                    "Perform 12 yoga poses in a sequence with controlled breathing."
            ));

            fitnessList.add(new FitnessItem(
                    "Lotus Pose",
                    "https://images.unsplash.com/photo-1515377905703-c4788e51af15",
                    "10 mins",
                    "Yoga",
                    "4pKly2JojMw",
                    "Calms the mind and improves posture.",
                    "Sit cross-legged with feet placed on opposite thighs."
            ));
        }

        // ---------------- ADAPTER ----------------

        adapter = new FitnessAdapter(getContext(), fitnessList);

        recyclerFitness.setAdapter(adapter);
    }
}