package com.natalie.handy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {
    //Variables
    private EditText et_full_name, et_email_address, et_phone_number, et_password;
    private DatabaseReference reference;
    private CheckBox conditions;
    private long maxId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_full_name = findViewById(R.id.full_name);
        et_email_address = findViewById(R.id.email);
        et_phone_number = findViewById(R.id.phone);
        et_password = findViewById(R.id.password);
        conditions = findViewById(R.id.conditions);

        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    maxId = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Boolean validateName() {
        String val_full_name = et_full_name.getText().toString();
        if (val_full_name.isEmpty()) {
            et_full_name.setError("Field cannot be empty");
            return false;
        } else {
            et_full_name.setError(null);
            et_full_name.setEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val_email_address = et_email_address.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val_email_address.isEmpty()) {
            et_email_address.setError("Field cannot be empty");
            return false;
        } else if (!val_email_address.matches(emailPattern)) {
            et_email_address.setError("Invalid email address");
            return false;
        } else {
            et_email_address.setError(null);
            et_email_address.setEnabled(false);
            return true;
        }
    }

    private Boolean validatePhone() {
        String val_phone_number = et_phone_number.getText().toString();
        if (val_phone_number.isEmpty()) {
            et_phone_number.setError("Field cannot be empty");
            return false;
        } else if (et_phone_number.length() < 10 || et_phone_number.length() > 10) {
            et_phone_number.setError("Enter a valid phone number");
            return false;
        } else {
            et_phone_number.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val_password = et_password.getText().toString();
        if (val_password.isEmpty()) {
            et_password.setError("Field cannot be empty");
            return false;
        } else if (val_password.length() < 8) {
            et_password.setError("Password should have at least 8 characters");
            return false;
        } else {
            et_password.setError(null);
            et_password.setEnabled(false);
            return true;
        }
    }

    public void register(View view) {

        if (!validateName() || !validateEmail() || !validatePhone() || !validatePassword() || !conditions.isChecked()) {
            Toast condition = Toast.makeText(RegistrationActivity.this, "Please Accept Terms and Conditions", Toast.LENGTH_SHORT);
            condition.show();
            return;
        }
        //Get all the values from the edit texts
        String full_name = et_full_name.getText().toString();
        String email_address = et_email_address.getText().toString();
        int phone_number = Integer.parseInt(et_phone_number.getText().toString().trim());
        String password = et_password.getText().toString();

        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("phone_number").equalTo(phone_number);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check if phone number is unique
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(RegistrationActivity.this, "Phone number already exists", Toast.LENGTH_SHORT).show();
                if(snapshot.getValue(String.class) !=null) {
                    String key = snapshot.getKey();
                    if (key.equals("Full name")) {
                        ;


                        String full_name = snapshot.getValue(String.class);
                        et_full_name.setText(full_name);
                    }
                    if ( key.equals("Email Address"));{
                    String email_address =snapshot.getValue(String.class);
                    et_email_address.setText(email_address);}
                    if (key.equals("Phone Number"));{
                    String phone_number = snapshot.getValue(String.class);
                    et_phone_number.setText(phone_number);}
                    if (key.equals("Password"));{
                    String password = snapshot.getValue(String.class);
                    et_password.setText((password));}
                }




                } else {
                    //send email link to user
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Insert values to database
                                            UserHelperClass helperClass = new UserHelperClass(full_name, email_address, phone_number,password);
                                            reference.child(String.valueOf(maxId + 1)).setValue(helperClass);

                                            et_full_name.setText("Full name");
                                            et_email_address.setText("Email Address");
                                            et_phone_number.setText("Phone Number");
                                            et_password.setText("Password");

                                            Toast register = Toast.makeText(RegistrationActivity.this, "Successful Registration", Toast.LENGTH_SHORT);
                                            register.show();

                                            Intent myIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                            startActivity(myIntent);
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }


        });

    }
    //open sign in activity
    public void sign_in(View view) {
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
    }

}