package com.example.moneymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;


public class IncomeFragment extends Fragment {

    private FirebaseAuth rootNode;
    private DatabaseReference incomeRef;
    private RecyclerView recyclerView;
    private TextView totalIncomeTV;
    private Spinner spinner;

    //update data
    private EditText descET;
    private EditText amountET;
    private TextView dateTV;
    private Button modifyBtn;
    private Button deleteBtn;
    private Button dateBtn;
    private Spinner spinner1;
    private String key;
    private String description;
    private String category;
    private String date;
    private double amount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        rootNode = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = rootNode.getCurrentUser();
        String uid = firebaseUser.getUid();
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Income").child(uid);
        totalIncomeTV = view.findViewById(R.id.incomeResultText);

        recyclerView = view.findViewById(R.id.incomeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        incomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalIncome = 0.0;

                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model data = ds.getValue(Model.class);
                    totalIncome += data.getAmount();
                    totalIncomeTV.setText(String.valueOf(totalIncome));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(incomeRef, Model.class).setLifecycleOwner(this).build();

        FirebaseRecyclerAdapter<Model,MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model,MyViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Model model) {
                holder.sAmount(model.getAmount());
                holder.sCategory(model.getCategory());
                holder.sDate(model.getDate());
                holder.sDescription(model.getDescription());

                holder.view1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        key = getRef(position).getKey(); //point to selected child
                        description = model.getDescription();
                        amount = model.getAmount();
                        date = model.getDate();
                        category = model.getCategory();
                        modifyData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycler_view, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view1;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view1 = itemView;
        }

        private void sDescription(String description) {
            TextView descTV = view1.findViewById(R.id.incDescT);
            descTV.setText(description);
        }

        private void sCategory(String category) {
            TextView catTV = view1.findViewById(R.id.incCategoryT);
            catTV.setText(category);
        }

        private void sAmount(double amount) {
            TextView amountTV = view1.findViewById(R.id.incAmountT);
            String amountS = String.valueOf(amount);
            amountTV.setText(amountS);
        }

        private void sDate(String date) {
            TextView dateTV = view1.findViewById(R.id.incDateT);
            dateTV.setText(date);
        }
    }

    private void modifyData() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.modify_data_new, null);

        spinner = (Spinner) view.findViewById(R.id.mSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.category2
                ,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        dialog.setView(view);

        amountET = view.findViewById(R.id.mAmount);
        descET = view.findViewById(R.id.mDesc);
        dateTV = view.findViewById(R.id.mDate);
        dateBtn = view.findViewById(R.id.mDateButton);
        modifyBtn = view.findViewById(R.id.modifyBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        spinner1 = view.findViewById(R.id.mSpinner);

        //show current values
        amountET.setText(String.valueOf(amount));
        amountET.setSelection(String.valueOf(amount).length());
        descET.setText(description);
        descET.setSelection(description.length());
        dateTV.setText(date);

        final AlertDialog dialog1 = dialog.create();

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                        dateTV.setText(currentDate);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = descET.getText().toString().trim();
                category = spinner1.getSelectedItem().toString().trim();
                date = dateTV.getText().toString().trim();
                String amountS = String.valueOf(amount);
                amountS = amountET.getText().toString().trim();
                double amountD = Double.parseDouble(amountS);

                Model data = new Model(amountD,description,category,date);
                incomeRef.child(key).setValue(data);
                Toast.makeText(getActivity(),"Data Modified",Toast.LENGTH_SHORT).show();
                dialog1.dismiss(); //close window
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeRef.child(key).removeValue();
                dialog1.dismiss();
            }
        });
        dialog1.show();
    }
}