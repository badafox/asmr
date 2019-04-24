package com.matome.asmr.youtube;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.matome.asmr.EndlessRecyclerOnScrollListener;
import com.matome.asmr.R;
import com.matome.asmr.Util;
import com.matome.asmr.view.CustomProgressBar;

public class YoutubeFragment extends Fragment implements YouTubePlayer.OnInitializedListener
        , SwipeRefreshLayout.OnRefreshListener{
    public static final String YOUTUBE_API_KEY = "AIzaSyCUC5UclEmyzNyvqr-j4t0t4A_v5F0QUAg";
    static String TAG = "YoutubeFragment";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    YouTubePlayerSupportFragment youTubePlayerFragment;
    static Context mContext;

    static String videoId = "";
    static int MAX_LIST = 50;
    static YouTubePlayer youTubePlayer;
    View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    final static String YOUTUBE_SEARCH = "id,snippet";
    final static String YOUTUBE_SEARCH_FIELD = "items(id/kind,snippet/channelTitle,snippet/publishedAt,id/videoId,snippet/title,snippet/thumbnails/high/url)";
    final static String YOUTUBE_VIDEO_LIST_PART = "id,contentDetails,statistics";
    final static String YOUTUBE_VIDEO_LIST_FIELDS = "items(contentDetails/duration,statistics/viewCount)";
    static String word;
    static String order;
    static ArrayList<YouTubeData> youtubeArray;
    static DateTime dateTime;
    static RecyclerView mRecyclerView;
    static YoutubeRecyclerAdapter youtubeRecyclerAdapter;
    static boolean mEndless = false;

    private GridLayoutManager mLayoutManager;

    static CustomProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mEndless = false;

        rootView = inflater.inflate(R.layout.youtube, container, false);

        progressBar = new CustomProgressBar(rootView);
        progressBar.init();
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipelayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        youtubeArray = new ArrayList<>();
        dateTime = new DateTime(System.currentTimeMillis());
        setYoutubeList();
        setPlayer();
        mRecyclerView = rootView.findViewById(R.id.recycler);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        loadEndless();

        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ダイアログを表示する
                new CustomDialog().show(getActivity().getFragmentManager(), "a");
            }
        });
        return rootView;
    }

    private static void setYoutubeList(){
        // バックグラウンドで実行
        YouTubeTask youTubeTask = new YouTubeTask(mContext);
        youTubeTask.execute();
    }

    private void setPlayer(){
        Log.v(TAG," setPlayer " + "videoId=" + videoId);
        // YouTubeフラグメントインスタンスを取得
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        // レイアウトにYouTubeフラグメントを追加
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.player, youTubePlayerFragment).commit();
        // YouTubeフラグメントのプレーヤーを初期化する
        youTubePlayerFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            // YouTubeプレーヤーの初期化成功
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                //ここで動画の表示方法を設定する
                Log.v(TAG, "再生：videoId=" + videoId);
                player.cuePlaylist(videoId);
                youTubePlayer = player;
            }
            // YouTubeプレーヤーの初期化失敗
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                // YouTube error
                String errorMessage = error.toString();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                Log.e("errorMessage:", errorMessage);
            }
        });
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
    }
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }
    public static String convertDate(String dateString){
//        Sat, 18 Feb 2017 18:00:00 +0900
        //文字列 → Date型 変換
//        2017-03-26T13:02:17+09:00
//        String pattern = "yyyy-MM-dd\\ HH:mm:ss Z";
//        Fri, 07 Apr 2017 17:50:44 +0900
//        Log.v(TAG," before dateString = " + dateString);
        Matcher matcher = Pattern.compile("^[0-9]{4}").matcher(dateString);
        String pattern ;
        if(matcher.find()){
            pattern = "yyyy-MM-dd'T'HH:mm";
        }else{
            pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        }
        SimpleDateFormat sd1 = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date date = sd1.parse(dateString, new ParsePosition(0));
        //Date型 → 独自のフォーマット 変換
        SimpleDateFormat sd2 = new SimpleDateFormat("MM/dd/HH:mm", Locale.ENGLISH);
        String str = "";
        try{
            str = sd2.format(date);
        }catch(Exception e ){
            e.printStackTrace();
            str ="2015/1/1";
        }
        return str;
    }

    @Override
    public void onRefresh() {
        //xml情報を取得
        dateTime = new DateTime(System.currentTimeMillis());
        mEndless = false;
        youtubeArray.clear();
        setYoutubeList();
        // 更新処理を実装する
        // 2秒後にインジケータ非表示
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 更新が終了したらインジケータ非表示
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    /**
     * Converting ISO8601 formatted duration to normal readable time
     */
    public static String convertISO8601DurationToNormalTime(String isoTime)
    {
        String formattedTime = "";
        if (isoTime.contains("H") && isoTime.contains("M") && isoTime.contains("S")) {
            String hours = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("H"));
            String minutes = isoTime.substring(isoTime.indexOf("H") + 1, isoTime.indexOf("M"));
            String seconds = isoTime.substring(isoTime.indexOf("M") + 1, isoTime.indexOf("S"));
            formattedTime = hours + ":" + formatTo2Digits(minutes) + ":" + formatTo2Digits(seconds);
        } else if (!isoTime.contains("H") && isoTime.contains("M") && isoTime.contains("S")) {
            String minutes = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("M"));
            String seconds = isoTime.substring(isoTime.indexOf("M") + 1, isoTime.indexOf("S"));
            formattedTime = minutes + ":" + formatTo2Digits(seconds);
        } else if (isoTime.contains("H") && !isoTime.contains("M") && isoTime.contains("S")) {
            String hours = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("H"));
            String seconds = isoTime.substring(isoTime.indexOf("H") + 1, isoTime.indexOf("S"));
            formattedTime = hours + ":00:" + formatTo2Digits(seconds);
        } else if (isoTime.contains("H") && isoTime.contains("M") && !isoTime.contains("S")) {
            String hours = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("H"));
            String minutes = isoTime.substring(isoTime.indexOf("H") + 1, isoTime.indexOf("M"));
            formattedTime = hours + ":" + formatTo2Digits(minutes) + ":00";
        } else if (!isoTime.contains("H") && !isoTime.contains("M") && isoTime.contains("S")) {
            String seconds = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("S"));
            formattedTime = "0:" + formatTo2Digits(seconds);
        } else if (!isoTime.contains("H") && isoTime.contains("M") && !isoTime.contains("S")) {
            String minutes = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("M"));
            formattedTime = minutes + ":00";
        } else if (isoTime.contains("H") && !isoTime.contains("M") && !isoTime.contains("S")) {
            String hours = isoTime.substring(isoTime.indexOf("T") + 1, isoTime.indexOf("H"));
            formattedTime = hours + ":00:00";
        }
        return formattedTime;
    }

    /**
     * Makes values consist of 2 letters "01"
     */
    private static String formatTo2Digits(String str)
    {
        if (str.length() < 2) {
            str = "0" + str;
        }
        return str;
    }

    public static class CustomDialog extends DialogFragment {
        EditText freeWord;
        Dialog dialog;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            if (activity == null) {
                return super.onCreateDialog(savedInstanceState);
            }
            dialog = new Dialog(getActivity());

            if (dialog.getWindow() == null){
                return null;
            }

            //タイトルなし
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            dialog.setContentView(R.layout.youtube_search_dialog);
            //フリーキワードを取得する
            freeWord = dialog.findViewById(R.id.editText);
            RadioGroup group = dialog.findViewById(R.id.group);
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case R.id.new_order:
                            Util.setSave(getActivity(), "date", Util.YOUTUBE_ORDER);
                            break;
                        case R.id.popular_order:
                            Util.setSave(getActivity(), "viewCount", Util.YOUTUBE_ORDER);
                            break;
                        case R.id.no_order:
                            Util.setSave(getActivity(), "relevance", Util.YOUTUBE_ORDER);
                            break;
                    }
                }
            });
            //検索マーク
            freeWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        word =freeWord.getText().toString();
                        if(word.equals("")){
                            word = getString(R.string.youtube_word);
                        }
                        mEndless = false;
                        youtubeArray = new ArrayList<>();
                        Util.setSave(getActivity(), word, Util.YOUTUBE_WORD);
                        dateTime = new DateTime(System.currentTimeMillis());
                        setYoutubeList();

                        dismiss();
                    }
                    return true; // falseを返すと, IMEがSearch→Doneへと切り替わる
                }
            });
            //検索ボタン
            TextView ok = dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String word;
                    word =freeWord.getText().toString();
                    if(word.equals("")){
                        word = getString(R.string.youtube_word);
                    }
                    Util.setSave(getActivity(), word, Util.YOUTUBE_WORD);
                    mEndless = false;
                    youtubeArray = new ArrayList<>();
                    dateTime = new DateTime(System.currentTimeMillis());
                    setYoutubeList();
                    dismiss();
                }
            });
            //いいえボタン
            TextView cancel = dialog.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return dialog;
        }
    }

    private void loadEndless(){
        //エンドレスな読み込み
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                mEndless = true;
                setYoutubeList();
            }
        });
    }

    private static class YouTubeTask extends AsyncTask<Void, Void, ArrayList<YouTubeData>>{
        private Context mContext;

        private YouTubeTask (Context context){
            mContext = context;
        }


        @Override
        protected void onPreExecute() {
            progressBar.show();
        }

        @Override
        protected ArrayList<YouTubeData> doInBackground(Void... params) {
            try {
                YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                    @Override
                    public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                    }
                }).setApplicationName("youtube-cmdline-search-sample").build();

                //検索条件
                word = Util.getSearch(mContext, Util.YOUTUBE_WORD);
                order = Util.getOrder(mContext, Util.YOUTUBE_ORDER);
