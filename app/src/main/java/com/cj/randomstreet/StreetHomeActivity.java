package com.cj.randomstreet;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.cj.randomstreet.service.StreetService;

public class StreetHomeActivity extends AppCompatActivity implements StreetService.StreetSuccessListener {

    Button streetButton;
    TextView streetTextView;
    private StreetService streetService;
    View progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_home);

        setUpLayoutResources();
        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {
        streetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make service call to retrieve street
                if (isNetworkAvailable(StreetHomeActivity.this)) {
                    if (streetService == null) {
                        streetService = new StreetService(StreetHomeActivity.this,
                                StreetHomeActivity.this, progressView);
                    }
                    streetService.getRandomStreet();
                } else {
                    showNoConnectionDialog();
                }
            }
        });
    }

    private void showNoConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.no_connection_error_message);
        builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }
        return false;
    }


    private void setUpLayoutResources() {
        streetButton = (Button) findViewById(R.id.street_choice_button);
        streetTextView = (TextView) findViewById(R.id.street_choice_textview);
        progressView = findViewById(R.id.progress_view);
    }

    @Override
    public void displayStreetResult(String street) {
        streetTextView.setText(street);
    }
}
