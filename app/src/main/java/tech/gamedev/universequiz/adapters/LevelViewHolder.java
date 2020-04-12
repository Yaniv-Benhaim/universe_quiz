package tech.gamedev.universequiz.adapters;

import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tech.gamedev.universequiz.Interface.ItemClickListener;
import tech.gamedev.universequiz.R;

public class LevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public LinearLayout levelBtn;
    public Button btn;


    private ItemClickListener itemClickListener;

    public LevelViewHolder(@NonNull View itemView) {
        super(itemView);
        levelBtn = itemView.findViewById(R.id.linear_btn);
        btn = itemView.findViewById(R.id.level_btn);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
