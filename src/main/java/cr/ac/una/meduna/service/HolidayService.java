package cr.ac.una.meduna.service;

import cr.ac.una.meduna.model.HolidayDTO;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;


/**
 * Servicio de dias festivos, consumo de API
 *
 * @author Angie Marks
 * @author Juan Calderón
 */

public class HolidayService {
    private final HttpClient http = HttpClient.newHttpClient();
    private final Jsonb jsonb = JsonbBuilder.create();

    public List<HolidayDTO> getHolidaysCR(int year) throws Exception {

        String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/CR";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " consultando feriados");
        }

        HolidayDTO[] arr = jsonb.fromJson(resp.body(), HolidayDTO[].class);
        return Arrays.asList(arr);
    }
}
