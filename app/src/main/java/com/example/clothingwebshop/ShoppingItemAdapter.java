package com.example.clothingwebshop;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private Context mContext;
    private ArrayList<ShoppingItem> mShoppingItemsData;
    private ArrayList<ShoppingItem> mShoppingItemsDataAll;
    private int lastPosition = -1;
    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mShoppingItemsDataAll.size();
                results.values = mShoppingItemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(ShoppingItem item: mShoppingItemsDataAll){
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mShoppingItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };


    ShoppingItemAdapter(Context context, ArrayList<ShoppingItem> itemsData){
        this.mContext = context;
        this.mShoppingItemsData = itemsData;
        this.mShoppingItemsDataAll = itemsData;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        ShoppingItem currentItem = mShoppingItemsData.get(position);

        holder.bindTo(currentItem);

       if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.animation_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
       }

    }

    @Override
    public int getItemCount() {
        return mShoppingItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return shoppingFilter;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImage;
        private TextView mTitle;
        private RatingBar mRatingBar;
        private TextView mInfo;
        private TextView mPrice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemImage = itemView.findViewById(R.id.itemImage);
            mTitle = itemView.findViewById(R.id.itemTitle);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
            mInfo = itemView.findViewById(R.id.itemInfo);
            mPrice = itemView.findViewById(R.id.itemPrice);


        }

        public void bindTo(ShoppingItem currentItem) {
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            mTitle.setText(currentItem.getName());
            mRatingBar.setRating(currentItem.getRated());
            mInfo.setText(currentItem.getInfo());
            mPrice.setText(currentItem.getPrice());

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view ->
                ((ShopListActivity) mContext).updateAlertIcon(currentItem));
            itemView.findViewById(R.id.delete).setOnClickListener(view ->
                ((ShopListActivity) mContext).deleteItem(currentItem));

        }
    }
}


