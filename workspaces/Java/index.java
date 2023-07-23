import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @RestController
    public class ApiController {

        private static final String SAMPLE_API_BASE = "https://api.example.com";
        private static final String WEBHOOK_ID = "your_webhook_id"; // Replace with your actual webhook ID
        private static final String CLIENT_ID = "your_client_id"; // Replace with your actual client ID
        private static final String CLIENT_SECRET = "your_client_secret"; // Replace with your actual client secret

        private String access_token;

        private String getAccessToken() {
            // Implement your OAuth token retrieval logic here.
            // You can use the CLIENT_ID and CLIENT_SECRET to authenticate and obtain an access token.
            // Store the token in the 'access_token' variable and return it.
            return access_token;
        }

        @GetMapping("/")
        public ResponseEntity<String> getIndex() {
            return new ResponseEntity<>("Hello, this is the index page!", HttpStatus.OK);
        }

        @PostMapping("/capture/{orderId}")
        public ResponseEntity<String> captureOrder(@PathVariable String orderId) {
            try {
                access_token = getAccessToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", "application/json");
                headers.set("Authorization", "Bearer " + access_token);

                HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<String> response = restTemplate.exchange(
                        SAMPLE_API_BASE + "/v2/checkout/orders/" + orderId + "/capture",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                System.out.println("💰 Payment captured!");
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("❌ Payment failed.", HttpStatus.BAD_REQUEST);
            }
        }

        @PostMapping("/webhook")
        public ResponseEntity<String> handleWebhook(@RequestBody String requestBody, @RequestHeader HttpHeaders requestHeaders) {
            try {
                access_token = getAccessToken();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Accept", "application/json");
                headers.set("Authorization", "Bearer " + access_token);
                headers.set("SAMPLE-transmission-id", requestHeaders.getFirst("SAMPLE-transmission-id"));
                headers.set("SAMPLE-transmission-time", requestHeaders.getFirst("SAMPLE-transmission-time"));
                headers.set("SAMPLE-cert-url", requestHeaders.getFirst("SAMPLE-cert-url"));
                headers.set("SAMPLE-auth-algo", requestHeaders.getFirst("SAMPLE-auth-algo"));
                headers.set("SAMPLE-transmission-sig", requestHeaders.getFirst("SAMPLE-transmission-sig"));

                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
                RestTemplate restTemplate = new RestTemplate();

                ResponseEntity<String> response = restTemplate.exchange(
                        SAMPLE_API_BASE + "/v1/notifications/verify-webhook-signature",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                String verification_status = response.getBody();

                if (!"SUCCESS".equals(verification_status)) {
                    System.out.println("⚠️ Webhook signature verification failed.");
                    return new ResponseEntity<>("⚠️ Webhook signature verification failed.", HttpStatus.BAD_REQUEST);
                }

                // Further logic for handling the webhook event
                String event_type = "CHECKOUT.ORDER.APPROVED"; // Retrieve the event type from the 'requestBody'

                if ("CHECKOUT.ORDER.APPROVED".equals(event_type)) {
                    // Capture the order
                    ResponseEntity<String> captureResponse = captureOrder("orderId"); // Pass the actual orderId here

                    if (captureResponse.getStatusCode().is2xxSuccessful()) {
                        System.out.println("💰 Payment captured!");
                    } else {
                        System.out.println("❌ Payment failed.");
                        return new ResponseEntity<>("❌ Payment failed.", HttpStatus.BAD_REQUEST);
                    }
                }

                return new ResponseEntity<>("Webhook processed successfully.", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("❌ Error processing webhook.", HttpStatus.BAD_REQUEST);
            }
        }
    }
}
