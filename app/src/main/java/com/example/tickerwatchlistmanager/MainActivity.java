package com.example.tickerwatchlistmanager;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements TickerListFragment.OnTickerSelectedListener {

    private TickerListFragment tickerListFragment;
    private InfoWebFragment infoWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tickerListFragment = (TickerListFragment) getSupportFragmentManager().findFragmentById(R.id.tickerListFragment);
        infoWebFragment = (InfoWebFragment) getSupportFragmentManager().findFragmentById(R.id.infoWebFragment);

        processIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIncomingIntent(intent);
    }

    private void processIncomingIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        if (intent.hasExtra("NEW_TICKER")) {
            String newTicker = intent.getStringExtra("NEW_TICKER");
            boolean showTicker = intent.getBooleanExtra("SHOW_TICKER", false);

            if (tickerListFragment != null && newTicker != null) {
                tickerListFragment.addOrUpdateTicker(newTicker);

                if (showTicker && infoWebFragment != null) {
                    infoWebFragment.loadTicker(newTicker);
                }
            }
        } else if (intent.hasExtra("ERROR_MESSAGE")) {
            String errorMessage = intent.getStringExtra("ERROR_MESSAGE");
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTickerSelected(String ticker) {
        if (infoWebFragment != null) {
            infoWebFragment.loadTicker(ticker);
        }
    }
}
