package com.sadpumpkin.farm2table.game.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.ResourceDefinition;
import com.sadpumpkin.farm2table.util.factory.ConverterInstance;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

public class ConverterInstanceViewHolder extends RecyclerView.ViewHolder {

    private TextView _nameLabel;
    private ProgressBar _progressBar;
    private TextView _progressBarLabel;
    private ImageView _consumedIcon;
    private TextView _consumedIconLabel;
    private ImageView _producedIcon;
    private TextView _producedIconLabel;

    public ConverterInstanceViewHolder(@NonNull View itemView) {
        super(itemView);

        _nameLabel = itemView.findViewById(R.id.titleLabel);
        _progressBar = itemView.findViewById(R.id.progressBar);
        _progressBarLabel = itemView.findViewById(R.id.progressBarLabel);
        _consumedIcon = itemView.findViewById(R.id.consumedIcon);
        _consumedIconLabel = itemView.findViewById(R.id.consumedIconLabel);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _producedIconLabel = itemView.findViewById(R.id.producedIconLabel);
    }

    public void setInstance(ConverterInstance instance,
                            ConverterDefinition definition,
                            FarmData farmData,
                            GameDataWrapper gameDataWrapper) {
        ResourceDefinition producedResource = gameDataWrapper.getResourceDefinition(definition.getProducedId());

        _nameLabel.setText(definition.getName());
        Glide.with(itemView)
                .load(gameDataWrapper.getImageReference(producedResource.getPath()))
                .into(_producedIcon);

        updateView(instance, definition, farmData, gameDataWrapper);
    }

    public void updateProgress(Double newProgress) {
        _progressBar.setProgress((int) Math.round(newProgress * 100));
        _progressBarLabel.setText(String.valueOf(Math.round(newProgress * 100) + "%"));
    }

    public void updateView(ConverterInstance instance,
                           ConverterDefinition definition,
                           FarmData farmData,
                           GameDataWrapper gameDataWrapper) {
        long count = instance.getCount();

        String consumedResourceId = FarmData.findLowestCostOwnedResourceInList(
                definition.getConsumedIds(),
                gameDataWrapper,
                farmData.getInventoryLive().getValue());
        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);

        if (consumedResource == null) {
            _consumedIcon.setVisibility(View.INVISIBLE);
            _consumedIconLabel.setVisibility(View.INVISIBLE);
            _producedIconLabel.setText("?");
        } else {
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
                    .into(_consumedIcon);
            _consumedIconLabel.setText("x" + String.valueOf(count));
            _producedIconLabel.setText("x" + String.valueOf(count));
            _consumedIcon.setVisibility(View.VISIBLE);
            _consumedIconLabel.setVisibility(View.VISIBLE);
        }
    }
}
