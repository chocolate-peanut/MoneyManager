package com.example.moneymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class StatisticActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private FirebaseAuth rootNode;
    private DatabaseReference expenseRef;
    private DatabaseReference incomeRef;

    private TextView salaryTV, awardsTV, investTV, dividendTV, refundTV, lotteryTV, incOthersTV;
    private TextView foodTV, billsTV, transportTV, shoppingTV, healthTV, petTV, travelTV, educationTV, expOthersTV;
    private ArrayList<Double> incomeList = new ArrayList<>();
    private ArrayList<Double> expenseList = new ArrayList<>();
    private ArrayList<Double> salary = new ArrayList<>();
    private ArrayList<Double> awards = new ArrayList<>();
    private ArrayList<Double> investment = new ArrayList<>();
    private ArrayList<Double> dividend = new ArrayList<>();
    private ArrayList<Double> refund = new ArrayList<>();
    private ArrayList<Double> lottery = new ArrayList<>();
    private ArrayList<Double> incOthers = new ArrayList<>();
    private ArrayList<Double> food = new ArrayList<>();
    private ArrayList<Double> bills = new ArrayList<>();
    private ArrayList<Double> transportation = new ArrayList<>();
    private ArrayList<Double> shopping = new ArrayList<>();
    private ArrayList<Double> health = new ArrayList<>();
    private ArrayList<Double> pet = new ArrayList<>();
    private ArrayList<Double> travel = new ArrayList<>();
    private ArrayList<Double> education = new ArrayList<>();
    private ArrayList<Double> expOthers = new ArrayList<>();
    private double expDouble, incDouble;
    private double salaryAmt, awardsAmt, investAmt, dividendAmt, refundAmt, lotteryAmt, incOthersAmt;
    private double foodAmt, billsAmt, transportAmt, shoppingAmt, healthAmt, petAmt, travelAmt, eduAmt, expOthersAmt;
    public DecimalFormat df = new DecimalFormat("##.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        getSupportActionBar().setTitle("Statistics");

        //store data to firebase
        rootNode = FirebaseAuth.getInstance();
        firebaseUser = rootNode.getCurrentUser();
        String uid = firebaseUser.getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference().child("Expense").child(uid);
        incomeRef = FirebaseDatabase.getInstance().getReference().child("Income").child(uid);

        salaryTV = findViewById(R.id.tvSalary);
        awardsTV = findViewById(R.id.tvAwards);
        investTV = findViewById(R.id.tvInvestment);
        dividendTV = findViewById(R.id.tvDividend);
        refundTV = findViewById(R.id.tvRefund);
        lotteryTV = findViewById(R.id.tvLottery);
        incOthersTV = findViewById(R.id.tvIncOthers);

        foodTV = findViewById(R.id.tvFood);
        billsTV = findViewById(R.id.tvBills);
        transportTV = findViewById(R.id.tvTransportation);
        shoppingTV = findViewById(R.id.tvShopping);
        healthTV = findViewById(R.id.tvHealth);
        petTV = findViewById(R.id.tvPet);
        travelTV = findViewById(R.id.tvTravel);
        educationTV = findViewById(R.id.tvEducation);
        expOthersTV = findViewById(R.id.tvExpOthers);

        //calculate percentage
        readData(new CallFirebase() {
            @Override
            public void onCall(List<Double> list) {
                salaryTV.setText(df.format(salaryAmt / incDouble * 100));
                awardsTV.setText(df.format(awardsAmt / incDouble * 100));
                investTV.setText(df.format(investAmt / incDouble * 100));
                dividendTV.setText(df.format(dividendAmt / incDouble * 100));
                refundTV.setText(df.format(refundAmt / incDouble * 100));
                lotteryTV.setText(df.format(lotteryAmt / incDouble * 100));
                incOthersTV.setText(df.format(incOthersAmt / incDouble * 100));

                foodTV.setText(df.format(foodAmt / expDouble * 100));
                billsTV.setText(df.format(billsAmt / expDouble * 100));
                transportTV.setText(df.format(transportAmt / expDouble * 100));
                shoppingTV.setText(df.format(shoppingAmt / expDouble * 100));
                healthTV.setText(df.format(healthAmt / expDouble * 100));
                petTV.setText(df.format(petAmt / expDouble * 100));
                travelTV.setText(df.format(travelAmt / expDouble * 100));
                educationTV.setText(df.format(eduAmt / expDouble * 100));
                expOthersTV.setText(df.format(expOthersAmt / expDouble * 100));
            }
        });

    }

    //deal with asynchronous properties of Firebase
    private interface CallFirebase {
        void onCall(List<Double> list);
    }

    private void readData(CallFirebase callFirebase) {

        //income database
        incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model data = ds.getValue(Model.class);
                    incDouble += data.getAmount();
                    incomeList.add(incDouble);

                    if(data.getCategory().equals("Salary")) {
                        salaryAmt += data.getAmount();
                        salary.add(salaryAmt);
                    }
                    if(data.getCategory().equals("Awards")) {
                        awardsAmt += data.getAmount();
                        awards.add(awardsAmt);
                    }
                    if(data.getCategory().equals("Investment")) {
                        investAmt += data.getAmount();
                        investment.add(investAmt);
                    }
                    if(data.getCategory().equals("Dividend")) {
                        dividendAmt += data.getAmount();
                        dividend.add(dividendAmt);
                    }
                    if(data.getCategory().equals("Refund")) {
                        refundAmt += data.getAmount();
                        refund.add(refundAmt);
                    }
                    if(data.getCategory().equals("Lottery")) {
                        lotteryAmt += data.getAmount();
                        lottery.add(lotteryAmt);
                    }
                    if(data.getCategory().equals("iOthers")) {
                        incOthersAmt += data.getAmount();
                        incOthers.add(incOthersAmt);
                    }
                }
                callFirebase.onCall(incomeList);
                callFirebase.onCall(salary);
                callFirebase.onCall(awards);
                callFirebase.onCall(investment);
                callFirebase.onCall(dividend);
                callFirebase.onCall(refund);
                callFirebase.onCall(lottery);
                callFirebase.onCall(incOthers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //expense database
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Model data = ds.getValue(Model.class);
                    expDouble += data.getAmount();
                    expenseList.add(expDouble);

                    if(data.getCategory().equals("Food")) {
                        foodAmt += data.getAmount();
                        food.add(foodAmt);
                    }
                    if(data.getCategory().equals("Bills")) {
                        billsAmt += data.getAmount();
                        bills.add(billsAmt);
                    }
                    if(data.getCategory().equals("Transportation")) {
                        transportAmt += data.getAmount();
                        transportation.add(transportAmt);
                    }
                    if(data.getCategory().equals("Shopping")) {
                        shoppingAmt += data.getAmount();
                        shopping.add(shoppingAmt);
                    }
                    if(data.getCategory().equals("Health")) {
                        healthAmt += data.getAmount();
                        health.add(healthAmt);
                    }
                    if(data.getCategory().equals("Pet")) {
                        petAmt += data.getAmount();
                        pet.add(petAmt);
                    }
                    if(data.getCategory().equals("Travel")) {
                        travelAmt += data.getAmount();
                        travel.add(travelAmt);
                    }
                    if(data.getCategory().equals("Education")) {
                        eduAmt += data.getAmount();
                        education.add(eduAmt);
                    }
                    if(data.getCategory().equals("eOthers")) {
                        expOthersAmt += data.getAmount();
                        expOthers.add(expOthersAmt);
                    }
                }
                callFirebase.onCall(expenseList);
                callFirebase.onCall(food);
                callFirebase.onCall(bills);
                callFirebase.onCall(transportation);
                callFirebase.onCall(shopping);
                callFirebase.onCall(health);
                callFirebase.onCall(pet);
                callFirebase.onCall(travel);
                callFirebase.onCall(education);
                callFirebase.onCall(expOthers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}