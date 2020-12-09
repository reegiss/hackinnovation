package br.hackinnovation.appintegramobi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.hackinnovation.appintegramobi.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmail, mPassword, mName, mPhone;
    private Button mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setTitle("Cadastrar no Aplicativo");

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
//                checkUserAccType();
            }
        };

        mName = findViewById(R.id.register_full_name);
        mEmail = findViewById(R.id.register_email);
        mPhone = findViewById(R.id.register_phone);
        mPassword = findViewById(R.id.register_password);

        mRegistration = findViewById(R.id.button_register_finish);
        mRegistration.setOnClickListener(v -> {
            if (validateField()) return;
            final String name = mName.getText().toString();
            final String email = mEmail.getText().toString();
            final String phone = mPhone.getText().toString();
            final String password = mPassword.getText().toString();
            final String accountType = "Customers";

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                } else {
                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(accountType).child(user_id);
                    current_user_db.child("name").setValue(name);
                    current_user_db.child("phone").setValue(phone);
                    current_user_db.child("email").setValue(email);

                }
            });
        });


    }

    private void checkUserAccType() {
        DatabaseReference mCustomerDatabase;
        String userID;

        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Intent intent = new Intent(RegisterActivity.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Intent intent = new Intent(RegisterActivity.this, CustomerMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    private boolean validateField() {
        if (mName.getText().toString().isEmpty()) {
            mName.setError("Preencha o nome");
            mName.requestFocus();
            return true;
        }
        if (mEmail.getText().toString().isEmpty()) {
            mEmail.setError("Preencha o email");
            mEmail.requestFocus();
            return true;
        }
        if (mPhone.getText().toString().isEmpty()) {
            mPhone.setError("Preencha o telefone");
            mPhone.requestFocus();
            return true;
        }
        if (mPassword.getText().toString().isEmpty()) {
            mPassword.setError("Preencha a senha");
            mPassword.requestFocus();
            return true;
        }
        return false;
    }

}
