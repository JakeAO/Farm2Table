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

        updateView(instance, definition, farmData, gameDataWrapper);
    }

    public void updateProgress(Double newProgress) {
        _progressBar.setProgress((int) Math.round(newProgress * 100));
        _progressBarLabel.setText(Math.round(newProgress * 100) + "%");
    }

    public void updateActive(boolean active) {
        _progressBar.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
        _progressBarLabel.setVisibility(active ? View.VISIBLE : View.INVISIBLE);
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
        String producedResourceId = definition.getProducedIdForConsumed(consumedResourceId);

        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);
        ResourceDefinition producedResource = gameDataWrapper.getResourceDefinition(producedResourceId);

        _nameLabel.setText(definition.getName());
        _consumedIconLabel.setText("x" + count);
        _producedIconLabel.setText("x" + count);

        if (consumedResource == null || producedResource == null) {
            Glide.with(itemView)
                    .load(R.drawable.ic_dummy_resource)
                    .into(_consumedIcon);
            Glide.with(itemView)
                    .load(R.drawable.ic_dummy_resource)
                    .into(_producedIcon);
        } else {
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
                    .into(_consumedIcon);
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(producedResource.getPath()))
                    .into(_producedIcon);
        }
    }
}
