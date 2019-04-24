package com.matome.asmr.nend;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import net.nend.android.NendAdNative;
import net.nend.android.NendAdNativeMediaView;
import net.nend.android.NendAdNativeMediaViewListener;
import net.nend.android.NendAdNativeVideo;
import net.nend.android.NendAdNativeVideoLoader;
import net.nend.android.NendAdNativeViewBinder;

import com.matome.asmr.R;

@SuppressWarnings("serial")
public class NendConstructor {
	final String TAG = "NendConstructor";
	AppCompatActivity mActivity;
	private MyNendAdViewBinder videoBinder;
	private NendAdNativeViewBinder normalBinder;
	final int NATIVE_VIDEO_SPOT_ID = 887591;
	final String NATIVE_VIDEO_API_KEY = "a284d892c3617bf5705facd3bfd8e9934a8b2491";

	public NendConstructor(AppCompatActivity activity) {
		super();
		mActivity = activity;
	}

	public void init(){
		videoBinder = new MyNendAdViewBinder.Builder()
				.mediaViewId(R.id.native_video_ad_mediaview)
//				.logoImageId(R.id.native_video_ad_logo_image)
				.titleId(R.id.native_video_ad_title)
//				.contentId(R.id.native_video_ad_content)
//				.advertiserId(R.id.native_video_ad_advertiser)
				.actionId(R.id.native_video_ad_action)
				.build();
		normalBinder = new NendAdNativeViewBinder.Builder()
//				.adImageId(R.id.native_video_ad_image)
//				.logoImageId(R.id.native_video_ad_logo_image)
				.titleId(R.id.native_video_ad_title)
//				.contentId(R.id.native_video_ad_content)
				.prId(R.id.native_video_ad_pr, NendAdNative.AdvertisingExplicitly.SPONSORED)
//				.promotionNameId(R.id.native_video_ad_advertiser)
				.actionId(R.id.native_video_ad_action)
				.build();
		final View itemView = mActivity.findViewById(R.id.include_native_inflater_video);

		NendAdNativeVideoLoader videoLoader = new NendAdNativeVideoLoader(mActivity, NATIVE_VIDEO_SPOT_ID, NATIVE_VIDEO_API_KEY);
		videoLoader.setFillerImageNativeAd(NATIVE_VIDEO_SPOT_ID, NATIVE_VIDEO_API_KEY);

		videoLoader.loadAd(new NendAdNativeVideoLoader.Callback() {
			@Override
			public void onSuccess(NendAdNativeVideo ad) {
				Log.d(TAG, "onSuccess: " + ad.getTitleText());
				TextView detailText = (TextView)mActivity.findViewById(R.id.native_video_ad_action);
				detailText.setVisibility(View.VISIBLE);

				if (ad.hasVideo()) {
					MyNendAdViewHolder holder = new MyNendAdViewHolder(itemView, videoBinder);
					videoBinder.renderView(holder, ad, new NendAdNativeMediaViewListener() {
						@Override
						public void onStartPlay(NendAdNativeMediaView nendAdNativeMediaView) {

						}

						@Override
						public void onStopPlay(NendAdNativeMediaView nendAdNativeMediaView) {

						}

						@Override
						public void onCompletePlay(NendAdNativeMediaView nendAdNativeMediaView) {

						}

						@Override
						public void onOpenFullScreen(NendAdNativeMediaView nendAdNativeMediaView) {

						}

						@Override
						public void onCloseFullScreen(NendAdNativeMediaView nendAdNativeMediaView) {

						}

						@Override
						public void onError(int i, String s) {

						}
					});
				} else {
					mActivity.findViewById(R.id.native_video_ad_mediaview).setVisibility(View.GONE);
					mActivity.findViewById(R.id.native_video_ad_pr).setVisibility(View.VISIBLE);

					NendAdNative adNormalNative = ad.getFallbackAd();
					adNormalNative.intoView(itemView, normalBinder);
				}
			}

			@Override
			public void onFailure(int error) {
				Log.e(TAG, "NendError: " + error);
			}
		});

	}


}

