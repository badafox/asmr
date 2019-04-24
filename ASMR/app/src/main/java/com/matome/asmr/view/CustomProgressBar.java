package com.matome.asmr.view;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ProgressBar;
import com.matome.asmr.R;

public class CustomProgressBar {
    View mView;
    ProgressBar progressBr;

    public CustomProgressBar(View view){
        mView = view;
    }

    public void init(){
        int colorId = mView.getResources().getColor(R.color.row_text);
        progressBr = (ProgressBar) mView.findViewById(R.id.contents_progress);
        progressBr.getIndeterminateDrawable().setColorFilter(colorId, PorterDuff.Mode.SRC_IN);
    }

    public void show(){
        progressBr.setVisibility(View.VISIBLE);
    }

    public void dismiss(){
        progressBr.setVisibility(View.GONE);
    }
}
