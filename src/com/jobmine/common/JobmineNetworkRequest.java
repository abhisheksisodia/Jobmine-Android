package com.jobmine.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;

import com.jobmine.models.Interview;
import com.jobmine.models.Job;

public class JobmineNetworkRequest {

	public static String getJobDescription (Context context, String jobId) {
		
		String descriptionText = "";
		String userName, pwd;
		
		//Set username/password
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		userName = settings.getString(Constants.userNameKey, "");
		pwd = settings.getString(Constants.pwdKey, "");
		
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
		
		String userName, pwd;
		
		//Set username/password
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		userName = settings.getString(Constants.userNameKey, "");
		pwd = settings.getString(Constants.pwdKey, "");

		ArrayList<Job> jobies = new ArrayList<Job>();

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
			Job tempJob = new Job();
			
			for (int i = 0; i < c.size(); i++) {

				if (c.get(i).id().contains("UW_CO_JB_TITLE2") && c.get(i).hasText()) {
					tempJob.title = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_JB_TITLE2") && c.get(i).hasText()) {
					tempJob.title = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_APPS_VW2_UW_CO_JOB_ID") && c.get(i).hasText()) {
					tempJob.id = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_JOBINFOVW_UW_CO_PARENT_NAME") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					tempJob.emplyer = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_TERMCALND_UW_CO_DESCR_30") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					tempJob.job = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_JOBSTATVW_UW_CO_JOB_STATUS") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					tempJob.jobStatus = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_APPSTATVW_UW_CO_APPL_STATUS") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					tempJob.appStatus = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_JOBAPP_CT_UW_CO_MAX_RESUMES") && (c.get(i).id().contains("$$")) && c.get(i).hasText()) {
					tempJob.resumes = c.get(i).ownText();
					
				} else if (c.get(i).id().contains("UW_CO_JOBAPP_D2_UW_CO_ALL_TEXT") && c.get(i).hasText()) {
					jobies.add(tempJob);
					tempJob = new Job();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Job>();
		}
		
		return jobies;
	}
	
	public static ArrayList<Interview> getInterviews(Context context) {
		
		String userName, pwd;
		
		//Set username/password
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		userName = settings.getString(Constants.userNameKey, "");
		pwd = settings.getString(Constants.pwdKey, "");
		
		ArrayList<String> emplyNameList,titleList,dateList,lengthList,timeList,interviewerList,idList;
		ArrayList<Interview> interviews = new ArrayList<Interview>();
		
		emplyNameList = new ArrayList<String>();
		titleList = new ArrayList<String>();
		dateList = new ArrayList<String>();
		lengthList = new ArrayList<String>();
		timeList = new ArrayList<String>();
		interviewerList = new ArrayList<String>();
		idList = new ArrayList<String>();
		
		try {

			DefaultHttpClient client = new DefaultHttpClient();
			HttpProtocolParams.setUserAgent(client.getParams(),"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			HttpPost post = new HttpPost(
					"https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&"
							+ "userid=" + userName + "&" + "pwd=" + pwd + "&" + "submit=Submit");

			HttpResponse resp = client.execute(post);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			resp.getEntity().consumeContent();

			post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_STU_INTVS.GBL?UW_CO_STU_ID="+userName+"&amp;PortalActualURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_STU_INTVS.GBL%3fUW_CO_STU_ID%3d20378462&amp;PortalContentURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_STU_INTVS.GBL&amp;PortalContentProvider=WORK&amp;PortalCRefLabel=Interviews&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsp%2fSS%2f&amp;PortalURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2f&amp;PortalHostNode=WORK&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
			resp = client.execute(post);
			stream = new ByteArrayOutputStream();
			resp.getEntity().writeTo(stream);
			Document webpage = Jsoup.parse(stream.toString());

			Element table = webpage.getElementById("UW_CO_STUD_INTV$scroll$0");
			Elements tableElements = table.getAllElements();
			for(int i = 0; i<tableElements.size(); i++){
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_JOB_ID")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					idList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_PARENT_NAME")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					emplyNameList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_JOBID_HL")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					titleList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_CHAR_DATE")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					dateList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_CHAR_STIME") && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					timeList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_INTV_DUR")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					lengthList.add(tableElements.get(i).text());
				}
				if (tableElements.get(i).id().contains("UW_CO_STUD_INTV_UW_CO_DESCR_50")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					interviewerList.add(tableElements.get(i).text());
				}
			}

			post = new HttpPost(
					"https://jobmine.ccol.uwaterloo.ca/psp/SS/EMPLOYEE/WORK/?cmd=logout");
			client.execute(post);
			
			for (int i = 0; i < emplyNameList.size(); i++) {
				Interview in = new Interview();
				in.employerName = emplyNameList.get(i);
				in.title = titleList.get(i);
				in.date = dateList.get(i);
				in.length = lengthList.get(i);
				in.time = timeList.get(i);
				in.interviewer = interviewerList.get(i);
				in.id = idList.get(i);
				
				if (!in.id.isEmpty()) {
					interviews.add(in);
				}
				
			}
			
		} catch (Exception e){
			e.printStackTrace();
			return new ArrayList<Interview>();
		}
		
		return interviews;
	}
	
}
