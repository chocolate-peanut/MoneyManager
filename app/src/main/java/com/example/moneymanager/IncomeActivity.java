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

public class IncomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private Button saveButton;
    private Button resetButton;
    private Button dateButton;
    private Spinner categorySpinner;
    private EditText amountET;
    private EditText descriptionET;
    private TextView dateView;

    private FirebaseUser firebaseUser;
    private FirebaseAuth rootNode;
    private DatabaseReference incomeRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income); //set another page for add_transaction activity

        //set action for back function on the toolbar
        getSupportActionBar().setTitle("New Income");

        dateButton = (Button) findViewById(R.id.mDateButton);
        categorySpinner = (Spinner) findViewById(R.id.mSpinner);
        amountET = (EditText) findViewById(R.id.mAmount);
        descriptionET = (EditText) findViewById(R.id.mDesc);
        amountET = (EditText) findViewById(R.id.mAmount);
        saveButton = (Button) findViewById(R.id.incSaveBtn);
        resetButton = (Button) findViewById(R.id.incCancelBtn);
        dateView = (TextView) findViewById(R.id.mDate);

        // Creating an Array Adapter to populate the spinner with the data in the string resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category2, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(this);

        rootNode = FirebaseAuth.getInstance(); //point to rootNode in Firebase
        firebaseUser = rootNode.getCurrentUser();
        String uid = firebaseUser.getUid();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Income").child(uid); //point to this reference

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
                String id = incomeRef.push().getKey(); //create random id to store new data
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
                incomeRef.child(id).setValue(data);
                Toast.makeText(IncomeActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
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

