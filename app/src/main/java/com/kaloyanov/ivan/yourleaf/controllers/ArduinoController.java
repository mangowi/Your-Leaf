package com.kaloyanov.ivan.yourleaf.controllers;

import android.util.Base64;

import com.kaloyanov.ivan.yourleaf.requests.HttpRequest;
import com.kaloyanov.ivan.yourleaf.responses.HttpResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/*
 * @author ivan.kaloyanov
 */
public class ArduinoController {

    private static final String GET_REQUEST = "GET";
    private static final String PUT_REQUEST = "PUT";
    private static final String POST_REQUEST = "POST";
    private static final Integer STATUS_OK = 200;
    private static final String  ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private  static final String LOGIN_ROUTE = "/login";
    private static final String AUTHORIZATION = "Authorization";

    private static ArduinoController arduinoController;
    private String address;
    private String password;

    // Create lazy singleton for every app session
    private ArduinoController(){}

    /*
    * @return returns a singleton ArduinoController instance
    */
    public static ArduinoController instance(){
        if(arduinoController == null){
            arduinoController = new ArduinoController();
            return arduinoController;
        }
        return arduinoController;
    }

    /*
    * Login into the server/board
    * @param String address, the IP address of the server/board
    * @param String password, the password of the server/board
    * @throws IOException if the HTTP request fails
    * @return return true if the authentication is successful
    */
    public boolean login(String address, String password) throws IOException {
        this.address = "http://" + address;
        this.password = password;
        HttpRequest request = new HttpRequest(LOGIN_ROUTE, null, this.POST_REQUEST); // kind of hardcoded
        HttpResponse response = this.sendHttpRequest(request);

        return response.getStatus().equals(this.STATUS_OK);
    }

    /*
    *  Make Http response to the server/board
    *  @param HttpRequest request, the request to send
    *  @throws IOException if the request fails
    *  @return HttpResponse with response code and data
    */
    public HttpResponse sendHttpRequest(HttpRequest request) {
        URL url;
        HttpURLConnection connection = null;
        try {
            // Create http request
            url = new URL( this.address + request.getTarget());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
            connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
            String passHolder = Base64.encodeToString((":"+ this.password).getBytes(), Base64.DEFAULT);
            connection.setRequestProperty(AUTHORIZATION, "Basic " + passHolder);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(request.getData() != null);

            // Send request
            if (request.getData() != null) {
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(request.getData());
                wr.flush();
                wr.close();
            }

            // Get Response
            Integer responseCode = connection.getResponseCode();
            InputStream stream = connection.getErrorStream();
            if (stream == null) {
                stream = connection.getInputStream();
            }
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(stream));
            String line;
            StringBuffer responseData = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                responseData.append(line);
                responseData.append('\n');
            }
            rd.close();

            HttpResponse response = new HttpResponse(responseCode,responseData.toString().trim());
            return response;
        }  catch (IOException e) {
            String error = "Application error";
            Integer status = -200;
            return new HttpResponse(status, error);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
