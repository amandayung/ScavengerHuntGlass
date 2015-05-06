package edu.rit.scavengerhuntglass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
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

    //private CardBuilder loginCard;
    private CardBuilder mainCard;
    private CardBuilder clueLifeline;
    private CardBuilder tempLifeline;
    private CardBuilder skipLifeline;

    private View mainView;

    static final int QR_SCAN_RESULT = 0;
    static final int GAME_TIME = 2700000;

    String team_name;
    boolean gameStarted;
    boolean timeUp;
    boolean hint;
    static int score = 0;
    public int target_score = 0;
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

    private long startTime = 0L;
    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;

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

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        if (location!=null) {
            userLog = location.getLongitude();
            userLat = location.getLatitude();
        }
        //create location clues
        location_clues = new String[10][3];
        location_clues[0][0] = "Where can you get a free copy of the New York Times?";
        location_clues[0][1] = "You can also sit here next to a fireplace while looking at a giant clock.";
        location_clues[0][2] = "You can also order coffee here.";

        location_clues[1][0] = "What is known as Geek Heaven?";
        location_clues[1][1] = "It is also known as the Student Innovation Hall.";
        location_clues[1][2] = "It is surrounded by glass and is filled with projectors.";

        location_clues[2][0] = "You can get your tan here.";
        location_clues[2][1] = "You can look professional after getting service from this place.";
        location_clues[2][2] = "This place will remove your dead cells or doing add-ons to your dead cells.";

        location_clues[3][0] = "You can order Western Union money orders here.";
        location_clues[3][1] = "You can buy boxes here.";
        location_clues[3][2] = "This place always close on Sunday.";

        location_clues[4][0] = "Where can you buy hand-made gifts?";
        location_clues[4][1] = "Where can you buy hand-made desk clock?";
        location_clues[4][2] = "Where can you buy glass pen?";

        location_clues[5][0] = "Where can you find a wall covered in different languages?";
        location_clues[5][1] = "Where should you go if you want to enroll other campus other than Rochester, NY?";
        location_clues[5][2] = "Where should you go to gain your international experience?";

        location_clues[6][0] = "Where can you find out about research about future everyday technologies?";
        location_clues[6][1] = "You can see this building when looking out from the Student Innovation Center.";
        location_clues[6][2] = "The building name is also a color.";

        location_clues[7][0] = "Where can you seek out the dean for the College of Applied Science and Technology?";
        location_clues[7][1] = "The same building also includes the office for Women in Technology.";
        location_clues[7][2] = "You’ll find the building entrance near a fountain.";

        location_clues[8][0] = "You will see a white board of RIT Aero design team.";
        location_clues[8][1] = "Where can you find a model of rocket launch vehicle?";
        location_clues[8][2] = "Where can you find a model of formula one racecar?";

        location_clues[9][0] = "Where can you find computer science, information technology, and software engineering?";
        location_clues[9][1] = "You can see the Dean’s office from here.";
        location_clues[9][2] = "You can get a “byte” at CTRL ALT DELi.";



        location_QR = new String[10];
        location_QR[0] = "1"; //Midnight Oil
        location_QR[1] = "2"; //Magic Lab
        location_QR[2] = "3"; //Shear Global Salon
        location_QR[3] = "4"; //Post Office
        location_QR[4] = "5"; //Shop One
        location_QR[5] = "6"; //Study Abroad Center
        location_QR[6] = "7"; //Orange Hall/FET Lab
        location_QR[7] = "8"; //Color Science
        location_QR[8] = "9"; //Aviation Lab
        location_QR[9] = "10"; //Entrance of Golisano Hall


        location_lat = new double[10];


        location_lat[0]=43.082541; //Midnight Oil
        location_lat[1] = 43.083085; //Magic Lab
        location_lat[2] = 43.082657;//Shear Global Salon
        location_lat[3] = 43.082597; //Post Office
        location_lat[4] = 43.082762; //Shop One
        location_lat[5] = 43.083172;//Study Abroad Center
        location_lat[6] = 43.083729 ; //Orange Hall/FET Lab
        location_lat[7] = 43.082548; //Color Science
        location_lat[8] = 43.084624; //Aviation Lab
        location_lat[9] = 43.084658;  //Entrance of Golisano Hall



        location_long = new double[10];
        location_long[0] =-77.679751; //Midnight Oil
        location_long[1] = -77.679854;//Magic Lab
        location_long[2] = -77.680929;//Shear Global Salon
        location_long[3] = -77.680686; //Post Office
        location_long[4] =  -77.680389; //Shop One
        location_long[5] = -77.680978; //Study Abroad Center
        location_long[6] = -77.678980; //Orange Hall/FET Lab
        location_long[7] = -77.678461; //Color Science
        location_long[8] = -77.678362; //Aviation Lab
        location_long[9] = -77.679969;  //Entrance of Golisano Hall

        //need to start up the location counter
        target_id = 0;
        clue_id = 0;



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

        /*loginCard = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Tap when you're ready to begin!")
                .setFootnote("Other Team's Score: 0")
                .setTimestamp("0:00");*/


        mainCard = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.activity_main)
                .setFootnote("Team Score: 0")
                .setTimestamp("45:00");

        clueLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Clue+")
                .setFootnote("Lifeline");
                //.setTimestamp("0:00");

        tempLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Temp")
                .setFootnote("Lifeline");
                //.setTimestamp("0:00");

        skipLifeline = new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Skip")
                .setFootnote("Lifeline (you can only use skip once!)");
                //.setTimestamp("0:00");


        mCards.add(mainCard);
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
                if (!timeUp) {
                    //start the game
                    if (position == 0 && !gameStarted) {
                        mCards.add(clueLifeline);
                        mCards.add(tempLifeline);
                        mCards.add(skipLifeline);
                        gameStarted = true;
                        mAdapter.notifyDataSetChanged();

                        //save reference to main card's view
                        mainView = mAdapter.getView(position, view, parent);

                        //start timer
                        startTime = SystemClock.uptimeMillis();
                        myHandler.postDelayed(updateTimerMethod, 0);

                        NewTeam myTeam = new NewTeam();
                        team_name = myTeam.createTeam();

                        showFirstClue();

                        //set target
                    /*TextView target = (TextView) mainView.findViewById(R.id.current_target);
                    target.setText("Target: 1/10");

                    //set first clue for first target
                    TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
                    clue.setText("This is the first clue.");*/
                    }
                    //if it's the main card, open up the QR code scanner
                    else if (position == 0 && gameStarted) {
                        scan();
                    } else if (position == 1) { //clue+ lifeline
                    /*TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
                    clue.setText("This would be the second clue to the first target.");*/
                        showNextClue();
                        mCardScrollView.setSelection(0);
                        mAdapter.notifyDataSetChanged();
                    } else if (position == 2) { //temp lifeline
                        //mainView.setBackgroundColor(Color.RED);
                        turnOnGPS();
                        mCardScrollView.setSelection(0);
                        mAdapter.notifyDataSetChanged();

                    } else if (position == 3) { //skip lifeline
                        skip(view);
                        mCardScrollView.setSelection(0);

                        //remove the skip lifeline
                        mCards.remove(3);
                        mAdapter.notifyDataSetChanged();

                    }
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

            // Handle successful scan
            if (contents.equals(location_QR[target_id])) {
                Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
                showNextLocation();

            }
            else {
            Toast.makeText(getApplicationContext(), "Sorry, incorrect! Try a different location!", Toast.LENGTH_LONG).show();
            }



        } else if (resultCode == RESULT_CANCELED) {
            // Handle cancel
            Log.i("App", "Scan unsuccessful");
        }
    }

    /*
      *  Shows the first clue.
      * */
    public void showFirstClue() {
        //TextView clue = (TextView)v;
        clue_id = 0;
        target_score = 10;
        String target_string = String.valueOf(target_id + 1);

        TextView target = (TextView) mainView.findViewById(R.id.current_target);
        target.setText("Target: " + target_string + "/10");

        //set first clue for first target
        TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
        clue.setText(location_clues[target_id][clue_id]);
        //View cluePlus = findViewById(R.id.clue_plus);
        //cluePlus.setVisibility(View.VISIBLE);
    }

    /*
    *  Shows the next clue for current location.
    * */
    public void showNextClue() {
        TextView target_label = (TextView) mainView.findViewById(R.id.current_target);
        TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
        clue_id++;
        String target_string = String.valueOf(target_id + 1);
        target_label.setText("Target " + target_string + "/10:");
        switch(clue_id){
            case 0: clue.setText("Clue 1: " + location_clues[target_id][0]);
                break;
            case 1:
                String message = "Clue 2: " + location_clues[target_id][1];
                target_score = target_score - 2;
                clue.setText(message);
                //System.out.println(message);
                break;
            case 2: clue.setText("Clue 3: " + location_clues[target_id][2]);
                target_score = target_score - 2;
                break;
            default: clue.setText("Clue 3: " + location_clues[target_id][2] +"\n\n No more clues!");
                //View cluePlus = findViewById(R.id.clue_plus);
                //cluePlus.setVisibility(View.GONE);
        }


    }



    public void doGpsView(double lat, double log){
        if(gps) {
            tempGPS(lat, log);
            counter++;
            System.out.println("GPS is turned on.");
        }
    }

    public void turnOnGPS() {
        gps = true; // turn on the GPS feature.
        //view = v;
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Currently, your Glass does not support GPS", Toast.LENGTH_LONG).show();
           // alertNoGPS(MainActivity.this, "Warning Message", "Currently, your phone does not support GPS", "Ok", v).show();
        } else {
            target_score = target_score - 5;
            doGpsView(userLat, userLog);
        }
    }

    /* warning Dialog box
       * */
   /* private  AlertDialog alertNoGPS(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, View view) {
        final View v = view;
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        return downloadDialog.show();
    }*/


    public void tempGPS(double lat,double log){
        userLat = lat;
        userLog = log;
        // double userLat = gpsTemp.getUserLat();//location_lat[0];//input location manually for testing. -> should get actual location -> //gpsTemp.getUserLat();
        // double userLog = gpsTemp.getUserLog();//location_long[0];//gpsTemp.getUserLog();
        //  int result  = gpsTemp.calculateDistance(userLat,userLog, location_lat[target_id],location_long[target_id]);
        double result  = distFrom(userLat,userLog, location_lat[target_id],location_long[target_id]);
        System.out.println("location_lat[target_id]:"+location_lat[target_id]);// for testing purposes. need to set [target_id]
        System.out.println("location_long[target_id]:"+location_long[target_id]);
        //  System.out.println("result:"+result);
        //   System.out.println("convert:"+convertKMtoInches(result));
        double distance = convertKMtoInches(result);
        //update background color
        float[] results = new float[4];
        location.distanceBetween(userLat,userLog,location_lat[target_id],location_long[target_id],results );
        String color = hexColors((int)(results[0]*5));
        System.out.println("distance to... "+results[0]*5);
        mainView.setBackgroundColor(Color.parseColor(color));

        //TextView text = (TextView) findViewById(R.id.other_score);
        mainCard.setFootnote("lat:"+userLat+" log:"+userLog+" c"+counter+ "d:"+(results[0]*5));
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
        // double conv = 1000 / 0.3048;
        double feet = Math.round((km*3280.8));
        return (int) feet;
    }

    /*
    * The colors change within 510 inches, if above it will stay blue.
    * */
    public String hexColors(int distance){
        String color = "#";
        int blue = 0;
        String green = "00";
        int red = 0;
        if(distance >= 255){ //higher than 255 feet (blue hue)
            red = (510-distance); //how much red should blend in with blue.
            if(red<= 0){
                red = 0;
            }else if(red>=255){
                red = 255;
            }
            blue = 255;
            System.out.println("red:"+red);

        }else{ //below 255 feet (red hue)
            System.out.println(distance);
            blue = (distance); //how much blue should blend in with red.
            if(distance<= 10 || distance == 0){
                blue = 0;
            }else if(distance>=255){
                blue = 255;
            }else{
                // blue =+ 50;
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

    /*
    *  Skip - can be only used once in the game.
    *  need to add alert dialog to confirm skipping.
    * */
    public void skip(View v){
        //showWarning(MainActivity.this, "Confirmation Message", "You are sure that you want to skip? It can be used once.", "Yes", "No", v).show();
        target_score = target_score - 2;
        showNextLocation();
    }

    /*
    *  Show the next location.
    * */
    private void showNextLocation() {
        score = score + target_score;
        gps = false; // turning off the GPS temp feature until user wants to use it.
        mainView.setBackgroundColor(Color.BLACK);
        TextView target_label = (TextView) mainView.findViewById(R.id.current_target);
        TextView clue = (TextView) mainView.findViewById(R.id.clue_text);

        mainCard.setFootnote("Team Score: " + score);

        UpdateScore myScore = new UpdateScore();
        myScore.execute(team_name, "" + score);

        if (target_id < 9) {
            target_id++;
            // clue.setText(location_clues[target_id][0]);
            showFirstClue();
            String target_string = String.valueOf(target_id + 1);
            target_label.setText("Target " + target_string + "/10:");
        }
        else {
            endGame();
        }

    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() - startTime;
            finalTime = timeSwap + timeInMillies;
            finalTime=GAME_TIME-finalTime;


            if (finalTime <= 0) {
                endGame();
                timeUp = true;
                return;
            }

            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            mainCard.setTimestamp("" + minutes + ":"
                    + String.format("%02d", seconds));
            myHandler.postDelayed(this, 0);
            mAdapter.notifyDataSetChanged();

        }

    };

    private void endGame() {
        TextView target = (TextView) mainView.findViewById(R.id.current_target);
        target.setText("Congratulations!");

        TextView clue = (TextView) mainView.findViewById(R.id.clue_text);
        clue.setText("You did it.");
        mainCard.setFootnote("Your Team's Score: " + score);
        mainCard.setTimestamp("Other Team's Score: 0");

        mCardScrollView.setSelection(0);

        while (mCards.size() > 1) {
            mCards.remove(1);
        }
        mAdapter.notifyDataSetChanged();
    }

    /* warning Dialog box
    * */
    /*private AlertDialog showWarning(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo, View view) {
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
    }*/


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
