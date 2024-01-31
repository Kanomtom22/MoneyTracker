package com.example.moneytracker;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.moneytracker.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddFragment extends Fragment {
    FragmentAddBinding binding;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String type="",currentDate;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerList;

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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        binding.calendarTransaction.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                currentDate = sdf.format(selectedDate.getTime());

            }
        });


        binding.saveTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = binding.amountTransaction.getText().toString().trim();
                String note = binding.noteTransaction.getText().toString().trim();
                String category = binding.categorySpinner.getSelectedItem().toString();



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
                transaction.put("category", category);
                transaction.put("date", currentDate);

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

        CollectionReference collRef = fStore.collection("Transaction")
                .document(firebaseAuth.getUid())
                .collection("Category");

            collRef.orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    List<String> values = new ArrayList<>();
                    values.add("Food");
                    values.add("Shopping");
                    values.add("Transportation");
                    values.add("Monthly Payment");
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.exists()) {
                            String categoryValue  = doc.getString("category"); // replace with the actual field name
                            values.add(categoryValue);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, values);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.categorySpinner.setAdapter(adapter);
                }
            });

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.categorySpinner.setAdapter(adapter);
        return view;
    }
}