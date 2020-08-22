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
import com.sadpumpkin.farm2table.util.factory.ProducerInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ProducerDefinition;

public class ProducerInstanceViewHolder extends RecyclerView.ViewHolder {

    private TextView _nameLabel;
    private ProgressBar _progressBar;
    private TextView _progressBarLabel;
    private ImageView _producedIcon;
    private TextView _producedIconLabel;

    public ProducerInstanceViewHolder(@NonNull View itemView) {
        super(itemView);

        _nameLabel = itemView.findViewById(R.id.titleLabel);
        _progressBar = itemView.findViewById(R.id.progressBar);
        _progressBarLabel = itemView.findViewById(R.id.progressBarLabel);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _producedIconLabel = itemView.findViewById(R.id.producedIconLabel);
    }

    public void setInstance(ProducerInstance instance,
                            ProducerDefinition definition,
                            GameDataWrapper gameDataWrapper) {

        ResourceDefinition producedResource = gameDataWrapper.getResourceDefinition(definition.getProducedId());

        _nameLabel.setText(definition.getName());
        Glide.with(itemView)
                .load(gameDataWrapper.getImageReference(producedResource.getPath()))
                .into(_producedIcon);

        updateView(instance, definition, gameDataWrapper);
    }

    public void updateProgress(Double newProgress) {
        _progressBar.setProgress((int) Math.round(newProgress * 100));
        _progressBarLabel.setText(Math.round(newProgress * 100) + "%");
    }

    public void updateActive(boolean active) {
        _progressBar.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
        _progressBarLabel.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateView(ProducerInstance instance,
                           ProducerDefinition definition,
                           GameDataWrapper gameDataWrapper) {
        Long count = instance.getCountLive().getValue();

        _producedIconLabel.setText("x" + count);
    }
}
