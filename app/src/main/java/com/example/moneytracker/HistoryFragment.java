package com.example.moneytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.moneytracker.databinding.FragmentHistoryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    FragmentHistoryBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    int sumExpenses=0;
    int sumIncome=0;
    ArrayList<TransactionModel> transactionModelArrayList;
    TransactionAdapter transactionAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        transactionModelArrayList = new ArrayList<>();

        binding.historyRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.historyRecycleView.setHasFixedSize(true);

        loadData();
        return view;

    }

    private void loadData() {
        firebaseFirestore.collection("Transaction")
                .document(firebaseAuth.getUid())
                .collection("Notes")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot ds:task.getResult()) {
                            TransactionModel model = new TransactionModel(
                                    ds.getString("id"),
                                    ds.getString("note"),
                                    ds.getString("amount"),
                                    ds.getString("type"),
                                    ds.getString("date"));


                            int amount=Integer.parseInt(ds.getString("amount"));
                            if (ds.getString("type").equals("Expenses")) {
                                sumExpenses = sumExpenses + amount;
                            }
                            else {
                                sumIncome = sumIncome + amount;
                            }
                            transactionModelArrayList.add(model);
                        }
                        binding.totalIncomeHistory.setText(String.valueOf(sumIncome));
                        binding.totalExpenseHistory.setText(String.valueOf(sumExpenses));
                        binding.totalBalanceHistory.setText(String.valueOf(sumIncome-sumExpenses));

                        transactionAdapter = new TransactionAdapter(getActivity(),transactionModelArrayList);
                        binding.historyRecycleView.setAdapter(transactionAdapter);
                    }
                });
    }
}