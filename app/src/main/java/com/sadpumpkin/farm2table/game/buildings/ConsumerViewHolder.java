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
import com.sadpumpkin.farm2table.util.factory.definition.ConsumerDefinition;

import java.text.NumberFormat;

public class ConsumerViewHolder extends RecyclerView.ViewHolder {

    private TextView _titleLabel;
    private ImageView _consumedIcon;
    private ImageView _producedIcon;
    private TextView _priceMultiplierLabel;
    private Button _buyButton;

    public ConsumerViewHolder(@NonNull View itemView) {
        super(itemView);

        _titleLabel = itemView.findViewById(R.id.titleLabel);
        _consumedIcon = itemView.findViewById(R.id.consumedIcon);
        _producedIcon = itemView.findViewById(R.id.producedIcon);
        _buyButton = itemView.findViewById(R.id.buyButton);
        _priceMultiplierLabel = itemView.findViewById(R.id.priceMultiplierLabel);

        Glide.with(itemView)
                .load(R.drawable.ui_graphic_coin)
                .into(_producedIcon);
    }

    public void setData(ConsumerDefinition definition,
                        FarmData farmData,
                        GameDataWrapper gameDataWrapper,
                        ICallback purchaseCallback) {

        _titleLabel.setText(definition.getName());
        _buyButton.setOnClickListener(clickedView -> purchaseCallback.onInvoke());
        _buyButton.setText(String.valueOf(definition.getCost()));
        _priceMultiplierLabel.setVisibility(View.VISIBLE);
        _priceMultiplierLabel.setText(NumberFormat.getPercentInstance().format(definition.getValueMultiplier()));

        String consumedResourceId = FarmData.findLowestCostOwnedResourceInList(
                definition.getConsumedIds(),
                gameDataWrapper,
                farmData.getInventory());
        if (TextUtils.isEmpty(consumedResourceId)) {
            consumedResourceId = definition.getConsumedIds().get(0);
        }

        ResourceDefinition consumedResource = gameDataWrapper.getResourceDefinition(consumedResourceId);

        if (consumedResource == null) {
            Glide.with(itemView)
                    .load(R.drawable.ic_dummy_resource)
                    .into(_consumedIcon);
        } else {
            Glide.with(itemView)
                    .load(gameDataWrapper.getImageReference(consumedResource.getPath()))
                    .into(_consumedIcon);
        }
    }
}
