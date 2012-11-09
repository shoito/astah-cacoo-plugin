package com.github.astah.cacoo2astah;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CacooClient {
	private String apiKey = "";
	private HttpClient client;
	
	public CacooClient(String apiKey) {
		this.apiKey = apiKey;
		
		client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
	}
	
	public void setAPIKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getAPIKey() {
		return apiKey;
	}
	
	private String doGet(String url) throws IOException {
		HttpUriRequest httpUriRequest = new HttpGet(url);
		HttpResponse response = client.execute(httpUriRequest);
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		response.getEntity().writeTo(byteArrayOutputStream);
		
		String responseText = "";
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			responseText = byteArrayOutputStream.toString("UTF-8");
		}
		
		byteArrayOutputStream.close();
		
		return responseText;
	}
	
	public JSONArray getDiagrams() {
		String url = "https://cacoo.com/api/v1/diagrams.json?apiKey=" + apiKey;
		JSONArray diagrams = new JSONArray();
		try {
			JSONObject result = new JSONObject(doGet(url));
			diagrams = result.getJSONArray("result");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return diagrams;
	}
	
	public JSONArray getSheets(String diagramId) {
		String url = "https://cacoo.com/api/v1/diagrams/" + diagramId + ".json?apiKey=" + apiKey;
		JSONArray sheets = new JSONArray();
		try {
			JSONObject result = new JSONObject(doGet(url));
			sheets = result.getJSONArray("sheets");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sheets;
	}
}
