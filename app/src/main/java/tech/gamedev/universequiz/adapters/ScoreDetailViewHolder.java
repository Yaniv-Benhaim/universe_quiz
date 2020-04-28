package tech.gamedev.universequiz.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tech.gamedev.universequiz.R;

public class ScoreDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_name,txt_score;


    public ScoreDetailViewHolder(@NonNull View itemView) {
        super(itemView);

        txt_name = itemView.findViewById(R.id.txt_name2);
        txt_score = itemView.findViewById(R.id.txt_score2);
    }
}
