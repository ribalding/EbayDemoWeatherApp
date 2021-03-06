package ryanharvey.ebaydemoproject.util;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ryanharvey.ebaydemoproject.constants.Constants;

/**
 * Created by Ryan on 10/24/2016.
 */
public class WeatherService {

    //Find Weather By LatLng
    public static void findWeather(LatLng latLng, Callback callback){
        String lat = Double.toString(latLng.latitude);
        String lng = Double.toString(latLng.longitude);

        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.WEATHER_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter("lat", lat);
        urlBuilder.addQueryParameter("lon", lng);
        urlBuilder.addQueryParameter("APPID", Constants.OPEN_WEATHER_API_KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    //Find Weather By Zip Code
    public static void findWeather(String zipCode, Callback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.WEATHER_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter("zip", zipCode + ",us");
        urlBuilder.addQueryParameter("APPID", Constants.OPEN_WEATHER_API_KEY);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static ArrayList<String> processResults(Response response){
        ArrayList<String> results = new ArrayList<>();
        try {
            if (response.isSuccessful()) {
                //Parse JSON Response
                JSONObject fullResultsJSONObject = new JSONObject(response.body().string());
                JSONArray weatherResultsJSONARRAY = fullResultsJSONObject.getJSONArray("weather");
                JSONObject tempResultsJSONObject = fullResultsJSONObject.getJSONObject("main");
                JSONObject weatherResultsJSONObject = weatherResultsJSONARRAY.getJSONObject(0);

                //Extract relevant data in string form and add to results array
                String mainWeather = weatherResultsJSONObject.getString("main");
                String temp = Integer.toString((int)(Math.round(WeatherService.convertKelvinToFahrenheit(tempResultsJSONObject.getDouble("temp")))));
                String cityName = fullResultsJSONObject.getString("name");
                results.add(cityName);
                results.add(mainWeather);
                results.add(temp);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }

    // Convert Kelvin to Fahrenheit
    public static double convertKelvinToFahrenheit(Double tempInKelvin){
        return (((tempInKelvin - 273) * 9d/5) + 32);
    }
}
