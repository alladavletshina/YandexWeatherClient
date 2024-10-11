import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Scanner;
import org.json.JSONArray;


public class YandexWeatherClient_version_1 {
    private final String apiKey;

    public YandexWeatherClient_version_1(String apiKey) {
        this.apiKey = apiKey;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Добро пожаловать в приложение для прогноза погоды в Москве\n");
        System.out.println("Можно сделать прогноз максимум на 7 дней!");
        System.out.println("Введите количество дней,на которые хотите получить прогноз:");
        Scanner scanner = new Scanner(System.in);
        int limit = getIntNumber(scanner);

        String apiKey = "44d84b73-a430-45b6-9e62-d4320ae66083";
        YandexWeatherClient_version_1 YandexWeatherClient = new YandexWeatherClient_version_1(apiKey);
        YandexWeatherClient.getWeatherData(55.75, 37.62, limit);
    }

    public static int getIntNumber (Scanner scanner) {
        int result = 0;
        boolean isValid = false;
        do {
            try {
                String input = scanner.nextLine().trim();
                int number = Integer.parseInt(input);
                if (number > 0 && number <= 7) {
                    result = number;
                    isValid = true;
                } else {
                    System.out.println("Введите число от 1 до 7");
                }
            } catch (NumberFormatException e) {
                System.out.println("Умею читать только числа. Введите число");
            }
        } while (!isValid);
        return result;
    }

    public void getWeatherData(double lat, double lon, int limit) throws Exception {
        try {
            String urlString = "https://api.weather.yandex.ru/v2/forecast?lat=" + lat + "&lon=" + lon + "&limit=" + limit;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-Yandex-Weather-Key", apiKey);
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonResponce = response.toString();
            JSONObject jsonObject = new JSONObject(jsonResponce);
            System.out.println("\nОтвет от сервиса в формате json: " + jsonResponce);

            int temp = jsonObject.getJSONObject("fact").getInt("temp");
            System.out.println("\nНа данный момент температура составляет: " + temp + " градусов цельсия");

            JSONArray forecast = jsonObject.getJSONArray("forecasts");
            int count = forecast.length();
            double sumTemp = 0;
            String firstDateForecast = forecast.getJSONObject(0).getString("date");
            String lastDateForecast = "";

            for (int i = 0; i < count && i <= limit; ++i) {
                JSONObject currentForecast = forecast.getJSONObject(i);
                lastDateForecast = currentForecast.getString("date");
                JSONObject parts = currentForecast.getJSONObject("parts");
                JSONObject dayPart = parts.getJSONObject("day");
                int tempAvg = dayPart.getInt("temp_avg");
                sumTemp += tempAvg;
            }

            double averageTemp = sumTemp / Math.min(count, limit);
            int roundedAverageTemp = (int) Math.ceil(averageTemp);
            System.out.println("\nСредняя температура днем за период c " + firstDateForecast + " по " + lastDateForecast + " составляет: " +  roundedAverageTemp + " градусов цельсия");

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

