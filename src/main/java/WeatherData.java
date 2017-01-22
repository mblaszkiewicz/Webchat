/**
 * Created by mblaszkiewicz on 14.01.2017.
 */
public class WeatherData {
    public Weather weather[];
    public MainData main;

    public class Weather {
        public String main;
    }
    public class MainData {
        public String temp;
        public String pressure;
        public String humidity;
    }
}
