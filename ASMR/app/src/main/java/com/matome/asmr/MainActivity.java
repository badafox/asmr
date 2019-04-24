package com.matome.asmr;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.matome.asmr.nend.NendConstructor;
import com.matome.asmr.youtube.YoutubeFragment;

import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;


public class MainActivity extends AppCompatActivity {

    private boolean FinishFlag = false;
    private DrawerLayout drawerLayout;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication application = new MyApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.analytics_event_category))
                .build());

        //ナビゲーションバーの透過
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appbar_layout);

        //Nendの初期化
        NendConstructor nendConstructor = new NendConstructor(this);
        nendConstructor.init();

        //フラグメント切り替え
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(savedInstanceState == null){
            transaction.replace(R.id.fragment, youtubeFragment);
            transaction.commit();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);

        //FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //メニューの設定
        String[] tabName = getResources().getStringArray(R.array.tab_name);
        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList(tabName));
        arrayList.add("");
        arrayList.add(0, getString(R.string.sns1));

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), R.layout.menu_navigation, R.id.nav_text, arrayList);
        final ListView menuList = (ListView) findViewById(R.id.navigation_list);
        menuList.setAdapter(adapter);
        menuList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                        if(position == 0){
                            String message= parent.getContext().getString(R.string.sns2);
                            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(MainActivity.this);
                            builder.setChooserTitle("共有するアプリを選択");
                            builder.setStream(Uri.parse("android.resource://com.matome.asmr/" + R.drawable.share));
                            builder.setText(message);
                            builder.setType("image/jpeg");
                            builder.startChooser();
                        }else{
                            String word = "ASMR " + parent.getItemAtPosition(position);
                            Util.setSave(parent.getContext(), word, Util.YOUTUBE_WORD);

                            YoutubeFragment youtubeFragment = new YoutubeFragment();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.fragment,youtubeFragment );
                            transaction.commit();
                        }
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
        );

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (FinishFlag){
            super.onBackPressed();
            return;

        } else {
            Toast.makeText(this, "アプリを終了する場合は、もう一度バックボタンを押してください", Toast.LENGTH_SHORT).show();
            FinishFlag = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FinishFlag = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        Util.setSave(getApplicationContext(), getString(R.string.youtube_order), Util.YOUTUBE_ORDER);
        Util.setSave(getApplicationContext(), getString(R.string.youtube_word), Util.YOUTUBE_WORD);
        super.onDestroy();

    }
}

