import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by mblaszkiewicz on 17.01.2017.
 */
public class Chatbot {
    private String q1 = "Która godzina?";
    private String q2 = "Jaki dziś dzień tygodnia?";
    private String q3 = "Jaka jest pogoda w Krakowie?";

    private String readUrl(String surl) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(surl);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int ch;
            while ((ch = reader.read()) != -1)
                buffer.append((char) ch);
            return buffer.toString();
        } finally {
            if(reader != null)
                reader.close();
        }
    }

    public String questionCheck(String msg) {
        try {
            System.out.print(msg);
            if(msg.equals(q1)) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                return "Godzina: "+sdf.format(calendar.getTime());
            }
            else if(msg.equals(q2)) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(calendar.getTime());
                return "Dziś " + translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
            }
            else if(msg.equals(q3)) {
                String json = readUrl("http://api.openweathermap.org/data/2.5/weather?id=3094802&APPID=");
                Gson gson = new Gson();
                WeatherData weatherData = gson.fromJson(json, WeatherData.class);
                return "W Krakowie dziś " +translateCondtions(weatherData.weather[0].main) + ", " + toCelcius(weatherData.main.temp) + "°C, "
                        + weatherData.main.pressure + "hPa, wilgotność: " + weatherData.main.humidity +"%";
            }
        } catch(Exception e) {
            return "Zapytanie nie mogło zostać zrealizowane.";
        }
        return "Niezrozumiałe zapytanie. ";
    }

    private String translateCondtions (String english) {
        switch (english) {
            case "Mist": return "mgła";
            case "Fog": return "mgła";
            case "Clouds": return "zachmurzenie";
            case "Sun": return "słonecznie";
            case "Haze": return "lekkie zamglenie";
            case "Clear": return "bezchmurnie";
            case "Snow": return "śnieg";
            default: return english;
        }
    }

    private String translateDayOfWeek (int num) {
        switch (num) {
            case 1: return "niedziela";
            case 2: return "poniedziałek";
            case 3: return "wtorek";
            case 4: return "środa";
            case 5: return "czwartek";
            case 6: return "piątek";
            case 7: return "sobota";
            default: return "";
        }
    }

    private String toCelcius (String Kelvins) {
        return new BigDecimal(Kelvins).add(new BigDecimal(-273)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }
}
