package com.example.tickerwatchlistmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {

    // Regex pattern for case-insensitive matching of Ticker:<<SYMBOL>>
    private static final Pattern TICKER_PATTERN =
            Pattern.compile("(?i)^\\s*Ticker\\s*:\\s*<<\\s*([A-Za-z]+)\\s*>>\\s*$");

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        String format = bundle.getString("format");

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            handleMessage(context, sms.getMessageBody());
        }
    }

    private void handleMessage(Context context, String message) {
        if (message == null) return;

        Matcher matcher = TICKER_PATTERN.matcher(message.trim());
        if (matcher.find()) {
            String ticker = matcher.group(1).toUpperCase();

            if (ticker.matches("^[A-Z]{1,5}$")) {
                launchMainActivity(context, ticker);
            } else {
                showError(context, "Invalid ticker format: " + ticker);
            }
        } else {
            showError(context, "No valid watchlist entry found");
        }
    }

    private void launchMainActivity(Context context, String ticker) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("NEW_TICKER", ticker);
        intent.putExtra("SHOW_TICKER", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        Toast.makeText(context, "Added ticker: " + ticker, Toast.LENGTH_SHORT).show();
    }

    private void showError(Context context, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("ERROR_MESSAGE", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}