package com.capella.zipit.activity;

import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.capella.zipit.R;
import com.capella.zipit.tools.Zipper;

public class Read_sms_activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms_activity);



        TextView from_name = (TextView) findViewById(R.id.from_name);
        TextView from_number = (TextView) findViewById(R.id.from_number);
        TextView from_message = (TextView) findViewById(R.id.from_message);
        TextView from_date = (TextView) findViewById(R.id.from_date);

        ImageButton btn_envoi_compressed = (ImageButton) findViewById(R.id.btn_compression_send);
        ImageButton btn_envoi_normal = (ImageButton) findViewById(R.id.btn_send);

        from_name.setText(getIntent().getStringExtra("from_name"));
        from_number.setText(getIntent().getStringExtra("from_number"));


        String decomp = "";

        try {
            decomp = Zipper.DeCompresserString(getIntent().getStringExtra("from_message"));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Probleme de decompression du contenu", Toast.LENGTH_SHORT).show();
        }
        if(!decomp.isEmpty())
            from_message.setText(decomp);
        else
            from_message.setText(getIntent().getStringExtra("from_message"));

        from_date.setText(getIntent().getStringExtra("from_date"));

        //on attribue un listener adapté aux vues
        btn_envoi_compressed.setOnClickListener(envoiCompressedListener);
        btn_envoi_normal.setOnClickListener(envoiNormalListener);



    }

    /*listener sur bouton envoi compressé*/
    private View.OnClickListener envoiCompressedListener = new View.OnClickListener() {
        /**
         * Methode qui permet de lancer l'activité ecrire un sms compressé (Write_sms_activity.class)
         * @Override
         */
        public void onClick(View v) {
            Intent intent = new Intent(Read_sms_activity.this, Write_sms_activity.class);/*instancition d'un intent (lien vers autre activité) => Write_sms_activity.class*/
            intent.putExtra("desinataire", getIntent().getStringExtra("from_number"));/*affectation d'un parametre a l'intent*/
            Read_sms_activity.this.startActivity(intent);/*lance l'intent*/
        }
    };




    /*listener sur bouton envoi normal*/
    private View.OnClickListener envoiNormalListener = new View.OnClickListener() {
        /**
         * Methode qui permet de lancer l'activité ecrire sms noramle (appli android)
         * @Override
         */
        public void onClick(View v) {
            Uri uri = Uri.parse("smsto:"+getIntent().getStringExtra("from_number"));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(intent);
        }
    };

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