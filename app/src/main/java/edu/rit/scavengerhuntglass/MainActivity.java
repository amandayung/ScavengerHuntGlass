package edu.rit.scavengerhuntglass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
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

    private CardBuilder loginCard;
    private CardBuilder mainCard;
    private CardBuilder clueLifeline;
    private CardBuilder tempLifeline;
    private CardBuilder skipLifeline;

    static final int QR_SCAN_RESULT = 0;

    boolean gameStarted;
    boolean hint;
    public String[][] location_clues;
    public String[] location_QR;
    public double[] location_lat;
    public double[] location_long;
    public int target_id;
    public int clue_id;
    double userLog;
    double userLat;
    boolean gps = false;
    int counter =0;
    GPSTemperature gpsTemp;
    private View view;
    protected LocationManager locationManager;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            userLog = location.getLongitude();
            userLat = location.getLatitude();
            doGpsView(userLat,userLog);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    LocationManager lm;
    Location location;

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

        loginCard = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Tap when you're ready to begin!")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");


        mainCard = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.activity_main)
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


        mCards.add(loginCard);
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
                if (position == 0 && !gameStarted) {
                    mCards.add(mainCard);
                    mCards.add(clueLifeline);
                    mCards.add(tempLifeline);
                    mCards.add(skipLifeline);
                    mCards.remove(0);
                    gameStarted = true;
                    mAdapter.notifyDataSetChanged();

                }
                else if (position == 0 && gameStarted) {
                    scan();
                }
                else if (position == 1) {
                    View mainView = mainCard.getView();
                    TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
                    //Toast.makeText(getApplicationContext(), clue.getText(), Toast.LENGTH_LONG).show();
                    clue.setText("This would be the second clue to the first target.");

                    //mainCard.setText("Target 1, Clue 2: This would be the second clue.");
                    //View mainCard = mAdapter.getView(0, view, parent);
                    //showNextClue(mainCard);
                    mCardScrollView.setSelection(0);
                    //mAdapter.notifyDataSetChanged();
                }
                else if (position == 2) {
                   //this currently changes the background of the Clue+ card, not sure why...
                    View mainView = mainCard.getView();
                    mainView.setBackgroundColor(Color.RED);
                    //turnOnGPS(mainCard);
                    mCardScrollView.setSelection(0);
                    mAdapter.notifyDataSetChanged();

                }
                else if (position == 3) {
                    View mainView = mainCard.getView();
                    TextView target = (TextView) mainView.findViewById(R.id.current_target);
                    TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
                    target.setText("Target: 2/10");
                    clue.setText("This would be the first clue to the second target.");

                    //mainCard.setText("Target 2, Clue 1: This would be the next target clue.");
                    //skip(view);
                    mCardScrollView.setSelection(0);

                    //remove the skip lifeline
                    mCards.remove(3);
                    mAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    private void scan() {

        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE","QR_CODE_MODE");
        startActivityForResult(intent, QR_SCAN_RESULT);

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



    /*
 *  Shows the first clue.
 * */
    public void showFirstClue(View v) {
        /*TextView clue = (TextView)v;
        clue.setText(location_clues[target_id][0]);
        View cluePlus = findViewById(R.id.clue_plus);
        cluePlus.setVisibility(View.VISIBLE);*/
    }

    /*
    *  Shows the next clue for current location.
    * */
    public void showNextClue(View v) {
        /*//TextView target_label = (TextView) findViewById(R.id.current_target);
        //TextView clue = (TextView) findViewById(R.id.clue_text);
        clue_id++;
        String target_string = String.valueOf(target_id + 1);
        //target_label.setText("Target " + target_string + "/10:");
        switch(clue_id){
            case 0: clue.setText(location_clues[target_id][0]);
                break;
            case 1:
                String message = location_clues[target_id][0]+ "\n"+ location_clues[target_id][1];
                clue.setText(message);
                System.out.println(message);
                break;
            case 2: clue.setText(location_clues[target_id][0]+ "\n"+ location_clues[target_id][1]+ "\n"+ location_clues[target_id][2]);
                break;
            default: clue.setText(location_clues[target_id][0]+ "\n"+ location_clues[target_id][1]+ "\n"+ location_clues[target_id][2] +"\n No more clues!");
                View cluePlus = findViewById(R.id.clue_plus);
                cluePlus.setVisibility(View.GONE);
        }*/


    }

    /*
    *  Skip - can be only used once in the game.
    *  need to add alert dialog to confirm skipping.
    * */
    public void skip(View v){
        //showWarning(MainActivity.this, "Confirmation Message", "You are sure that you want to skip? It can be used once.", "Yes", "No", v).show();
        showNextLocation();
    }

    /*
*  Show the next location.
* */
    private void showNextLocation() {
        /*gps = false; // turning off the GPS temp feature until user wants to use it.
        //TextView target_label = (TextView) findViewById(R.id.current_target);
        //TextView clue = (TextView) findViewById(R.id.clue_text);

        if (target_id < 2) {
            target_id++;
            //clue.setText(location_clues[target_id][0]);

            String target_string = String.valueOf(target_id + 1);

            //target_label.setText("Target " + target_string + "/10:");
        }
        else {
            //target_label.setText("Done!");
            //clue.setText("No more clues!");
        }*/

    }

    /* warning Dialog box
    * */
    private AlertDialog showWarning(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo, View view) {
        final View v = view;
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //View cluePlus = findViewById(R.id.skip);
                //cluePlus.setVisibility(View.GONE);
                showNextLocation();
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void doGpsView(double lat, double log){
        /*if(gps) {
            tempGPS(lat, log);
            counter++;
        }*/
    }

    public void turnOnGPS(View v){
        /*gps = true; // turn on the GPS feature.
        view = v;*/
    }
    public void tempGPS(double lat,double log){
        /*userLat = lat;
        userLog = log;
        // double userLat = gpsTemp.getUserLat();//location_lat[0];//input location manually for testing. -> should get actual location -> //gpsTemp.getUserLat();
        // double userLog = gpsTemp.getUserLog();//location_long[0];//gpsTemp.getUserLog();
        //  int result  = gpsTemp.calculateDistance(userLat,userLog, location_lat[target_id],location_long[target_id]);
        double result  = distFrom(userLat,userLog, location_lat[target_id],location_long[target_id]);
        System.out.println("location_lat[target_id]:"+location_lat[target_id]);// for testing purposes. need to set [target_id]
        System.out.println("location_long[target_id]:"+location_long[target_id]);
        System.out.println("result:"+result);
        System.out.println("convert:"+convertKMtoInches(result));
        //  double distance = convertKMtoInches(result);
        //update background color
        float[] results = new float[4];
        location.distanceBetween(userLat,userLog,43.07996217,-77.61915561,results );
        String color = hexColors((results[0]*3.28084));
        View main = findViewById(R.id.Main_Layout);
        main.setBackgroundColor(Color.parseColor(color));

        TextView  text = (TextView) findViewById(R.id.other_score);

        text.setText("lat:"+userLat+" log:"+userLog+" c"+counter+ "d"+((int)(results[0]*3.28084)));*/
    }


    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    /*
  *  Converts KM to feet
  * */
    public int convertKMtoInches (double km){
        System.out.println("Conv km:"+km);
        double conv = 1000 / 0.3048;
        double feet = Math.round((km*conv));
        return (int) feet;
    }

    /*
    * The colors change within 510 inches, if above it will stay blue.
    * */
    public String hexColors(double distance){
        String color = "#";
        int blue = 0;
        String green = "00";
        int red = 0;
        if(distance >= 255){ //higher than 255 feet (blue hue)
            red = (int)(510-distance); //how much red should blend in with blue.
            if(red<= 0){
                red = 0;
            }else if(red>=255){
                red = 255;
            }
            blue = 255;
            System.out.println("red:"+red);

        }else{ //below 255 feet (red hue)
            blue = (int)(distance); //how much blue should blend in with red.
            if(blue<= 5 || distance == 0){
                blue = 0;
            }else if(blue>=255){
                blue = 255;
            }else{
                blue =+ 50;
            }
            red = 255;
            System.out.println("blue:"+blue);
        }

        if(Integer.toHexString(red).length()==2){
            color += Integer.toHexString(red);
        }else{
            color += "0"+Integer.toHexString(red);
        }
        color += green;

        if(Integer.toHexString(blue).length()==2){
            color += Integer.toHexString(blue);
        }else{
            color += "0"+Integer.toHexString(blue);
        }
        System.out.println("color:"+color);
        return color;
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
