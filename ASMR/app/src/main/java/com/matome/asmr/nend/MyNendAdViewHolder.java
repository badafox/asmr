package com.matome.asmr.nend;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.nend.android.NendAdNativeMediaView;
import net.nend.android.NendAdNativeMediaViewListener;
import net.nend.android.NendAdNativeVideo;
import net.nend.android.NendAdNativeViewBinder;

public class MyNendAdViewHolder extends RecyclerView.ViewHolder {

    public View itemView;
    //Common
    public ImageView logoImageView;
    public TextView titleTextView;
    public TextView contentTextView;
    public TextView prTextView;
    public TextView actionTextView;

    //Image
    public NendAdNativeViewBinder normalBinder;
    public NendAdNativeMediaView adNativeMediaView;
    public TextView advertiserNameTextView;
    public LinearLayout ratingStarsLinearLayout;
    public TextView ratingCountTextView;
    //Video
    private MyNendAdViewBinder videoBinder;

    public MyNendAdViewHolder(final View itemView, MyNendAdViewBinder viewBinder) {
        super(itemView);
        if (viewBinder == null) {
            return;
        }

        this.itemView = itemView;
        this.videoBinder = viewBinder;

        try {
            adNativeMediaView = (NendAdNativeMediaView) itemView.findViewById(viewBinder.getMediaViewId());
            logoImageView = (ImageView) itemView.findViewById(viewBinder.getLogoImageId());
            titleTextView = (TextView) itemView.findViewById(viewBinder.getTitleId());
            contentTextView = (TextView) itemView.findViewById(viewBinder.getContentId());
            advertiserNameTextView = (TextView) itemView.findViewById(viewBinder.getAdvertiserId());
            ratingStarsLinearLayout = (LinearLayout) itemView.findViewById(viewBinder.getRatingId());
            ratingCountTextView = (TextView) itemView.findViewById(viewBinder.getRatingCountId());
            actionTextView = (TextView) itemView.findViewById(viewBinder.getActionId());
        } catch (ClassCastException exception) {
            logoImageView = null;
            titleTextView = null;
            contentTextView = null;
            prTextView = null;
            ratingStarsLinearLayout = null;
            ratingCountTextView = null;
            actionTextView = null;
        }
    }

    public MyNendAdViewHolder(final View itemView, NendAdNativeViewBinder viewBinder) {
        super(itemView);
        if (viewBinder == null) {
            return;
        }

        this.itemView = itemView;
        this.normalBinder = viewBinder;

        try {
            logoImageView = (ImageView) itemView.findViewById(viewBinder.getLogoImageId());
            titleTextView = (TextView) itemView.findViewById(viewBinder.getTitleId());
            contentTextView = (TextView) itemView.findViewById(viewBinder.getContentId());
            prTextView = (TextView) itemView.findViewById(viewBinder.getPrId());
            actionTextView = (TextView) itemView.findViewById(viewBinder.getActionId());
        } catch (ClassCastException exception) {
            logoImageView = null;
            titleTextView = null;
            contentTextView = null;
            prTextView = null;
            actionTextView = null;
        }
    }

    public void renderView(NendAdNativeVideo ad, NendAdNativeMediaViewListener listener) {
        videoBinder.renderView(this, ad, listener);
    }
}
