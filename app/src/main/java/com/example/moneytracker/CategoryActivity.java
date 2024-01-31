package com.example.moneytracker;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
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
            if (category.equalsIgnoreCase("Food") || category.equalsIgnoreCase("Shopping") || category.equalsIgnoreCase("Transportation") || category.equalsIgnoreCase("Monthly Payment")) {
                Toast.makeText(CategoryActivity.this, "This category already exists", Toast.LENGTH_SHORT).show();
            } else {
                fStore.collection("Transaction")
                        .document(firebaseAuth.getUid())
                        .collection("Category")
                        .whereEqualTo("category", category)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(CategoryActivity.this, "This category already exists", Toast.LENGTH_SHORT).show();
                                } else {
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
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CategoryActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(CategoryActivity.this, "Category name mustn't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteCategory() {
        String category = editText.getText().toString().trim();

        // Check if the category is a default category
        if (category.equals("Food") || category.equals("Shopping") || category.equals("Transportation") || category.equals("Monthly Payment")) {
            Toast.makeText(CategoryActivity.this, "Can't delete Default", Toast.LENGTH_SHORT).show();
        } else {
            // Create AlertDialog to confirm deletion
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("This action will delete all associated transaction. Are you sure to delete this category? ")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteNotes(category);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    private void deleteNotes(String category) {
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

                            // Also delete associated notes
                            fStore.collection("Transaction")
                                    .document(firebaseAuth.getUid())
                                    .collection("Notes")
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
                                            Toast.makeText(CategoryActivity.this, "Category and all associated transaction deleted", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CategoryActivity.this, "Error deleting associated notes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CategoryActivity.this, "Error deleting category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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