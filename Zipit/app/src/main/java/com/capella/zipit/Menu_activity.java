package com.capella.zipit;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class Menu_activity extends ActionBarActivity {

	private Button btn_local = null;
	private Button btn_sd = null;
	private Button btn_sms_W = null;
	private Button btn_sms_R =null;
	private Button btn_picures = null;
	private Button btn_videos = null;
	private Button btn_contacts = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_activity);

		//On récupère tous les boutons du menu
		btn_local = (Button) findViewById(R.id.btn_internal);
		btn_sd = (Button) findViewById(R.id.btn_sd);
		btn_sms_W = (Button) findViewById(R.id.btn_write_sms);
		btn_sms_R = (Button) findViewById(R.id.btn_read_sms);
		btn_picures = (Button) findViewById(R.id.btn_pictures);
		btn_videos = (Button) findViewById(R.id.btn_videos);
		btn_contacts = (Button) findViewById(R.id.btn_contacts);

		//on attribue un listener adapté aux vues
		View.OnClickListener localListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);
				intent.putExtra("menuchoice", Environment.getExternalStorageDirectory().getAbsolutePath());
				Menu_activity.this.startActivity(intent);
			}
		};

		View.OnClickListener sdListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);
				intent.putExtra("menuchoice", "/storage/extSdCard");
				Menu_activity.this.startActivity(intent);
			}
		};

		btn_local.setOnClickListener(localListener);
		btn_sd.setOnClickListener(sdListener);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_menu_activity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
