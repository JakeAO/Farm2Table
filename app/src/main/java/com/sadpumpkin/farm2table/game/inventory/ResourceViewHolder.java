package com.sadpumpkin.farm2table.game.inventory;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sadpumpkin.farm2table.R;
import com.sadpumpkin.farm2table.util.GameDataWrapper;
import com.sadpumpkin.farm2table.util.ResourceDefinition;

public class ResourceViewHolder extends RecyclerView.ViewHolder {

    private ImageView _iconImage;
    private TextView _titleLabel;
    private TextView _countLabel;

    public ResourceViewHolder(@NonNull View itemView) {
        super(itemView);

        _iconImage = itemView.findViewById(R.id.iconImage);
        _titleLabel = itemView.findViewById(R.id.titleLabel);
        _countLabel = itemView.findViewById(R.id.countLabel);
    }

    public void setData(ResourceDefinition definition, Long count, GameDataWrapper gameDataWrapper) {
        Glide.with(itemView)
                .load(gameDataWrapper.getImageReference(definition.getPath()))
                .into(_iconImage);
        _titleLabel.setText(definition.getName());
        _countLabel.setText(String.valueOf(count));
    }
}
