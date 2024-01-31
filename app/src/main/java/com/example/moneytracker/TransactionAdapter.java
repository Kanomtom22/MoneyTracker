package com.example.moneytracker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder>{
    Context context;
    ArrayList<TransactionModel> transactionModelArrayList;

    public TransactionAdapter(Context context, ArrayList<TransactionModel> transactionModelArrayList) {
        this.context = context;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.MyViewHolder holder, int position) {
        TransactionModel model = transactionModelArrayList.get(position);
        String priority = model.getType();
        if (priority.equals("Expenses")) {
            holder.amount.setTextColor(context.getResources().getColor(R.color.pink));
            holder.bath.setTextColor(context.getResources().getColor(R.color.pink));
        }
        else {
            holder.amount.setTextColor(context.getResources().getColor(R.color.sky));
            holder.bath.setTextColor(context.getResources().getColor(R.color.sky));
        }
        holder.amount.setText(model.getAmount());
        holder.note.setText(model.getNote());
        holder.date.setText(model.getDate());
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note,amount,date,bath;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.note_one);
            amount = itemView.findViewById(R.id.amount_one);
            date = itemView.findViewById(R.id.date_one);
            bath = itemView.findViewById(R.id.bath);

        }
    }
}
