/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.Arrays;
import java.util.List;
import logger.DJLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author SYUS
 */
public final class CurrencyRatesRestConsumer {
    
    //A lot more is possible with the API, but this proves functionality for now
    private static final String API_URL = "https://bitpay.com/api/rates";
    
    
    //Read rates
    public static List<Object> readRates() {
        DJLog logger = new DJLog();
        RestTemplate restTemplate = new RestTemplate();
        logger.writeInfo("attempting read currencies");
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(API_URL, Object[].class);
        if(responseEntity.getStatusCode()!=HttpStatus.OK) {
            return null;
        }
        //MediaType contentType = responseEntity.getHeaders().getContentType();
        //HttpStatus statusCode = responseEntity.getStatusCode();
        Object[] currencies = responseEntity.getBody();
        logger.writeInfo("got currencies");
        return Arrays.asList(currencies);
    }
}
