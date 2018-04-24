package com.example.gj94g.myhw2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


public class Ckbxad extends RecyclerView.Adapter<Ckbxadhd>{

    boolean[] checked_list;
    public Ckbxad(){
        checked_list = new boolean[100];
    }

    @Override
    public Ckbxadhd onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkboxlist, parent,false);
        Ckbxadhd ckbxadhd = new Ckbxadhd(v);
        ckbxadhd.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                int index = Integer.parseInt(checkBox.getText().toString()) - 1;
                checked_list[index] = !checked_list[index];
            }
        });
        return ckbxadhd;
    }

    @Override
    public void onBindViewHolder(Ckbxadhd holder, int position) {
        String num = String.valueOf(position+1);
        holder.checkBox.setText(num);
        holder.checkBox.setChecked(checked_list[position]);
    }

    @Override
    public int getItemCount() {
        return checked_list.length;
    }
}
