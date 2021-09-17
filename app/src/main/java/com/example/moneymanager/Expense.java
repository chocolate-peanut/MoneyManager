package com.example.moneymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class Expense extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private Button saveButton;
    private Button resetButton;
    private Button dateButton;
    private Spinner categorySpinner;
    private EditText amountET;
    private EditText descriptionET;
    private TextView dateView;

    private FirebaseAuth rootNode;
    private DatabaseReference expenseRef;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense); //set another page for add_transaction activity

        //set action for back function on the toolbar
        getSupportActionBar().setTitle("New Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (Button) findViewById(R.id.incSaveBtn);
        resetButton = (Button) findViewById(R.id.incCancelBtn);
        dateButton = (Button) findViewById(R.id.mDateButton);
        categorySpinner = (Spinner) findViewById(R.id.mSpinner);
        amountET = (EditText) findViewById(R.id.mAmount);
        descriptionET = (EditText) findViewById(R.id.mDesc);
        dateView = (TextView) findViewById(R.id.mDate);

        //set the spinner for the category options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        rootNode = FirebaseAuth.getInstance(); //point to rootNode in Firebase
        firebaseUser = rootNode.getCurrentUser();
        String uid = firebaseUser.getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference().child("Expense").child(uid); //point to this reference

       dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.moneymanager.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = expenseRef.push().getKey(); //create random id to store new data
                String amount = amountET.getText().toString().trim();
                String description = descriptionET.getText().toString().trim();
                String category = categorySpinner.getSelectedItem().toString().trim();
                String date = dateView.getText().toString().trim();

                if(TextUtils.isEmpty(date)) {
                    dateView.setError("Choose a Date");
                    return;
                }
                if(TextUtils.isEmpty(description)) {
                    descriptionET.setError("Please enter Description");
                    return;
                }
                if(TextUtils.isEmpty(amount)) {
                    amountET.setError("Please enter Amount");
                    return;
                }

                double amountD = Double.parseDouble(amount);
                Model data = new Model(amountD, description, category, date); //pass values
                expenseRef.child(id).setValue(data);
                Toast.makeText(Expense.this, "Data Saved", Toast.LENGTH_SHORT).show();
            }

        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptionET.setText("");
                amountET.setText("");
                dateView.setText("");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        dateView.setText(currentDate);
    }
}