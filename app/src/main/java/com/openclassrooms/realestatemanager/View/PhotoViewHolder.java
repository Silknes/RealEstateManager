package com.openclassrooms.realestatemanager.View;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.realestatemanager.Model.Photo;
import com.openclassrooms.realestatemanager.R;

public class PhotoViewHolder extends RecyclerView.ViewHolder{
    private ImageView photoView;
    private TextView descriptinoView;

    public PhotoViewHolder(@NonNull View itemView) {
        super(itemView);

        photoView = itemView.findViewById(R.id.fragment_detail_property_item_image);
        descriptinoView = itemView.findViewById(R.id.fragment_detail_property_item_description);
    }


    public void updateWithPhoto(Photo photo, RequestManager glide){
        glide.load(photo.getUriPhoto())
                .apply(new RequestOptions()
                        .transform(new CenterCrop()))
                .into(photoView);

        descriptinoView.setText(photo.getDescription());
    }
}
