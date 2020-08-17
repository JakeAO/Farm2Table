package com.sadpumpkin.farm2table.game.buildings;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.FarmData;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.ResourceDefinition;
import com.sadpumpkin.farm2table.util.callback.ICallback;
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

public class BuildingViewHolder extends RecyclerView.ViewHolder {

    private TextView _titleLabel;
    private ImageView _consumedIcon;
    private ImageView _producedIcon;
    private Button _buyButton;
    private TextView _costLabel;

    public BuildingViewHolder(@NonNull View itemView) {
        super(itemView);

        _titleLabel = itemView.findViewById(R.id.titleLabel);
        _consumedIcon = itemView.findViewById(R.id.consumedIcon);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _buyButton = itemView.findViewById(R.id.buyButton);
        _costLabel = itemView.findViewById(R.id.costLabel);
    }

    public void setData(ConverterDefinition definition,
                        FarmData farmData,
                        GameDataWrapper gameDataWrapper,
                        ICallback purchaseCallback) {
        ResourceDefinition producedResource = gameDataWrapper.getResourceDefinition(definition.getProducedId());

        _titleLabel.setText(definition.getName());
        Glide.with(itemView)
                .load(gameDataWrapper.getImageReference(producedResource.getPath()))
                .into(_producedIcon);
        _buyButton.setOnClickListener(clickedView -> purchaseCallback.onInvoke());
        _costLabel.setText(String.valueOf(definition.getBaseCost()));

        String consumedResourceId = FarmData.findLowestCostOwnedResourceInList(
                definition.getConsumedIds(),
                gameDataWrapper,
                farmData.getInventory());
        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);
        if (consumedResource == null) {
            _consumedIcon.setVisibility(View.INVISIBLE);
        } else {
            _consumedIcon.setVisibility(View.VISIBLE);
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
                    .into(_consumedIcon);
        }
    }

    public void setData(ConsumerDefinition definition,
                        FarmData farmData,
                        GameDataWrapper gameDataWrapper,
                        ICallback purchaseCallback) {

        _titleLabel.setText(definition.getName());
        Glide.with(itemView)
                .load(R.drawable.ui_graphic_coin)
                .into(_producedIcon);
        _buyButton.setOnClickListener(clickedView -> purchaseCallback.onInvoke());
        _costLabel.setText(String.valueOf(definition.getBaseCost()));

        String consumedResourceId = FarmData.findHighestCostOwnedResourceInList(
                definition.getConsumedIds(),
                gameDataWrapper,
                farmData.getInventory());
        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);
        if (consumedResource == null) {
            _consumedIcon.setVisibility(View.INVISIBLE);
        } else {
            _consumedIcon.setVisibility(View.VISIBLE);
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
                    .into(_consumedIcon);
        }
    }
}
