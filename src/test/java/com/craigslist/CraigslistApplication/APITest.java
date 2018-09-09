package com.craigslist.CraigslistApplication;

import java.io.IOException;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SessionConfig;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class APITest {
	
	String sessionId;
	CookieFilter cookieFilter = new CookieFilter();
	SessionFilter sessionFilter = new SessionFilter();
		
	public void deleteSearch(String subId, String subName) throws IOException {

		Cookies allCookies = login();
		deleteSearch(allCookies, subId, subName);
	}

	@Test
	private Cookies login() {
		RestAssured.baseURI = "https://accounts.craigslist.org";
		RequestSpecification httpRequest = RestAssured.given().config(new RestAssuredConfig().sessionConfig(new SessionConfig().sessionIdName("cl_session")));
		
		httpRequest.contentType("application/x-www-form-urlencoded")
		.and().header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36")
		.and().header( "Host", "accounts.craigslist.org")
		.and().header("Origin", "https://accounts.craigslist.org")
		.and().header("Referer", "https://accounts.craigslist.org/login")
		.given().body("inputEmailHandle:neelima.chennavaram@gmail.com\r\n" + 
				"inputPassword:Ansh123$\r\n" + 
				"step:confirmation")
		.filter(sessionFilter)
		.log().everything();
		Response response = httpRequest.post("/login");
		sessionId = response.getCookie("cl_session");
		System.out.println("sessionId = " + sessionId);
		Cookies allCookies = response.then()
	            .statusCode(200)
	            .extract()
	            .response()
	            .getDetailedCookies();//getCookies();
		int statusCode = response.getStatusCode();
		System.out.println(statusCode);
	//	String body = response.getBody().prettyPrint();
		for(Cookie c : allCookies.asList())
			System.out.println(c);
		return allCookies;
	}
	
	private void deleteSearch(Cookies allCookies, String subId, String subName) {
		RestAssured.baseURI = "https://accounts.craigslist.org";
		RequestSpecification httpRequest = RestAssured.given().config(new RestAssuredConfig().sessionConfig(new SessionConfig().sessionIdName("cl_session")));
		
		httpRequest.contentType("application/x-www-form-urlencoded")
		.and().header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36")
		.and().header( "Host", "accounts.craigslist.org")
		.and().header("Origin", "https://accounts.craigslist.org")
		.and().header("Referer", "https://accounts.craigslist.org/login/home?show_tab=searches")
		.and().cookies(allCookies)
		.and().filter(sessionFilter)
		.given().body("subID:" + subId + "\r\n" + 
				"subName:" + subName)
		.filter(sessionFilter)
		.log().everything();
		Response response = httpRequest.post("/savesearch/delete");
		int statusCode = response.getStatusCode();
		System.out.println(statusCode);;
	}

	}
