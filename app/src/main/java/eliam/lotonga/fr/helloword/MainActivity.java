package eliam.lotonga.fr.helloword;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.util.List;
import java.util.Locale;

import eliam.lotonga.fr.apis.Api_Meteo;
import eliam.lotonga.fr.apis.Localisation;

import static eliam.lotonga.fr.apis.Localisation.MY_PERMISSION_REQUEST_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText query;
    private Button detectMe, submit;
    private Localisation localisation;

    TextView city, humidite, pression, max, min, temperature, date, description;
    ImageView icon;
    //Typeface weatherFont;
    LocationManager locationManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeVariables();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.detectMe.setOnClickListener(this);
        this.submit.setOnClickListener(this);
        this.localisation = new Localisation();
       // weatherFont = Typeface.createFromFile(R.drawable)createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        city = (TextView)findViewById(R.id.city);
        humidite = (TextView)findViewById(R.id.humidite);
        pression = (TextView)findViewById(R.id.pression);
        description = (TextView)findViewById(R.id.description);
        date = (TextView)findViewById(R.id.date);
        temperature = (TextView)findViewById(R.id.temperature);
        min = (TextView)findViewById(R.id.min);
        max = (TextView)findViewById(R.id.max);
        icon = (ImageView) findViewById(R.id.imageMeteo);
       // weatherIcon.setTypeface(weatherFont);
        this.listenerLocationBtn();
        changeViewByDetectLocation();

        submit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {

                    submit.setBackgroundColor(Color.rgb(51,181,229));
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(query.getWindowToken(), 0);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    submit.setBackgroundColor(Color.rgb(10,142,165));

                }

                return false;
            }
        });
        detectMe.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    detectMe.setBackgroundColor(Color.rgb(51,181,229));
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    detectMe.setBackgroundColor(Color.rgb(10,142,165));
                }

                return false;
            }
        });


    }


    public String hereLocation( double lat, double lon){
        String curCity = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.FRANCE);

        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if(addressList.size() > 0 ){
                curCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this, lat+" / "+ lon, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return curCity;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION_REQUEST_LOCATION:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        try{
                            this.updateView(hereLocation(location.getLatitude(), location.getLongitude()));
                        }catch(Exception e){
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(this, "No permission granted !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Drawable updateIcon(String name){
        Drawable dr = null;
        switch (name){
            case "01d":
                dr = getResources().getDrawable(R.drawable.f01d);
                break;
            case "01n":
                dr = getResources().getDrawable(R.drawable.f01n);
                break;
            case "02d":
                dr = getResources().getDrawable(R.drawable.f02d);
                break;
            case "02n":
                dr = getResources().getDrawable(R.drawable.f02n);
                break;
            case "03n":
            case "03d":
                dr = getResources().getDrawable(R.drawable.f03d);
                break;
            case "04d":
            case "04n":
                dr = getResources().getDrawable(R.drawable.f04d);
                break;
            case "09d":
            case "09n":
                dr = getResources().getDrawable(R.drawable.f09d);
                break;
            case "10d":
                dr = getResources().getDrawable(R.drawable.f10d);
                break;
            case "10n":
                dr = getResources().getDrawable(R.drawable.f10n);
                break;
            case "11n":
            case "11d":
                dr = getResources().getDrawable(R.drawable.f11d);
                break;
            case "13d" :
            case "13n":
                dr = getResources().getDrawable(R.drawable.f13d);
                break;
            case "50d":
            case "50n":
                dr = getResources().getDrawable(R.drawable.f50d);
                break;
        }
        return dr;
    }

    public void updateView(String ville){

        Api_Meteo.placeIdTask asyncTask =new Api_Meteo.placeIdTask(new Api_Meteo.AsyncResponse() {
            public void processFinish(String p_city, String p_description, String p_temperature, String p_humidity, String p_pressure, String p_icon, String p_min, String p_max, String p_df) {

                city.setText(p_city);
                description.setText(p_description);
                temperature.setText(p_temperature);
                date.setText(p_df);
                min.setText("Min : "+p_min+"°c");
                max.setText("Max : "+p_max+"°c");
                humidite.setText(p_humidity);
                pression.setText(p_pressure);
                icon.setImageDrawable(updateIcon(p_icon));

            }
        });
       asyncTask.execute(ville);
    }

    /**
     * binding entre la vue et le controller
     */
    private void initializeVariables()
    {
        this.query = (EditText) findViewById(R.id.query);
        this.detectMe = (Button) findViewById(R.id.detectMe);
        this.submit = (Button) findViewById(R.id.submit);
        //this.villeMeteo = (TextView) findViewById(R.id.villeDescription);

    }

    private void changeViewByDetectLocation(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            Api_Meteo.placeIdTask asyncTask = new Api_Meteo.placeIdTask(new Api_Meteo.AsyncResponse() {
                public void processFinish(String p_city, String p_description, String p_temperature, String p_humidity, String p_pressure, String p_icon, String p_min, String p_max, String p_df) {

                    city.setText(p_city);
                    description.setText(p_description);
                    temperature.setText(p_temperature);
                    date.setText(p_df);
                    min.setText("Min : " + p_min + "°");
                    max.setText("Max : " + p_max + "°");
                    humidite.setText(p_humidity);
                    pression.setText(p_pressure);
                    icon.setImageDrawable(updateIcon(p_icon));
                }
            });
            Location location = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //Toast.makeText(MainActivity.this, hereLocation(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
            asyncTask.execute(hereLocation(location.getLatitude(), location.getLongitude())); //  asyncTask.execute("Latitude", "Longitude")
        }
    }

    /**
     * Recuperer la recherche de l'utilisateur : la ville
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            // Au clique de ce bouton, on recupere la recherche de l'utilisateur

            // On interrroge l'api meteo

            // On interroge l'api BAN

            // On affichage les informations pertinentes à l'écran
            case R.id.submit: {
                if(query.getText() != null ){
                    updateView(query.getText().toString());
                }
                break;
            }

            //.... etc
        }
    }

    private void listenerLocationBtn(){
        detectMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
                    if( ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_REQUEST_LOCATION);
                       // Toast.makeText(localisation, "IF 2", Toast.LENGTH_SHORT).show();
                    }else{
                        //Toast.makeText(localisation, "ELSE 1", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
                    }
                }else{
                   // Toast.makeText(localisation, "ELSE", Toast.LENGTH_SHORT).show();
                    try{

                        Api_Meteo.placeIdTask asyncTask =new Api_Meteo.placeIdTask(new Api_Meteo.AsyncResponse() {
                            public void processFinish(String p_city, String p_description, String p_temperature, String p_humidity, String p_pressure, String p_icon, String p_min, String p_max, String p_df) {

                                city.setText(p_city);
                                description.setText(p_description);
                                temperature.setText(p_temperature);
                                date.setText(p_df);
                                min.setText("Min : "+p_min+"°");
                                max.setText("Max : "+p_max+"°");
                                humidite.setText(p_humidity);
                                pression.setText(p_pressure);
                                icon.setImageDrawable(updateIcon(p_icon));
                            }
                        });
                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        //Toast.makeText(MainActivity.this, hereLocation(location.getLatitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
                        asyncTask.execute( hereLocation( location.getLatitude(), location.getLongitude() ) ); //  asyncTask.execute("Latitude", "Longitude")

                        //Toast.makeText(localisation, location.toString(), Toast.LENGTH_SHORT).show();
                        //updateView(location,null);
                        //coord.setText(hereLocation(location.getLatitude(), location.getLongitude()));
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this, "CATCH" , Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Not Found :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
