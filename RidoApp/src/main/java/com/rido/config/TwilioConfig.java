package com.rido.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;

@Configuration
@ConfigurationProperties("twilio")
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;
    
    @Value("${twilio.trailNumber}")
    private String trailNumber;

    @Bean
    
    public TwilioRestClient twilioInitializer() {
        Twilio.init(accountSid, authToken);
        return Twilio.getRestClient(); // Assuming Twilio has a method to get the REST client
    }
//    public Twilio twilioInitializer() {
////        Twilio.init(accountSid, authToken);
//       
//    	Twilio.init(trailNumber, authToken, accountSid);
//     return	twilioInitializer();
//    }

	public String getAccountSid() {
		return accountSid;
	}

	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getTrailNumber() {
		return trailNumber;
	}

	public void setTrailNumber(String trailNumber) {
		this.trailNumber = trailNumber;
	}
    
}
