package com.example.moneytracker;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
    Spinner spinner;
    EditText editText;
    Button button, delBtn;
    ArrayList<String> spinnerList;
    ArrayAdapter<String> adapter;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        spinner = findViewById(R.id.spinner);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        delBtn = findViewById(R.id.button2);
        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        spinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(CategoryActivity.this, android.R.layout.simple_spinner_dropdown_item,spinnerList);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cate = editText.getText().toString().trim();

                if (!cate.isEmpty()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("category", cate);

                    fStore.collection("Transaction")
                        .document(firebaseAuth.getUid())
                        .collection("Category")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                editText.setText("");
                                adapter.notifyDataSetChanged();
                            }
                        })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(CategoryActivity.this, "Name mustn't Empty", Toast.LENGTH_SHORT).show();
                }


            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cate = editText.getText().toString().trim();

                if (!cate.isEmpty()) {
                    // ค้นหาเอกสารที่มีค่า category เท่ากับ cate
                    fStore.collection("Transaction")
                            .document(firebaseAuth.getUid())
                            .collection("Category")
                            .whereEqualTo("category", cate)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        // ลบเอกสารที่ตรงกับเงื่อนไข
                                        document.getReference().delete();
                                    }

                                    editText.setText("");
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(CategoryActivity.this, "Data Deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(CategoryActivity.this, "Name mustn't Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        spinner.setAdapter(adapter);
        showData();
    }

    private void showData() {
        fStore.collection("Transaction").document(firebaseAuth.getUid())
                .collection("Category").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                spinnerList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String value = document.getString("category");
                    spinnerList.add(value);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}