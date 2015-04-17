package edu.rit.scavengerhuntglass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    /**
     * {@link com.google.android.glass.widget.CardScrollView} to use as the main content view.
     */
    private List<CardBuilder> mCards;
    private CardScrollView mCardScrollView;
    private GameCardScrollAdapter mAdapter;

    private CardBuilder mainCard;
    private CardBuilder clueLifeline;
    private CardBuilder tempLifeline;
    private CardBuilder skipLifeline;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        createCards();

        mCardScrollView = new CardScrollView(this);
        mAdapter = new GameCardScrollAdapter();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setupClickListener();
        setContentView(mCardScrollView);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        mainCard = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Target 1, Clue 1: This would be the first clue.")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");

        clueLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Clue+")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");

        tempLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Temp")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");

        skipLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Skip")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");


        mCards.add(mainCard);
        mCards.add(clueLifeline);
        mCards.add(tempLifeline);
        mCards.add(skipLifeline);

    }

    private class GameCardScrollAdapter extends CardScrollAdapter {

        public int getCount() {
            return mCards.size();
        }

        public Object getItem(int position) {
            return mCards.get(position);
        }

        public int getViewTypeCount() {
            return CardBuilder.getViewTypeCount();
        }

        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        public int getItemViewType(int position) {
            return mCards.get(position).getItemViewType();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }

    private void setupClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if it's the main card, open up the QR code scanner
                if (position == 0) {
                    scan();
                }
                else if (position == 1) {
                    mainCard.setText("Target 1, Clue 2: This would be the second clue.");
                    mCardScrollView.setSelection(0);
                }
                else if (position == 2) {
                    mCardScrollView.setSelection(0);
                    view.setBackgroundColor(Color.RED);

                }
                else if (position == 3) {
                    mainCard.setText("Target 2, Clue 1: This would be the next target clue.");
                    mCardScrollView.setSelection(0);

                    //remove the skip lifeline
                    mCards.remove(3);

                }
            }
        });
    }

    private void scan() {

        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE","QR_CODE_MODE");
        startActivityForResult(intent, 0);

    }

    //when a QR code is read, it will send a result code
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {

            String contents = intent.getStringExtra("SCAN_RESULT");
            //String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

            Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();

            // Handle successful scan

        } else if (resultCode == RESULT_CANCELED) {
            // Handle cancel
            Log.i("App", "Scan unsuccessful");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCardScrollView.activate();
    }

    @Override
    protected void onPause() {
        mCardScrollView.deactivate();
        super.onPause();
    }
}
