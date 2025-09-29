package com.example.tickerwatchlistmanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class TickerListFragment extends Fragment {
    private ArrayList<String> tickers = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private OnTickerSelectedListener listener;

    public interface OnTickerSelectedListener {
        void onTickerSelected(String ticker);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTickerSelectedListener) {
            listener = (OnTickerSelectedListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticker_list, container, false);

        ListView listView = view.findViewById(R.id.ticker_list_view);
        if (tickers.isEmpty()) {
            tickers.add("NEE");
            tickers.add("AAPL");
            tickers.add("DIS");
        }
        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                tickers
        );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, v, position, id) -> {
            if (listener != null) {
                listener.onTickerSelected(tickers.get(position));
            }
        });
        return view;
    }

    public void addTicker(String ticker) {
        // Avoid duplicates
        if (tickers.contains(ticker)) return;
        if (tickers.size() < 6) {
            tickers.add(ticker);
        } else {
            // Replace the 6th ticker
            tickers.set(5, ticker);
        }
        adapter.notifyDataSetChanged();
    }
}
