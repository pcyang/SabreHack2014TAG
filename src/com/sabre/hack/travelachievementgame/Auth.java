package com.sabre.hack.travelachievementgame;

import android.util.Base64;

public class Auth {
	
	/**
	 * 
	 * @param dsC 
	 * @return the access token
	 */
	public static String getAccessToken(DSCommHandler dsC) {
		
		final String clientId = "V1:snnikcv70upaurnz:DEVCENTER:EXT";//Put Your Client Id Here
		final String clientSecret= "yW5tnK0V";//Put Your Secret Id Here
		String encodedClientIdSecret = null; 
		
		//authenticate 
		encodedClientIdSecret = authenticate(clientId, clientSecret);
	
		//get autherized token 
		String token = dsC.getAuthToken("https://api.test.sabre.com",encodedClientIdSecret);
		
		return token;
	}

	/**
	 * @param clientId 
	 * @param clientSecret
	 * @return encoded concatenated string 
	 */
	private static String authenticate(String clientId, String clientSecret) {
        //encode clientId and clientSecret
//        String encodedClientId = Base64.encodeBase64String((clientId).getBytes());
//        String encodedClientSecret = Base64.encodeBase64String((clientSecret).getBytes());
//        
//        
//        //Concatenate encoded client and secret strings, separated with colon
//        String encodedClientIdSecret = encodedClientId+":"+encodedClientSecret;
//        encodedClientIdSecret = Base64.encodeBase64String(encodedClientIdSecret.getBytes());
//        
//		return encodedClientIdSecret;
		return null;
	}

}
