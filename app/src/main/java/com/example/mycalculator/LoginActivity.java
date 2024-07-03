package com.example.mycalculator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;

    Button buttonLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.editTextLoginEmail);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        loadLoginDetails();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });
    }
    private void checkLogin(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String registeredEmail = sharedPreferences.getString("email", "");
        String registeredPassword = sharedPreferences.getString("password", "");

        if(email.equals(registeredEmail) && password.equals(registeredPassword)) {
            // Login successful
                saveDetails(email, password);
                Intent intent = new Intent(LoginActivity.this, MyContactsActivity.class);
                startActivity(intent);
                finish();

        } else {
            // Login failed
            showDialog("Invalid Email or Password");
        }
    }
    private void saveDetails(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("email", email);
        myEdit.putString("password", password);
        myEdit.apply();
    }

    private void loadLoginDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String registeredEmail = sharedPreferences.getString("email", "");
        String registeredPassword = sharedPreferences.getString("password", "");
        editTextEmail.setText(registeredEmail);
        editTextPassword.setText(registeredPassword);
    }
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Sign Up Status");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}