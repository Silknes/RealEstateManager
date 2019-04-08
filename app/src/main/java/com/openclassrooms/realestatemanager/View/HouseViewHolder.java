package com.openclassrooms.realestatemanager.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.realestatemanager.R;

public class HouseViewHolder extends RecyclerView.ViewHolder {
    private TextView priceProperty, typeProperty, cityProperty;
    private ImageView photoProperty;

    public HouseViewHolder(View itemView) {
        super(itemView);

        priceProperty = itemView.findViewById(R.id.fragment_property_item_price_property);
        typeProperty = itemView.findViewById(R.id.fragment_property_item_type_property);
        cityProperty = itemView.findViewById(R.id.fragment_property_item_city_property);
        photoProperty = itemView.findViewById(R.id.fragment_property_item_photo_property);
    }
}
