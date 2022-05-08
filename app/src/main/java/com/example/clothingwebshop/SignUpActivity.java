package com.example.clothingwebshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private static final String LOG_TAG = SignUpActivity.class.getName();
    private static final String PREF_KEY = SignUpActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 5325633;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText phoneNumberEditText;
    private EditText addressEditText;
    private RadioGroup genderRadioGroup;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle bundle = getIntent().getExtras();
        int secret_key = bundle.getInt("SECRET_KEY");

        if(secret_key != 5325633) {
            finish();
        }

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        addressEditText = findViewById(R.id.addressEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        genderRadioGroup.check(R.id.default_gender);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");
        emailEditText.setText(email);
        passwordEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }


    public void signup(View view) {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String address = addressEditText.getText().toString();

        RadioButton genderRadioButton = genderRadioGroup.findViewById(genderRadioGroup.getCheckedRadioButtonId());
        String gender = genderRadioButton.getText().toString();


        if(!email.matches("^[A-Za-z0-9]{2,}[@][a-z]{4,}[.][a-z]{2,}$")){
            Toast.makeText(SignUpActivity.this, "Az e-mail cím formátuma nem megfelelő!", Toast.LENGTH_LONG).show();
        }


        if(!password.equals(passwordConfirm)){
            Log.e(LOG_TAG, "A két jelszó megadás nem egyezik meg!");
            Toast.makeText(SignUpActivity.this, "A két jelszó megadás nem egyezik meg!", Toast.LENGTH_LONG).show();
            return;
        }
        if(username.equals("") || email.equals("") || password.equals("")
                || passwordConfirm.equals("") || phoneNumber.equals("") || address.equals("") || gender.equals("")){
            Toast.makeText(SignUpActivity.this, "Az összes adatot kötelező megadni a regisztrációhoz!", Toast.LENGTH_LONG).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                Log.d(LOG_TAG, "A felhasználó sikeresen létrehozva!");
                Intent intent = new Intent(this, ShopListActivity.class);
                intent.putExtra("SECRET_KEY", SECRET_KEY);
                startActivity(intent);
            } else {
                Log.e(LOG_TAG, "A felhasználó létrehozása nem sikerült!");
                Toast.makeText(SignUpActivity.this, "A felhasználó létrehozása nem sikerült: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }
}