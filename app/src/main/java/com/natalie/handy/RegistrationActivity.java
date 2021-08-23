package com.natalie.handy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Variables
    private EditText et_full_name, et_email_address, et_phone_number, et_password, et_location;
    private CheckBox conditions;
    private Spinner roleSpinner;
    private String item, userRole;
    private Button btn_register;
    //Declare an instance of Firebase Authentication
    private FirebaseAuth mAuth;
    //Declare an instance of FireBase Database
    private FirebaseDatabase database;
    //Declare an instance of FireBase Database Reference; details
    // A Database reference is a node in our database, e.g the node users to store user
    private DatabaseReference userDetailsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        et_full_name = findViewById(R.id.full_name);
        et_email_address = findViewById(R.id.email);
        et_phone_number = findViewById(R.id.phone);
        et_password = findViewById(R.id.password);
        et_location = findViewById(R.id.location);
        conditions = findViewById(R.id.conditions);
        btn_register = findViewById(R.id.register);
        roleSpinner = findViewById(R.id.user_role);

        // Initialize an Instance of Firebase Authentication by calling the getInstance() method
        mAuth = FirebaseAuth.getInstance();
        // Initialize an Instance of Firebase Database by calling the getInstance() method
        database = FirebaseDatabase.getInstance();
        //Initialize an Instance of Firebase Database reference by calling the database instance,
        // getting a reference using the get reference() method on the database,and creating a new child node,
        // in our case "Users" where we will store details of registered users
        userDetailsReference = database.getReference();

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

    private Boolean validateLocation() {
        String val_location = et_location.getText().toString();
        if (val_location.isEmpty()) {
            et_location.setError("Field cannot be empty");
            return false;
        } else {
            et_location.setError(null);
            et_location.setEnabled(false);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Use the method getItemAtPosition() to get the label selected
        item = adapterView.getItemAtPosition(i).toString();
        if (item.equals("Client")) {
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    register(item);
                }
            });
        } else {
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerHandy(item);
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

    //register client into the database
    public void register(String role) {

        if (!conditions.isChecked()) {
            Toast condition = Toast.makeText(RegistrationActivity.this, "Please Accept Terms and Conditions", Toast.LENGTH_SHORT);
            condition.show();
            return;
        } else if (!validateName() || !validateEmail() || !validatePhone() || !validatePassword() || !validateLocation()) {
            return;
        }
        //Get all the values from the edit texts
        String full_name = et_full_name.getText().toString();
        String email_address = et_email_address.getText().toString();
        String phone_number = et_phone_number.getText().toString();
        String password = et_password.getText().toString();
        String location = et_location.getText().toString();

        userRole = role;

        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("clients").orderByChild("phone_number").equalTo(phone_number);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check if phone number is unique
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(RegistrationActivity.this, "Phone number already exists", Toast.LENGTH_SHORT).show();
                } else {
                    //send email link to user
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (mAuth.getCurrentUser() != null) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Insert values to database
                                                String user_id = mAuth.getCurrentUser().getUid();
                                                //create a child node database reference to attach the user_id to the users node
                                                UserHelperClass helperClass = new UserHelperClass(full_name, email_address, location, phone_number);
                                                userDetailsReference.child("clients").child(user_id).setValue(helperClass);

                                                et_full_name.setText("");
                                                et_email_address.setText("");
                                                et_phone_number.setText("");
                                                et_password.setText("");
                                                et_location.setText("");

                                                Toast register = Toast.makeText(RegistrationActivity.this, "Successful registration", Toast.LENGTH_SHORT);
                                                register.show();
                                                //open profile activity on successful registration
                                                Intent myIntent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                                                myIntent.putExtra("user_role",userRole);
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
                        }//if registration was not successful
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //register handyperson into the database
    public void registerHandy(String role) {

        if (!conditions.isChecked()) {
            Toast condition = Toast.makeText(RegistrationActivity.this, "Please Accept Terms and Conditions", Toast.LENGTH_SHORT);
            condition.show();
            return;
        } else if (!validateName() || !validateEmail() || !validatePhone() || !validatePassword() || !validateLocation()) {
            return;
        }
        //Get all the values from the edit texts
        String full_name = et_full_name.getText().toString();
        String email_address = et_email_address.getText().toString();
        String phone_number = et_phone_number.getText().toString();
        String password = et_password.getText().toString();
        String location = et_location.getText().toString();

        userRole = role;

        Query phoneQuery = FirebaseDatabase.getInstance().getReference().child("handypersons").orderByChild("phone_number").equalTo(phone_number);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Check if phone number is unique
                if (snapshot.getChildrenCount() > 0) {
                    Toast.makeText(RegistrationActivity.this, "Phone number already exists", Toast.LENGTH_SHORT).show();
                } else {
                    //send email link to user
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (mAuth.getCurrentUser() != null) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Insert values to database
                                                String user_id = mAuth.getCurrentUser().getUid();
                                                //create a child node database reference to attach the user_id to the users node
                                                UserHelperClass helperClass = new UserHelperClass(full_name, email_address, location, phone_number);
                                                userDetailsReference.child("handypersons").child(user_id).setValue(helperClass);
                                                userDetailsReference.child("handypersons").child(user_id).child("accountStatus").setValue("notVerified");

                                                et_full_name.setText("");
                                                et_email_address.setText("");
                                                et_phone_number.setText("");
                                                et_password.setText("");
                                                et_location.setText("");

                                                Toast register = Toast.makeText(RegistrationActivity.this, "Successful registration", Toast.LENGTH_SHORT);
                                                register.show();
                                                //open profile activity on successful registration
                                                Intent myIntent = new Intent(RegistrationActivity.this, ProfileActivity2.class);
                                                myIntent.putExtra("user_role",userRole);
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
                        }//if registration was not successful
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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