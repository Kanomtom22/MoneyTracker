package com.example.moneytracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moneytracker.databinding.FragmentHistoryBinding;
import com.example.moneytracker.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    int sumExpenses=0;
    int sumIncome=0;
    ArrayList<TransactionModel> transactionModelArrayList;
    TransactionAdapter transactionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        transactionModelArrayList = new ArrayList<>();

        loadData();
        return view;
    }

    private void loadData() {
        firebaseFirestore.collection("Transaction")
                .document(firebaseAuth.getUid())
                .collection("Notes")
                .orderBy("date", Query.Direction.DESCENDING)
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot ds : task.getResult()) {
                            TransactionModel model = new TransactionModel(
                                    ds.getString("id"),
                                    ds.getString("note"),
                                    ds.getString("amount"),
                                    ds.getString("type"),
                                    ds.getString("date"));


                            int amount = Integer.parseInt(ds.getString("amount"));
                            if (ds.getString("type").equals("Expenses")) {
                                sumExpenses = sumExpenses + amount;
                            } else {
                                sumIncome = sumIncome + amount;
                            }
                            transactionModelArrayList.add(model);
                        }


                        binding.totalIncome.setText("฿" + String.valueOf(sumIncome));
                        binding.totalExpenses.setText("฿" +String.valueOf(sumExpenses));
                        binding.totalBaht.setText("฿" +String.valueOf(sumIncome - sumExpenses));

                        transactionAdapter = new TransactionAdapter(getActivity(), transactionModelArrayList);
                    }

                });

        Collections.sort(transactionModelArrayList, new Comparator<TransactionModel>() {
            @Override
            public int compare(TransactionModel model1, TransactionModel model2) {
                // เปรียบเทียบค่า "date"
                int dateComparison = model2.getDate().compareTo(model1.getDate());

                // ถ้า "date" เท่ากัน ให้เปรียบเทียบ "timestamp"
                if (dateComparison == 0) {
                    return dateComparison;
//                    return model2.getTimestamp().compareTo(model1.getTimestamp());
                } else {
                    return dateComparison;
                }
            }
        });
    }
}