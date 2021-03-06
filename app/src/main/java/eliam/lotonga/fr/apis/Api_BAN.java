package eliam.lotonga.fr.apis;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by eliam on 31/12/2017.
 */

public class Api_BAN {
    private static String OPEN_BAN_URL = "https://api-adresse.data.gouv.fr/search/?q=";

    public interface AsyncResponse {
        //  Nbr de population, code postal, code de la ville, context (departement), coordonnées
        void processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output7, String output8, String output9);
    }


    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;//Call back interface

        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            try {
                jsonWeather = getWeatherJSON(params[0]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null){
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String date = simpleDateFormat.format(calendar.getTime());

                    String city = json.getString("name").toUpperCase(Locale.FRANCE) + ", " + json.getJSONObject("sys").getString("country");
                    String description = details.getString("description").toUpperCase(Locale.FRANCE);
                    String temperature = String.format("%.2f", main.getDouble("temp"))+ "°";
                    String humidity = main.getString("humidity") + "%";
                    String pressure = main.getString("pressure") + " hPa";
                    String icon =   details.getString("icon");
                    String min = main.getString("temp_min");
                    String max = main.getString("temp_max");
                    delegate.processFinish(city, description, temperature, humidity, pressure, icon, min, max, date);

                }
            } catch (JSONException e) {
                //Log.e(LOG_TAG, "Cannot process JSON results", e);
            }



        }
    }


    public static JSONObject getWeatherJSON(String city){
        try {
            URL url = new URL(OPEN_BAN_URL +city);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            // connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
