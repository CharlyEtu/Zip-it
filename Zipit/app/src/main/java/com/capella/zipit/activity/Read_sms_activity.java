package com.capella.zipit.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.capella.zipit.R;

public class Read_sms_activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms_activity);

        TextView from_name = (TextView) findViewById(R.id.from_name);
        TextView from_number = (TextView) findViewById(R.id.from_number);
        TextView from_message = (TextView) findViewById(R.id.from_message);
        TextView from_date = (TextView) findViewById(R.id.from_date);

        from_name.setText(getIntent().getStringExtra("from_name"));
        from_number.setText(getIntent().getStringExtra("from_number"));
        from_message.setText(getIntent().getStringExtra("from_message"));
        from_date.setText(getIntent().getStringExtra("from_date"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_sms_activity, menu);

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
