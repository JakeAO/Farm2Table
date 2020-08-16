package com.sadpumpkin.farm2table.game.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.ResourceDefinition;
import com.sadpumpkin.farm2table.util.factory.ConsumerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;

public class ConsumerInstanceViewHolder extends RecyclerView.ViewHolder {

    private TextView _nameLabel;
    private ProgressBar _progressBar;
    private TextView _progressBarLabel;
    private ImageView _consumedIcon;
    private TextView _consumedIconLabel;
    private ImageView _producedIcon;
    private TextView _producedIconLabel;

    public ConsumerInstanceViewHolder(@NonNull View itemView) {
        super(itemView);

        _nameLabel = itemView.findViewById(R.id.titleLabel);
        _progressBar = itemView.findViewById(R.id.progressBar);
        _progressBarLabel = itemView.findViewById(R.id.progressBarLabel);
        _consumedIcon = itemView.findViewById(R.id.consumedIcon);
        _consumedIconLabel = itemView.findViewById(R.id.consumedIconLabel);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _producedIconLabel = itemView.findViewById(R.id.producedIconLabel);
    }

    public void setInstance(ConsumerInstance instance, ConsumerDefinition definition, GameDataWrapper gameDataWrapper) {
//        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(definition.getConsumedId());

        _nameLabel.setText(definition.getName());
//        Glide.with(itemView)
//                .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
//                .into(_consumedIcon);
        _consumedIconLabel.setText("x1");
        Glide.with(itemView)
                .load(R.drawable.ui_graphic_coin)
                .into(_producedIcon);
        _producedIconLabel.setText("x" + definition.getBaseCost());
    }
}
