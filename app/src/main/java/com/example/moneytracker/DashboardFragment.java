package com.example.moneytracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private PieChart pieChart;
    private ListView listView;
    private ArrayList<String> categoryList;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        pieChart = view.findViewById(R.id.chart);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        listView = view.findViewById(R.id.categoryHistory);

        categoryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, categoryList);
        listView.setAdapter(adapter);

        //query from firebase
        firebaseFirestore.collection("Transaction")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Notes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                    if (!documents.isEmpty()) {
                        Map<String, Integer> categoryTotal = new HashMap<>();

                        for (DocumentSnapshot document : documents) {
                            String category = document.getString("category");
                            int amount = Integer.parseInt(document.getString("amount"));

                            if(category != null) {
                                categoryTotal.put(category, categoryTotal.getOrDefault(category, 0) + amount);
                            }
                        }

                        ArrayList<PieEntry> entries = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : categoryTotal.entrySet()) {
                            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                        }

                        PieDataSet dataSet = new PieDataSet(entries, "Category");
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        PieData pieData = new PieData(dataSet);

                        pieChart.getDescription().setEnabled(false);
                        pieChart.setData(pieData);
                        pieChart.invalidate();
                    } else {

                    }
                })
                .addOnFailureListener(e -> {

                });

        showData();

        return view;
    }

    //show category
    private void showData() {
        firebaseFirestore.collection("Transaction").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Category")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categoryList.clear();
                        categoryList.add("Food");
                        categoryList.add("Shopping");
                        categoryList.add("Transportation");
                        categoryList.add("Monthly Payment");

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String value = document.getString("category");
                            categoryList.add(value);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}