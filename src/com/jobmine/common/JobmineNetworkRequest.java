package com.jobmine.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;

import com.jobmine.models.Job;

public class JobmineNetworkRequest {
	private static String userName;
	private static String pwd;
	
	public static String getJobDescription (Context context, String jobId) {
		
		String descriptionText = "";
		
		if (userName == null || userName.isEmpty()) {
			//Set username/password
			SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
			setUserName(settings.getString(Constants.userNameKey, ""));
			setPassword(settings.getString(Constants.pwdKey, ""));
		}
		
		try {
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(
					"https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&"
							+ "userid=" + userName + "&" + "pwd=" + pwd + "&" + "submit=Submit");
			
			HttpResponse resp = client.execute(post);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(stream);

			post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_JOBDTLS?UW_CO_JOB_ID="+jobId);
			resp = client.execute(post);
			stream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(stream);
			Document table = Jsoup.parse(new String(stream.toByteArray()));
			Element description = table.getElementById("UW_CO_JOBDTL_VW_UW_CO_JOB_DESCR");
			descriptionText = description.html(); 
			post = new HttpPost(
					"https://jobmine.ccol.uwaterloo.ca/psp/SS/EMPLOYEE/WORK/?cmd=logout");
			client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return descriptionText;
	}
	
	public static ArrayList<Job> getJobmine (Context context) {
		
		if (userName == null || userName.isEmpty()) {
			//Set username/password
			SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
			setUserName(settings.getString(Constants.userNameKey, ""));
			setPassword(settings.getString(Constants.pwdKey, ""));
		}

		ArrayList<Job> jobies = new ArrayList<Job>();

		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> id = new ArrayList<String>();
		ArrayList<String> employer = new ArrayList<String>();
		ArrayList<String> jobStatus = new ArrayList<String>();
		ArrayList<String> appStatus = new ArrayList<String>();
		ArrayList<String> resumes = new ArrayList<String>();
		ArrayList<String> job = new ArrayList<String>();

		DefaultHttpClient client = new DefaultHttpClient();

		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&" + "userid=" + userName + "&" + "pwd=" + pwd + "&" + "submit=Submit");
		
		try {
			HttpResponse resp = client.execute(post);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resp.getEntity().consumeContent();

			post = new HttpPost(
					"https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL?pslnkid=UW_CO_APP_SUMMARY_LINK&FolderPath=PORTAL_ROOT_OBJECT.UW_CO_APP_SUMMARY_LINK&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder&PortalActualURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL%3fpslnkid%3dUW_CO_APP_SUMMARY_LINK&PortalContentURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL%3fpslnkid%3dUW_CO_APP_SUMMARY_LINK&PortalContentProvider=WORK&PortalCRefLabel=Applications&PortalRegistryName=EMPLOYEE&PortalServletURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsp%2fSS%2f&PortalURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2f&PortalHostNode=WORK&NoCrumbs=yes&PortalKeyStruct=yes");// ?ICType=Panel&Menu=UW_CO_STUDENTS&Market=GBL&PanelGroupName=UW_CO_APP_SUMMARY&RL=&target=main0&navc=5170");
			resp = client.execute(post);
			stream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(stream);
			Document table = Jsoup.parse(new String(stream.toByteArray()));

			post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/EMPLOYEE/WORK/?cmd=logout");
			client.execute(post);
			Elements element = table.getElementsByTag("table");
			if (element.size() < 6) {
				return null;
			}
			Element b = element.get(5);
			Elements c = b.getAllElements();
			for (int i = 0; i < c.size(); i++) {
				if (c.get(i).id().contains("UW_CO_JB_TITLE2") && c.get(i).hasText()) {
					title.add(c.get(i).ownText());
				} else if (c.get(i).id().contains("UW_CO_JB_TITLE2") && c.get(i).hasText()) {
					title.add(c.get(i).ownText());
				}

				if (c.get(i).id().contains("UW_CO_APPS_VW2_UW_CO_JOB_ID") && c.get(i).hasText()) {
					id.add(c.get(i).ownText());
				}
				if (c.get(i).id().contains("UW_CO_JOBINFOVW_UW_CO_PARENT_NAME") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					employer.add(c.get(i).ownText());
				}
				if (c.get(i).id().contains("UW_CO_TERMCALND_UW_CO_DESCR_30") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					job.add(c.get(i).ownText());
				}
				if (c.get(i).id().contains("UW_CO_JOBSTATVW_UW_CO_JOB_STATUS") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					jobStatus.add(c.get(i).ownText());
					if (c.get(i).ownText().contains("Ranking Completed")) {
						appStatus.add(" ");
					}
				}
				if (c.get(i).id().contains("UW_CO_APPSTATVW_UW_CO_APPL_STATUS") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					appStatus.add(c.get(i).ownText());
				}
				if (c.get(i).id().contains("UW_CO_JOBAPP_CT_UW_CO_MAX_RESUMES") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					resumes.add(c.get(i).ownText());
				}
			}

			for (int i = 0; i < title.size(); i++) {
				Job j = new Job();
				j.appStatus = appStatus.get(i);
				j.emplyer = employer.get(i);
				j.id = id.get(i);
				j.job = job.get(i);
				j.jobStatus = jobStatus.get(i);
				j.resumes = resumes.get(i);
				j.title = title.get(i);
				
				if (!j.id.isEmpty()) {
					jobies.add(j);
				}
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return jobies;
	}
	
	public static void setPassword(String pass){
		pwd = pass;
	}
	
	public static void setUserName(String user){
		userName = user;
	}

}
