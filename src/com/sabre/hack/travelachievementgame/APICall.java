package com.sabre.hack.travelachievementgame;

public class APICall {

	public static String APIRequest(String api){
		DSCommHandler DsC = new DSCommHandler();
		String token = Auth.getAccessToken(DsC);
		String sabreURL = "https://api.sabre.com/" +api;
		String response = DsC.sendRequest(sabreURL, token);
		return response;
	}
}
