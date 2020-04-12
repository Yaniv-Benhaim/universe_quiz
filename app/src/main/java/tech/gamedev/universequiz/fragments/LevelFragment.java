package tech.gamedev.universequiz.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tech.gamedev.universequiz.Common.Common;
import tech.gamedev.universequiz.Interface.ItemClickListener;
import tech.gamedev.universequiz.R;
import tech.gamedev.universequiz.adapters.LevelViewHolder;
import tech.gamedev.universequiz.model.Level;
import tech.gamedev.universequiz.ui.GameActivity;
import tech.gamedev.universequiz.ui.MainActivity;


public class LevelFragment extends Fragment {

    View myFragment;
    RecyclerView levelsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Level, LevelViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference level;
    public Animation btnAnim;


    public static LevelFragment newInstance(){
        LevelFragment levelFragment = new LevelFragment();
        return levelFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        level = database.getReference("Level");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_level,container,false);

        levelsRecyclerView = myFragment.findViewById(R.id.category_level_rv);
        levelsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(container.getContext());
        levelsRecyclerView.setLayoutManager(layoutManager);
        
        loadLevels();
        
        return myFragment;
    }

    private void loadLevels() {
        adapter = new FirebaseRecyclerAdapter<Level, LevelViewHolder>(
                Level.class,
                R.layout.level_layout_item,
                LevelViewHolder.class,
                level
        ) {
            @Override
            protected void populateViewHolder(LevelViewHolder levelViewHolder, final Level level, int i) {
                levelViewHolder.btn.setText(level.getName());
                btnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.btn_scale_anim);
                levelViewHolder.btn.startAnimation(btnAnim);
                levelViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(), "WORKINg", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), GameActivity.class);
                        Common.levelId = adapter.getRef(position).getKey();
                        startActivity(intent);


                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        levelsRecyclerView.setAdapter(adapter);
    }
}
