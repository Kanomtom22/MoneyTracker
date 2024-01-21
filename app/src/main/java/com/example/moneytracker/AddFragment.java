package com.example.moneytracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moneytracker.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String type="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        binding.incomeTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Income";
                binding.incomeTransaction.setChecked(true);
                binding.expensesTransaction.setChecked(false);
            }
        });

        binding.expensesTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Expenses";
                binding.expensesTransaction.setChecked(true);
                binding.incomeTransaction.setChecked(false);
            }
        });

        binding.cancelTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back latest screen
                /*Navigation.findNavController(v).navigateUp();*/
                Navigation.findNavController(v).navigate(R.id.fragment_home);
            }
        });

        binding.saveTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = binding.amountTransaction.getText().toString().trim();
                String note = binding.noteTransaction.getText().toString().trim();

                if (amount.length() <=0 ) {
                    return;
                }
                if (type.length() <= 0){
                    Toast.makeText(getContext(), "Select transaction type", Toast.LENGTH_SHORT).show();
                    return;
                }

                String id = UUID.randomUUID().toString();
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id",id);
                transaction.put("amount",amount);
                transaction.put("note",note);
                transaction.put("type",type);

                fStore.collection("Transaction")
                        .document(firebaseAuth.getUid())
                        .collection("Notes")
                        .document(id)
                        .set(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Added new transaction", Toast.LENGTH_SHORT).show();
                                binding.noteTransaction.setText("");
                                binding.amountTransaction.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return view;
    }
}