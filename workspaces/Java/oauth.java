import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/token")
public class AccessTokenController {

    private final WebClient webClient;

    // Replace with your actual values
    private final String SAMPLE_API_BASE = "https://api.SAMPLE.com";
    private final String CLIENT_ID = "your_client_id";
    private final String CLIENT_SECRET = "your_client_secret";

    public AccessTokenController() {
        this.webClient = WebClient.builder()
                .baseUrl(SAMPLE_API_BASE)
                .build();
    }

    @PostMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getAccessToken() {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        return webClient.post()
                .uri("/v1/oauth2/token")
                .header("Authorization", "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> ResponseEntity.ok().body(response));
    }
}
