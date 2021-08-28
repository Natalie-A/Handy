package com.natalie.handy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Variables
    TextInputEditText et_email_address, et_password;
    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Spinner roleSpinner;
    private String item;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_email_address = findViewById(R.id.email);
        et_password = findViewById(R.id.password);

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        btn_login = findViewById(R.id.btn_login);
        roleSpinner = findViewById(R.id.user_role);

        //fill the spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_label, android.R.layout.simple_spinner_item);
        //Specify the layout for drop-down menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        roleSpinner.setAdapter(adapter);

        //Set an onItemSelectedListener on the spinner object/variable you have created
        if (roleSpinner != null) {
            roleSpinner.setOnItemSelectedListener(this);
        }
    }

    private Boolean validateEmail() {
        String val_email_address = et_email_address.getText().toString();
        if (val_email_address.isEmpty()) {
            et_email_address.setError("Field cannot be empty");
            return false;
        } else {
            et_email_address.setError(null);
            et_email_address.setEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val_password = et_password.getText().toString();
        if (val_password.isEmpty()) {
            et_password.setError("Field cannot be empty");
            return false;
        } else {
            et_password.setError(null);
            et_password.setEnabled(false);
            return true;
        }
    }

    public void sign_up(View view) {
        Intent myIntent = new Intent(this, RegistrationActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Use the method getItemAtPosition() to get the label selected
        item = adapterView.getItemAtPosition(i).toString();
        if (item.equals("Client")) {
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
        } else {
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginHandy();
                }
            });
        }
    }

    private void loginHandy() {
        if (!validateEmail() || !validatePassword()) {
            return;
        } else {
            String email_address = et_email_address.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email_address, password).addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    //if the email has already been verified
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        String currentUserId = firebaseAuth.getCurrentUser().getUid();
                        Query query = FirebaseDatabase.getInstance().getReference().child("handypersons");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(currentUserId)) {
                                    //check if account has been verified
                                    if (snapshot.child(currentUserId).child("accountStatus").getValue(String.class).equals("notVerified")) {
                                        Toast.makeText(LoginActivity.this, "Please wait for your account to be verified", Toast.LENGTH_SHORT).show();
                                    } else if (snapshot.child(currentUserId).child("accountStatus").getValue(String.class).equals("enabled")) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity3.class);
                                        Toast login = Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT);
                                        login.show();
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(LoginActivity.this, "Your account has been disabled", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Something to do
        Toast toast = Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT);
        toast.show();
    }

    //login the client
    public void login() {
        if (!validateEmail() || !validatePassword()) {
            return;
        } else {
            String email_address = et_email_address.getText().toString().trim();
            String password = et_password.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email_address, password).addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    //if the email has already been verified
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //startActivity(new Intent(LoginActivity.this, MainActivity2.class));
                        String currentUserId = firebaseAuth.getCurrentUser().getUid();
                        Query query = FirebaseDatabase.getInstance().getReference().child("clients");
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(currentUserId)) {
                                    if (snapshot.child(currentUserId).child("accountStatus").getValue(String.class).equals("enabled")) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                        Toast login = Toast.makeText(LoginActivity.this, "Successful login", Toast.LENGTH_SHORT);
                                        login.show();
                                        startActivity(intent);
                                    } else
                                        Toast.makeText(LoginActivity.this, "Your account has been disabled", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Please verify your email first", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void forgot_password(View view) {
        View v = inflater.inflate(R.layout.reset_pop, null);
        //start alert dialog
        reset_alert.setTitle("Reset Password ?").setMessage("Please enter your email address to get the password reset link").setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //validate the email address
                EditText email = v.findViewById(R.id.email);
                if (email.getText().toString().isEmpty()) {
                    email.setError("Required Field");
                    return;
                }
                //send the reset link
                firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Cancel", null).setView(v).create().show();
    }
}