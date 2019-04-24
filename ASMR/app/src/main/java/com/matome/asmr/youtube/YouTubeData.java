package com.matome.asmr.youtube;

public class YouTubeData {

    String imageUrl;
    String pubData ;
    String titleText;
    String channelTitle;
    String playTime;
    String videoId;
    String viewCount;

    public void setImageUrl(String url) {
        imageUrl = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setPubData(String t) {
        pubData = t;
    }

    public String getPubData() {
        return pubData;
    }

    public void setTitleText(String t) {
        titleText = t;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setChannelTitle(String t) {
        channelTitle = t;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setPlayTime(String t) {
        playTime = t;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setVideoId(String t) {
        videoId = t;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setViewCount(String t) {
        viewCount = t;
    }

    public String getViewCount() {
        return viewCount;
    }

}