//                    YouTube.Search.List search = youtube.search().list(YOUTUBE_SEARCH);
//                    search.setKey(YOUTUBE_API_KEY);    // APIキーを設定
//                    search.setQ("シノアリス");   // 検索するキーワードを設定
//                    search.setType("video");  // 検索対象のTypeを設定。他にchannelとplaylistが設定できる
//                    search.setMaxResults((long)MAX_LIST); // 取得するヒット数の最大値を設定
//                    search.setOrder("date");//作成日順
//                    search.setFields(YOUTUBE_SEARCH_FIELD);
//                    SearchListResponse searchResponse = search.execute();
//                    List<SearchResult> searchResultList = searchResponse.getItems();
//                    Log.v(TAG,"youtube :" + searchResultList);

                YouTube.Search.List searchList;
                YouTube.Videos.List videosList;

                // Define the API request for retrieving search results.
                searchList = youtube.search().list(YOUTUBE_SEARCH);
                searchList.setKey(YOUTUBE_API_KEY);
                searchList.setQ(word);
                searchList.setType("video");
                searchList.setOrder(order);//作成日順
                searchList.setMaxResults((long)MAX_LIST);
                searchList.setFields(YOUTUBE_SEARCH_FIELD);
                videosList = youtube.videos().list(YOUTUBE_VIDEO_LIST_PART);
                videosList.setKey(YOUTUBE_API_KEY);
                videosList.setFields(YOUTUBE_VIDEO_LIST_FIELDS);

                searchList.setPublishedBefore(dateTime);
                SearchListResponse searchListResponse = searchList.execute();
                List<SearchResult> searchResults = searchListResponse.getItems();
                StringBuilder contentDetails = new StringBuilder();

                int ii = 0;
                for (SearchResult result : searchResults) {
                    if(ii == searchResults.size() - 1){
                        dateTime = result.getSnippet().getPublishedAt();
                    }
                    contentDetails.append(result.getId().getVideoId());
                    if (ii < searchResults.size() - 1) contentDetails.append(",");
                    ii++;
                }
                videosList.setId(contentDetails.toString());
                VideoListResponse resp = videosList.execute();
                List<Video> videoResults = resp.getItems();
                int index = 0;
                for (SearchResult result : searchResults) {
                    if(result.getId().getVideoId().equals(videoId)){
                        continue;
                    }else{
                        videoId = result.getId().getVideoId();
                    }

                    YouTubeData item = new YouTubeData();
                    //タイトル
                    item.setTitleText(result.getSnippet().getTitle());
                    //画像URL
                    item.setImageUrl(result.getSnippet().getThumbnails().getHigh().getUrl());
                    //ビデオID
                    item.setVideoId(result.getId().getVideoId());
                    String channelTitle = result.getSnippet().getChannelTitle();
                    //チャンネル名
                    item.setChannelTitle(channelTitle);

                    String date = result.getSnippet().getPublishedAt().toString();
                    //投稿日付
                    item.setPubData(convertDate(date));
                    // Video info
                    if (videoResults.get(index) != null) {
                        if (videoResults.get(index).getStatistics() != null) {
                            BigInteger viewsNumber = videoResults.get(index).getStatistics()
                                    .getViewCount();
                            String viewsFormatted = NumberFormat.getIntegerInstance().format
                                    (viewsNumber) + " views";
                            //再生回数
                            item.setViewCount(viewsFormatted);
                        }
                        String isoTime = videoResults.get(index).getContentDetails().getDuration();
                        String time = convertISO8601DurationToNormalTime(isoTime);
                        if(time.equals("0:00")){
                            time = "LIVE";
                        }
                        //再生時間
                        item.setPlayTime(time);
                    } else {
                        item.setPlayTime("NA");
                    }

                    // Add to the list
                    youtubeArray.add(item);
                    index++;
                }

            }catch(Exception e){
                e.printStackTrace();
                Log.e(TAG,"youtube :" + e);
            }
            return youtubeArray; // Void型なのでnull値を返しておく

        }
        @Override
        protected void onPostExecute(ArrayList<YouTubeData> list) {
            progressBar.dismiss();

            if(mEndless){
                youtubeRecyclerAdapter.notifyDataSetChanged();
            }else{
                youtubeRecyclerAdapter = null;
                youtubeRecyclerAdapter = new YoutubeRecyclerAdapter(list, mContext);
                mRecyclerView.setAdapter(youtubeRecyclerAdapter);
                try{
                    YouTubeData data = youtubeArray.get(0);
                    videoId = data.getVideoId();
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                        @Override
                        public void onLoading() {
                        }
                        @Override
                        public void onLoaded(String s) {
                            youTubePlayer.pause();
                        }
                        @Override
                        public void onAdStarted() {
                        }
                        @Override
                        public void onVideoStarted() {
                        }
                        @Override
                        public void onVideoEnded() {
                        }
                        @Override
                        public void onError(YouTubePlayer.ErrorReason errorReason) {
                            Log.e(TAG, "YoutubePlayerError:" + errorReason);
                        }
                    });
                }catch(Exception e){
                    Log.e(TAG, "YoutubePlayerError:" + e);
                }
            }
            mContext = null;
        }
    };
}
