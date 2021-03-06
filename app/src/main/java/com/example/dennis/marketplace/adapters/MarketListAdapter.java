package com.example.dennis.marketplace.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dennis.marketplace.R;
import com.example.dennis.marketplace.models.Market;
import com.example.dennis.marketplace.ui.MarketDetailActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dennis on 9/15/17.
 */

public class MarketListAdapter extends RecyclerView.Adapter<MarketListAdapter.MarketViewHolder>  {
    private ArrayList<Market> mMarket = new ArrayList<>();
    private Context mContext;

    //Now lets create a constructor
    public MarketListAdapter (Context context, ArrayList<Market> markets){
        mContext = context;
        mMarket = markets;
    }

    @Override
    public MarketListAdapter.MarketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_item, parent, false);
        MarketViewHolder viewHolder = new MarketViewHolder(view);
        return viewHolder;
    }

    //This is an onbind view
    @Override
    public void onBindViewHolder(MarketListAdapter.MarketViewHolder holder, int position) {
        holder.bindMarket(mMarket.get(position));
    }
    //get the amount of items here
    @Override
    public int getItemCount() {
        return mMarket.size();
    }


    public class MarketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.itemImageView)
        ImageView mImageView;

        @Bind(R.id.itemNameTextView) TextView mNameTextView;
        @Bind(R.id.priceTextView)
        TextView mPriceTextView;
        @Bind(R.id.stockTextView) TextView stocks;
        private Context mContext;

        public MarketViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        public void bindMarket(Market market) {
            mNameTextView.setText("Description: "+market.getName());
            Picasso.with(mContext).load(market.getImage()).into(mImageView);
            mPriceTextView.setText("Amount $: " + market.getSalePrice());
            stocks.setText("Stock: "+market.getStock());
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getLayoutPosition();
            Intent intent = new Intent(mContext, MarketDetailActivity.class);
            intent.putExtra("position", itemPosition);
            intent.putExtra("markets", Parcels.wrap(mMarket));
            mContext.startActivity(intent);
        }
    }

}
