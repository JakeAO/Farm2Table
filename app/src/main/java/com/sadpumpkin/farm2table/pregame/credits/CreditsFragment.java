package com.sadpumpkin.farm2table.pregame.credits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sadpumpkin.farm2table.R;

public class CreditsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_credits, container, false);

        TextView textView = root.findViewById(R.id.text_home);
        textView.setText("Credits Fragment");

        return root;
    }
}