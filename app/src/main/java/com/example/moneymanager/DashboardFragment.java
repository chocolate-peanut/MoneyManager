package com.example.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {

    //Firebase
    private FirebaseUser firebaseUser;
    private FirebaseAuth rootNode;
    private DatabaseReference expenseRef;
    private DatabaseReference incomeRef;

    //floating action button
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabExpense;
    private FloatingActionButton fabIncome;

    //floating action button text
    private TextView expenseTxt;
    private TextView incomeTxt;

    //object of animation
    private Animation fabOpen, fabClose;
    private boolean isOpen = false;

    //income, expense and balance
    private TextView totalIncome;
    private TextView totalExpense;
    private TextView totalBalance;
    private ArrayList<Double> incomeList = new ArrayList<>();
    private ArrayList<Double> expenseList = new ArrayList<>();
    private double balance, expDouble, incDouble;
    public DecimalFormat df = new DecimalFormat("##.##");

    //recycler view
    private RecyclerView expenseRecycler;
    private RecyclerView incomeRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //store data to firebase
        rootNode = FirebaseAuth.getInstance(); //point to rootNode in Firebase
        firebaseUser = rootNode.getCurrentUser();
        String uid = firebaseUser.getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference().child("Expense").child(uid); //point to this reference
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Income").child(uid);

        //connect floating button with layout
        fabAdd = view.findViewById(R.id.addFBtn);
        fabIncome = view.findViewById(R.id.incomeFBtn);
        fabExpense = view.findViewById(R.id.expenseFBtn);

        //connect floating button text with layout
        incomeTxt = view.findViewById(R.id.incomeFText);
        expenseTxt = view.findViewById(R.id.expenseFText);

        //income, expense and balance
        totalIncome = view.findViewById(R.id.totalIncome);
        totalExpense = view.findViewById(R.id.totalExpense);
        totalBalance = view.findViewById(R.id.balance);

        //recycler view
        expenseRecycler = view.findViewById(R.id.dbExpenseRecycler);
        incomeRecycler = view.findViewById(R.id.dbIncomeRecycler);

        //connect animation
        fabOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fb_open);
        fabClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fb_close);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                fabAnimation();
            }
        });

        //set recycler view layout
        LinearLayoutManager incomeLM = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        incomeLM.setStackFromEnd(true);
        incomeLM.setReverseLayout(true);
        incomeRecycler.setHasFixedSize(true);
        incomeRecycler.setLayoutManager(incomeLM);

        LinearLayoutManager expenseLM = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        expenseLM.setStackFromEnd(true);
        expenseLM.setReverseLayout(true);
        expenseRecycler.setHasFixedSize(true);
        expenseRecycler.setLayoutManager(expenseLM);

        readData(new CallFirebase() {
            @Override
            public void onCall(List<Double> list) {
                balance = incDouble - expDouble;
                totalBalance.setText(String.valueOf(balance));
              //  totalBalance.setText(df.format(incDouble - expDouble));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //display income on dashboard
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(incomeRef, Model.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<Model, ViewIncome> incAdapter = new FirebaseRecyclerAdapter<Model, ViewIncome>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewIncome holder, int position, @NonNull Model model) {
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeCategory(model.getCategory());
                holder.setIncomeDate(model.getDate());
            }

            @NonNull
            @Override
            public ViewIncome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new DashboardFragment.ViewIncome(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_income, parent, false));
            }
        };
        incomeRecycler.setAdapter(incAdapter);

        //display expense on dashboard
        FirebaseRecyclerOptions<Model> options1 = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(expenseRef, Model.class).setLifecycleOwner(this).build();
        FirebaseRecyclerAdapter<Model, ViewExpense> expAdapter = new FirebaseRecyclerAdapter<Model, ViewExpense>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull ViewExpense holder, int position, @NonNull Model model) {
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseCategory(model.getCategory());
                holder.setExpenseDate(model.getDate());
            }

            @NonNull
            @Override
            public ViewExpense onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new DashboardFragment.ViewExpense(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_expense, parent, false));
            }
        };
        expenseRecycler.setAdapter(expAdapter);
    }

    //floating button add data
    private void addData() {
        fabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Expense.class));
            }
        });

        fabIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), IncomeActivity.class));
            }
        });
    }

    //deal with asynchronous properties of Firebase
    private interface CallFirebase {
        void onCall(List<Double> list);
    }

    //read data from Firebase for calculation
    private void readData(CallFirebase callFirebase) {

        //calculate total income
        incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model data = ds.getValue(Model.class);
                    incDouble += data.getAmount();
                    totalIncome.setText(String.valueOf(incDouble));
                    incomeList.add(incDouble);
                }
                callFirebase.onCall(incomeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //calculate total expense
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model data = ds.getValue(Model.class);
                    expDouble += data.getAmount();
                    totalExpense.setText(String.valueOf(expDouble));
                    expenseList.add(expDouble);
                }
                callFirebase.onCall(expenseList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //floating action button animation
    private void fabAnimation() {
        if(isOpen) {
            fabIncome.startAnimation(fabClose);
            fabExpense.startAnimation(fabClose);
            fabIncome.setClickable(false);
            fabExpense.setClickable(false);
            incomeTxt.startAnimation(fabClose);
            expenseTxt.startAnimation(fabClose);
            incomeTxt.setClickable(false);
            expenseTxt.setClickable(false);
            isOpen = false;
        }
        else {
            fabIncome.startAnimation(fabOpen);
            fabExpense.startAnimation(fabOpen);
            fabIncome.setClickable(true);
            fabExpense.setClickable(true);
            incomeTxt.startAnimation(fabOpen);
            expenseTxt.startAnimation(fabOpen);
            incomeTxt.setClickable(true);
            expenseTxt.setClickable(true);
            isOpen = true;
        }
    }

    //display income data
    public static class ViewIncome extends RecyclerView.ViewHolder {
        View view;

        public ViewIncome(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setIncomeCategory(String category) {
            TextView iCategory = view.findViewById(R.id.dbIncomeCategory);
            iCategory.setText(category);
        }

        public void setIncomeAmount(double amount) {
            TextView iAmount = view.findViewById(R.id.dbIncomeAmount);
            String amountStr = String.valueOf(amount);
            iAmount.setText(amountStr);
        }

        public void setIncomeDate(String date) {
            TextView iDate = view.findViewById(R.id.dbIncomeDate);
            iDate.setText(date);
        }
    }

    //display expense data
    public static class ViewExpense extends RecyclerView.ViewHolder {
        View view;

        public ViewExpense(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setExpenseCategory(String category) {
            TextView iCategory = view.findViewById(R.id.dbExpenseCategory);
            iCategory.setText(category);
        }

        public void setExpenseAmount(double amount) {
            TextView iAmount = view.findViewById(R.id.dbExpenseAmount);
            String amountStr = String.valueOf(amount);
            iAmount.setText(amountStr);
        }

        public void setExpenseDate(String date) {
            TextView iDate = view.findViewById(R.id.dbExpenseDate);
            iDate.setText(date);
        }
    }
}