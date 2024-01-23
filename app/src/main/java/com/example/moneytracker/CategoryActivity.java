package com.example.moneytracker;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {
    private EditText editText;
    private Button addButton, deleteButton;
    private ListView listView;
    private ArrayList<String> categoryList;
    private ArrayAdapter<String> adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        editText = findViewById(R.id.editText);
        addButton = findViewById(R.id.button);
        deleteButton = findViewById(R.id.button2);
        listView = findViewById(R.id.list1);

        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        categoryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryList);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory();
            }
        });

        showData();
    }

    private void addCategory() {
        String category = editText.getText().toString().trim();

        if (!category.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("category", category);
            data.put("timestamp", FieldValue.serverTimestamp());

            fStore.collection("Transaction")
                    .document(firebaseAuth.getUid())
                    .collection("Category")
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            editText.setText("");
                            showData();
                            Toast.makeText(CategoryActivity.this, "Category Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CategoryActivity.this, "Category name mustn't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCategory() {
        String category = editText.getText().toString().trim();

        if (!category.isEmpty()) {
            fStore.collection("Transaction")
                    .document(firebaseAuth.getUid())
                    .collection("Category")
                    .whereEqualTo("category", category)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                document.getReference().delete();
                            }

                            editText.setText("");
                            showData();
                            Toast.makeText(CategoryActivity.this, "Category Deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(CategoryActivity.this, "Category name mustn't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        fStore.collection("Transaction").document(firebaseAuth.getUid())
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