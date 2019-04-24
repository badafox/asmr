package com.matome.asmr.youtube;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.matome.asmr.R;

public class YoutubeRecyclerAdapter extends RecyclerView.Adapter<YoutubeRecyclerAdapter.BaseViewHolder> {
    private final String TAG = "YoutubeRecyclerAdapter";
    private LayoutInflater mInflater;
    private ArrayList<YouTubeData> mData;
    private Context mContext;
//    private final int LIST_AD_DATA = 1000;
    private final int CONTENT = 0;

    public YoutubeRecyclerAdapter(ArrayList<YouTubeData> data, Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mData = data;
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        // 表示するレイアウトを設定
        switch (viewType) {
            case CONTENT:
                return new ListViewHolder(mInflater.inflate(R.layout.youtube_list_row, parent, false));
        }
        return null;
    }
    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CONTENT:
                // 特定の行(position)のデータを得る
//                YouTubeData item = mData.get(getRealPosition(position));
                YouTubeData item = mData.get(position);

                //タイトル
                holder.memberTitleText.setText(item.getTitleText());
                //チャンネル名
                holder.channelName.setText(item.getChannelTitle());
                //公開日
                holder.memberPubDate.setText(item.getPubData());
                //再生時間
                holder.timeText.setText(item.getPlayTime());
                //再生回数
                holder.viewCount.setText(item.getViewCount());
                //サムネイル画像取得
                holder.url = item.getImageUrl();

                try{
                    if(item.getImageUrl() != null){
                        Picasso.with(mContext).cancelRequest(holder.memberImage);
                        Picasso.with(mContext).load(holder.url)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .fit()
                                .centerCrop()
                                .into(holder.memberImage);
                        holder.memberImage.setVisibility(View.VISIBLE);
//                        Picasso.with(mContext).setIndicatorsEnabled(true);
                    }else{
                        //課題:スクロールすると間違った位置に画像がセットされる
                        //Picassoで使用しているsetImageDrawableにnullを入れる
                        holder.memberImage.setImageDrawable(null);
                    }
                }catch (Exception e){
                    //課題:スクロールすると間違った位置に画像がセットされる
                    //Picassoで使用しているsetImageDrawableにnullを入れる
                    holder.memberImage.setImageDrawable(null);
                    Log.e(TAG," not Path = " + item.getImageUrl());
                }
                Picasso.with(mContext).invalidate(item.getImageUrl());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        YouTubeData data = mData.get(getRealPosition(holder.getAdapterPosition()));
                        YouTubeData data = mData.get(holder.getAdapterPosition());

                        Log.e(TAG,"data=" + data.getTitleText());
                        String videoId = data.getVideoId();
                        Log.e(TAG,"videoId=" + videoId);
                        YoutubeFragment.youTubePlayer.loadVideo(videoId);
                        YoutubeFragment.youTubePlayer.play();
                    }
                });
                break;

            default:
                break;
        }
    }
//    private int getRealPosition(int position) {
//        if (LIST_AD_DATA == 0) {
//            return position;
//        } else {
//            return position - position / LIST_AD_DATA ;
//        }
//    }
    @Override
    public int getItemCount() {

        return mData.size();
//        int adContent = 0;
//        if (mData != null && mData.size() > 0 && LIST_AD_DATA > 0) {
//            adContent = mData.size() / LIST_AD_DATA;
//        }
//        if(mData != null){
//            return mData.size() + adContent;
//        }else {
//            return 0;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return CONTENT;
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder {
        ImageView memberImage;
        TextView memberPubDate;
        TextView memberTitleText;
        TextView channelName;
        TextView viewCount;
        TextView timeText;
        String url ;

        public BaseViewHolder(View v) {
            super(v);
        }
    }

    public class ListViewHolder extends BaseViewHolder{
        public ListViewHolder(View v) {
            super(v);
            memberImage = v.findViewById(R.id.row_image);
            memberTitleText = v.findViewById(R.id.title);
            memberPubDate = v.findViewById(R.id.date_text);
            channelName = v.findViewById(R.id.channel_text);
            timeText = v.findViewById(R.id.time_text);
            viewCount = v.findViewById(R.id.view_count);
        }
    }
}
