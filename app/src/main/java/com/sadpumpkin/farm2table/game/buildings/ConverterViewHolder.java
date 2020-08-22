package com.sadpumpkin.farm2table.game.buildings;

import android.text.TextUtils;
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
import com.sadpumpkin.farm2table.util.factory.definition.ConverterDefinition;

public class ConverterViewHolder extends RecyclerView.ViewHolder {

    private TextView _titleLabel;
    private ImageView _consumedIcon;
    private ImageView _producedIcon;
    private TextView _priceMultiplierLabel;
    private Button _buyButton;

    public ConverterViewHolder(@NonNull View itemView) {
        super(itemView);

        _titleLabel = itemView.findViewById(R.id.titleLabel);
        _consumedIcon = itemView.findViewById(R.id.consumedIcon);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _buyButton = itemView.findViewById(R.id.buyButton);
        _priceMultiplierLabel = itemView.findViewById(R.id.priceMultiplierLabel);
    }

    public void setData(ConverterDefinition definition,
                        FarmData farmData,
                        GameDataWrapper gameDataWrapper,
                        ICallback purchaseCallback) {

        _titleLabel.setText(definition.getName());
        _buyButton.setOnClickListener(clickedView -> purchaseCallback.onInvoke());
        _buyButton.setText(String.valueOf(definition.getCost()));
        _priceMultiplierLabel.setVisibility(View.GONE);

        String consumedResourceId = FarmData.findLowestCostOwnedResourceInList(
                definition.getConsumedIds(),
                gameDataWrapper,
                farmData.getInventory());
        if(TextUtils.isEmpty(consumedResourceId)) {
            consumedResourceId = definition.getConsumedIds().get(0);
        }
        String producedResourceId = definition.getProducedIdForConsumed(consumedResourceId);

        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);
        ResourceDefinition producedResource = gameDataWrapper.getResourceDefinition(producedResourceId);

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
