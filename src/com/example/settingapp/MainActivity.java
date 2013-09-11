package com.example.settingapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int WPA = 0;
	private static final int WEP = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ContentResolver cr = getContentResolver();

		// 壁紙設定
		WallpaperManager wallman = WallpaperManager.getInstance(this);
		DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.google_favicon_128);
		Bitmap wallpaper = Bitmap.createScaledBitmap(bm, width, height, true);
		try {
			// resフォルダにある画像を壁紙にセットする
			wallman.clear();
			wallman.setBitmap(wallpaper);

//			wallman.suggestDesiredDimensions(width, height);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// wifi AP 追加
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled() == false) {
			wm.setWifiEnabled(true);
		}
		WifiConfiguration config = generateWifiConfig("ssid", "password", WPA);

		int networkId = wm.addNetwork(config); // 失敗した場合は-1となります
		if (networkId == -1) {
			Toast.makeText(this, "wifi setup error!", Toast.LENGTH_LONG).show();
		}
		wm.saveConfiguration();
		wm.updateNetwork(config);

		// bookmark 追加
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 50, os);
		byte[] bin = os.toByteArray();

		ContentValues values = new ContentValues();
		values.put(Browser.BookmarkColumns.TITLE, "google japan");
		values.put(Browser.BookmarkColumns.URL, "http://www.google.co.jp");
		values.put(Browser.BookmarkColumns.BOOKMARK, 1);
		values.put(Browser.BookmarkColumns.CREATED, 0);
		values.put(Browser.BookmarkColumns.FAVICON, bin);
		values.put(Browser.BookmarkColumns.DATE, 0);
		values.put("parentId", 3); // just for Chrome

		cr.insert(Uri.parse("content://com.android.chrome.ChromeBrowserProvider/bookmarks"), values);

		// マナーモードへ変更
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		// スクリーンオフの時間を2分に設定
		Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 2);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private WifiConfiguration generateWifiConfig(String ssid, String password, int mode) {
		WifiConfiguration config = new WifiConfiguration();
		if (mode == WPA) {
			config.SSID = "\"" + ssid + "\"";
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.preSharedKey = "\"" + password + "\"";
		} else if (mode == WEP) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.wepKeys[0] = "\"password\"";
			config.wepTxKeyIndex = 0;
		} else {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedAuthAlgorithms.clear();
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		}
		return config;
	}

}
