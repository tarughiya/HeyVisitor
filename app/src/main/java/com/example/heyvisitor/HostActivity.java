package com.example.heyvisitor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.heyvisitor.Model.Host;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class HostActivity extends AppCompatActivity {
    EditText name, email, phone;
    Button hostbutton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // [END get_firestore_instance]

    // [START set_firestore_settings]
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

//    FirebaseDatabase database;
//    DatabaseReference hostdatabase;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        hostbutton = findViewById(R.id.hostbutton);

//        database = FirebaseDatabase.getInstance();
//        hostdatabase = database.getReference();

        hostbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name = name.getText().toString().trim();
                final String Email = email.getText().toString().trim();
                final String Number =(phone.getText().toString());

                if (Name.isEmpty() || Email.isEmpty()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(HostActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Fill up all the fields!");
                    alert.show();
                } else {

                    Map<String, Object> host = new HashMap<>();
                    host.put("Name", Name);
                    host.put("Email", Email);
                    host.put("Number",Number);

                    db.collection("Host")
                            .add(host)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(HostActivity.this);
                                    alert.setTitle("Success");
                                    alert.setMessage("Host added successfully!");
                                    alert.show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

//                    hostdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Host host = new Host(Name, Email, Number);
//                            hostdatabase.child(Name).setValue(host);
//

//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }
        });

    }

}