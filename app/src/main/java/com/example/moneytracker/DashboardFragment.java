package com.example.moneytracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pieChart = view.findViewById(R.id.chart);
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("Transaction")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Notes")
                .whereEqualTo("category", null);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                Map<String, Integer> categoryTotal = new HashMap<>();

                for (DocumentSnapshot document : documents) {
                    String category = document.getString("category");
                    int total = document.getDouble("amount").intValue();

                    categoryTotal.put(category, categoryTotal.getOrDefault(category, 0) + total);
                }

                ArrayList<PieEntry> entries = new ArrayList<>();
                for (Map.Entry<String , Integer> entry : categoryTotal.entrySet()) {
                    entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }

                PieDataSet dataSet = new PieDataSet(entries, "Category");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                PieData pieData = new PieData(dataSet);
                pieChart.setData(pieData);
                pieChart.invalidate(); // รีเฟรชแผนภูมิ
            }
        });

        return view;
    }
}