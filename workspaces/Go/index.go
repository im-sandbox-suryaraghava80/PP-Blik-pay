package main

import (
	oauth "PP-Blik-pay/server/oauth"
	"fmt"
	"log"
	"mime"
	"net/http"
	"os"
	"os/exec"

	"github.com/gorilla/mux"
)

const (
	PAYPAL_API_BASE = "https://api.sandbox.paypal.com" // Replace this with your actual API base URL
)

func indexHandler(w http.ResponseWriter, r *http.Request) {
	http.ServeFile(w, r, "./static/index.html")
}

func captureHandler(w http.ResponseWriter, r *http.Request) {
	orderID := mux.Vars(r)["orderId"]

	// Implement your logic to get access_token using the getAccessToken() function
	// The getAccessToken() function should return the access_token value.
	// For brevity, I am assuming you have a function that retrieves the access_token.
	access_token, err := oauth.GetAccessToken()

	url := fmt.Sprintf("%s/v2/checkout/orders/%s/capture", PAYPAL_API_BASE, orderID)
	req, err := http.NewRequest("POST", url, nil)
	if err != nil {
		log.Println("Error creating HTTP request:", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")
	req.Header.Set("Authorization", "Bearer "+access_token)

	client := http.DefaultClient
	resp, err := client.Do(req)
	if err != nil {
		log.Println("Error sending HTTP request:", err)
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	defer resp.Body.Close()

	// Process the response and handle the payment capture result as needed
	// For example, you can read the response body using ioutil.ReadAll() and return it in the response.
	// But for brevity, I'm simply sending "Payment captured!" as the response.
	fmt.Fprintln(w, "Payment captured!")
}

func webhookHandler(w http.ResponseWriter, r *http.Request) {
	// Implement the logic for handling webhooks
	// For brevity, I'm just responding with a 200 OK status.
	w.WriteHeader(http.StatusOK)
}

// Execute before the service runs.
func init() {
	log.Println("Initializing...")
	FixMimeTypes()
	_ = mime.AddExtensionType(".js", "text/javascript")
}

func FixMimeTypes() {
	err1 := mime.AddExtensionType(".js", "text/javascript")
	if err1 != nil {
		log.Printf("Error in mime js %s", err1.Error())
	}

	err2 := mime.AddExtensionType(".css", "text/css")
	if err2 != nil {
		log.Printf("Error in mime js %s", err2.Error())
	}
}

func main() {
	r := mux.NewRouter()

	r.HandleFunc("/", indexHandler).Methods("GET")
	r.HandleFunc("/capture/{orderId}", captureHandler).Methods("POST")
	r.HandleFunc("/webhook", webhookHandler).Methods("POST")

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	cmd := exec.Command("open", fmt.Sprintf("http://localhost:%s", port))
	cmd.Start()

	log.Printf("Example app listening at http://localhost:%s\n", port)
	log.Fatal(http.ListenAndServe(":"+port, r))
}
