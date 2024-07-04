package com.example.mycalculator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.List;

public class MyContactsActivity extends AppCompatActivity {
    private ListView contactsList;
    Button createButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);
        db = new DatabaseHelper(this);
        contactsList = findViewById(R.id.contacts_list);
        createButton = findViewById(R.id.create_contact_button);
        loadContacts();

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected contact's details
                String contactDetails = (String) parent.getItemAtPosition(position);
                String[] details = contactDetails.split("\n");
                String name = details[0].substring(details[0].indexOf(":") + 2);
                String phoneHome = details[1].substring(details[1].indexOf(":") + 2);

                // option dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MyContactsActivity.this);
                builder.setTitle("Choose an option")
                        .setItems(new String[]{"Update", "Delete", "Details"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Update
                                        // Intent to start the CreateContactActivity
                                        Intent intent = new Intent(MyContactsActivity.this, CreateContactActivity.class);
                                        String fullDetails = db.getContactDetails(phoneHome);
                                        String[] fullDetailsArray = fullDetails.split("\n");
                                        intent.putExtra("name", fullDetailsArray[0].substring(fullDetailsArray[0].indexOf(":") + 2));
                                        intent.putExtra("email", fullDetailsArray[1].substring(fullDetailsArray[1].indexOf(":") + 2));
                                        intent.putExtra("homePhone", fullDetailsArray[2].substring(fullDetailsArray[2].indexOf(":") + 2));
                                        intent.putExtra("officePhone", fullDetailsArray[3].substring(fullDetailsArray[3].indexOf(":") + 2));

                                        startActivity(intent);
                                        break;

                                    case 1:
                                        // Delete
                                        new AlertDialog.Builder(MyContactsActivity.this)
                                                .setTitle("Delete Contact")
                                                .setMessage("Are you sure you want to delete this contact?")
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                                        button.setTextColor(Color.RED);
                                                        // delete operation
                                                        int result = db.deleteData(phoneHome, null);
                                                        if (result > 0) {
                                                            // successful deletion
                                                            showDialog(true);
                                                            // loading updated data
                                                            loadContacts();
                                                        } else {
                                                            // an error
                                                            showDialog(false);
                                                        }
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                        break;

                                    case 2:
                                        // Details
                                        String contactDetails = db.getContactDetails(phoneHome);
                                        new AlertDialog.Builder(MyContactsActivity.this)
                                                .setTitle("Contact Details")
                                                .setMessage(contactDetails)
                                                .setPositiveButton(android.R.string.ok, null)
                                                .show();
                                        break;
                                }
                            }
                        });
                builder.create().show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyContactsActivity.this, CreateContactActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // loading all data from sqlite
    private void loadContacts() {
        List<String> contacts = db.getAllContacts();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contacts);
        contactsList.setAdapter(adapter);
    }

    private void showDialog(boolean success) {
        if (success) {
            new AlertDialog.Builder(MyContactsActivity.this)
                    .setTitle("Success")
                    .setMessage("Contact deleted successfully")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            new AlertDialog.Builder(MyContactsActivity.this)
                    .setTitle("Error")
                    .setMessage("Error deleting contact")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }
}
