package com.example.tickerwatchlistmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    // Pattern to match valid ticker format: Ticker:<<SYMBOL>>
    private static final String TICKER_PATTERN = "Ticker:<<([^>]+)>>";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = smsMessage.getMessageBody();
                    processSmsMessage(context, messageBody);
                }
            }
        }
    }

    private void processSmsMessage(Context context, String message) {
        // Check if message matches our pattern
        if (message.matches("Ticker:<<([^>]+)>>")) {
            // Extract the ticker symbol from between << >>
            String ticker = extractTickerFromMessage(message);

            if (isValidTicker(ticker)) {
                // Valid ticker - launch activity with the new ticker
                launchMainActivityWithTicker(context, ticker.toUpperCase());
            } else {
                // Invalid ticker format
                launchMainActivityWithError(context, "Invalid ticker format: " + ticker);
            }
        } else {
            // Message doesn't match required format
            launchMainActivityWithError(context, "No valid watchlist entry found");
        }
    }

    private String extractTickerFromMessage(String message) {
        // Extract content between << and >>
        int start = message.indexOf("<<") + 2;
        int end = message.indexOf(">>");
        if (start >= 2 && end > start) {
            return message.substring(start, end);
        }
        return "";
    }

    private boolean isValidTicker(String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            return false;
        }

        // Check if ticker contains only letters (no numbers or symbols)
        // Convert to uppercase for validation
        String upperTicker = ticker.toUpperCase();

        // Valid ticker should contain only letters A-Z
        if (!upperTicker.matches("[A-Z]+")) {
            return false;
        }

        // Additional validation - ticker should be reasonable length
        return upperTicker.length() >= 1 && upperTicker.length() <= 5;
    }

    private void launchMainActivityWithTicker(Context context, String ticker) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("NEW_TICKER", ticker);
        intent.putExtra("SHOW_TICKER", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        // Show success toast
        Toast.makeText(context, "Added ticker: " + ticker, Toast.LENGTH_SHORT).show();
    }

    private void launchMainActivityWithError(Context context, String errorMessage) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("ERROR_MESSAGE", errorMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        // Show error toast
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    }
}