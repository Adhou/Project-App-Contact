package com.example.mycalculator;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class CreateContactActivity extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText editName, editEmail, editHomePhone, editOfficePhone;
    Button save, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        myDb = new DatabaseHelper(this);
        editName = findViewById(R.id.contact_name);
        editEmail =  findViewById(R.id.contact_email);
        editHomePhone =  findViewById(R.id.number_home);
        editOfficePhone = findViewById(R.id.number_office);
        save =  findViewById(R.id.buttonSave);
        back =  findViewById(R.id.back);

        // Check if the Intent contains the extra data
        Intent intent = getIntent();
        if (intent.hasExtra("name")) {
            // If it does, populate the input fields with the existing details
            editName.setText(intent.getStringExtra("name"));
            editEmail.setText(intent.getStringExtra("email"));
            editHomePhone.setText(intent.getStringExtra("homePhone"));
            editOfficePhone.setText(intent.getStringExtra("officePhone"));
        }

        AddData();
        back.setOnClickListener(new View.OnClickListener(){
        public void onClick (View v){
        Intent intent = new Intent(CreateContactActivity.this, MyContactsActivity.class);
        startActivity(intent);
        finish();
    }
    });
}

    public void AddData() {
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editName.getText().toString();
                        String homePhone = editHomePhone.getText().toString();
                        String officePhone = editOfficePhone.getText().toString();
                        String email = editEmail.getText().toString();

                        if(name.isEmpty() || (name.length()<3)){
                            showError("Enter a valid name");
                        } else if (homePhone.isEmpty() && officePhone.isEmpty()){
                            showError("Enter at least one phone number");
                        } else if ((!homePhone.isEmpty() && homePhone.length()<9) || (!officePhone.isEmpty() && officePhone.length()<9)){
                            showError("Enter a valid phone number");
                        } else if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            showError("Enter a valid email address");
                        }
                        else {
                            //Intent contains the extra data?
                            Intent intent = getIntent();
                            if (intent.hasExtra("name")) {
                                //then update the contact
                                int result = myDb.updateData(name, email, homePhone, officePhone);
                                if (result == 0) {
                                    showDialog(false);
                                } else {
                                    showDialog(true);
                                    editName.setText("");
                                    editHomePhone.setText("");
                                    editOfficePhone.setText("");
                                    editEmail.setText("");
                                }
                            } else {
                                //or insert a new contact
                                int result = myDb.insertData(name, email, homePhone, officePhone);
                                if (result == -1) {
                                    showError("A contact with this phone number already exists");
                                } else if (result == 0) {
                                    showDialog(false);
                                } else {
                                    showDialog(true);
                                    editName.setText("");
                                    editHomePhone.setText("");
                                    editOfficePhone.setText("");
                                    editEmail.setText("");
                                }
                            }
                        }
                    }
                }
        );
    }


    private void showDialog(boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateContactActivity.this);
        if (status) {
            builder.setMessage("Saved Successfully");
        } else {
            builder.setMessage("Failed");

        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //error msg
    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateContactActivity.this);
        builder.setTitle("Error");
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