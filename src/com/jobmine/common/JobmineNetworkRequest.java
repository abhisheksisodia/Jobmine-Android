package com.jobmine.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
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
import android.content.SharedPreferences.Editor;

import com.jobmine.models.Interview;
import com.jobmine.models.Job;

public class JobmineNetworkRequest {
	
	public static final int INVALID_ID_PASS = 0;
	public static final int INVALID_TIME = 1;
	public static final int UNKNOWN_ERROR = 2;
	public static final int SUCCESS = 3;
	public static final int SUCCESS_NO_UPDATE = 4;
	
	private static int lastError = -1;
	
	public static synchronized String getJobDescription (Context context, String jobId) {
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		String decodedResult = "";
		String descriptionText = "";
		
		try {
			response = login(context, client); //login to jobmine
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			
			if (decodedResult.contains("Your User ID and/or Password are invalid.")) {
				lastError = INVALID_ID_PASS;
				return null;
			} else if (decodedResult.contains("is not authorized for this time period") || decodedResult.contains("Invalid signon time")) {
				lastError = INVALID_TIME;
				return null;
			}
			
			response = getJobDescriptionResponse(client, jobId);
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());

			response = logout(client);
			response.getEntity().consumeContent();
			
			Document table = Jsoup.parse(decodedResult);
			Element description = table.getElementById("UW_CO_JOBDTL_VW_UW_CO_JOB_DESCR");
			descriptionText = description.html(); 
			
		} catch (Exception e) {
			e.printStackTrace();
			lastError =  UNKNOWN_ERROR;
			return null;
		}
		
		lastError =  SUCCESS;
		return descriptionText;
	}
	
	public static synchronized ArrayList<Job> getApplications (Context context, boolean forceUpdate) {
	
		long now = System.currentTimeMillis() / 1000l;
		long prev = getPref (context, Constants.PREFERENCE_LAST_UPDATE_TIME_APPS, 0);
		if (now - prev < Constants.LAST_UPDATE_TIME_TRESHOLD && !forceUpdate) {
			lastError = SUCCESS_NO_UPDATE;
			return null;
		}
			
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		String decodedResult = "";
		
		ArrayList<Job> jobies = new ArrayList<Job>();
		
		try {
			response = login(context, client); //login to jobmine
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			
			if (decodedResult.contains("Your User ID and/or Password are invalid.")) {
				lastError = INVALID_ID_PASS;
				return null;
			} else if (decodedResult.contains("is not authorized for this time period") || decodedResult.contains("Invalid signon time")) {
				lastError = INVALID_TIME;
				return null;
			}
			
			response = getApplicationsResponse (client);
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());

			response = logout(client);
			response.getEntity().consumeContent();
			
			Document table = Jsoup.parse(decodedResult);

			Elements element = table.getElementsByTag("table");
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
			lastError =  UNKNOWN_ERROR;
			return null;
		}
		
		setPref (context, Constants.PREFERENCE_LAST_UPDATE_TIME_APPS, System.currentTimeMillis() / 1000l);
		lastError = SUCCESS;
		return jobies;
	}
	
	public static synchronized ArrayList<Interview> getInterviews (Context context, boolean forceUpdate) {
		
		long now = System.currentTimeMillis() / 1000l;
		long prev = getPref (context, Constants.PREFERENCE_LAST_UPDATE_TIME_INTER, 0);
		if (now - prev < Constants.LAST_UPDATE_TIME_TRESHOLD && !forceUpdate) {
			lastError = SUCCESS_NO_UPDATE;
			return null;
		}
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		String decodedResult = "";
		
		ArrayList<String> emplyNameList,titleList,dateList,lengthList,timeList,interviewerList,idList,typeList,roomList,instructionsList,statusList;
		ArrayList<String> gIdList, gEmplyNameList, gTitleList, gDateList, gStartTimeList, gEndTimeList, gRoomList, gInstructionsList;
		ArrayList<Interview> interviews = new ArrayList<Interview>();
		
		emplyNameList = new ArrayList<String>();
		titleList = new ArrayList<String>();
		dateList = new ArrayList<String>();
		lengthList = new ArrayList<String>();
		timeList = new ArrayList<String>();
		interviewerList = new ArrayList<String>();
		idList = new ArrayList<String>();
		typeList = new ArrayList<String>();
		roomList = new ArrayList<String>();
		instructionsList = new ArrayList<String>();
		statusList = new ArrayList<String>();
		
		//Arrays for Group interviews
		gIdList = new ArrayList<String>();
		gEmplyNameList = new ArrayList<String>();
		gTitleList = new ArrayList<String>();
		gDateList = new ArrayList<String>();
		gStartTimeList = new ArrayList<String>();
		gEndTimeList = new ArrayList<String>();
		gRoomList = new ArrayList<String>();
		gInstructionsList = new ArrayList<String>();
		
		try {
			response = login(context, client); //login to jobmine
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());
			
			if (decodedResult.contains("Your User ID and/or Password are invalid.")) {
				lastError = INVALID_ID_PASS;
				return null;
			} else if (decodedResult.contains("is not authorized for this time period") || decodedResult.contains("Invalid signon time")) {
				lastError = INVALID_TIME;
				return null;
			}
			
			response = getInterviewsResponse (context, client);
			decodedResult = httpStreamToString(response.getEntity().getContent(), response.getEntity().getContentEncoding());

			response = logout(client);
			response.getEntity().consumeContent();
			
			Document webpage = Jsoup.parse(decodedResult);
			Element table = webpage.getElementById("UW_CO_STUD_INTV$scroll$0");
			Elements tableElements = table.getAllElements();
			for(int i = 0; i<tableElements.size(); i++){
				String id = tableElements.get(i).id();
				if (id.contains("UW_CO_STUD_INTV_UW_CO_JOB_ID")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					idList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_STUD_INTV_UW_CO_PARENT_NAME")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					emplyNameList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_JOBID_HL")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					titleList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_STUD_INTV_UW_CO_CHAR_DATE")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					dateList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_STUD_INTV_UW_CO_CHAR_STIME") && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					timeList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_STUD_INTV_UW_CO_INTV_DUR")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					lengthList.add(tableElements.get(i).text());
				}
				else if (id.contains("UW_CO_STUD_INTV_UW_CO_DESCR_50")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					interviewerList.add(tableElements.get(i).text());
				}
				
				else if (id.contains("win0divUW_CO_STUD_INTV_UW_CO_INTV_TYPE") && tableElements.get(i).hasText()) {
					typeList.add(tableElements.get(i).text());
				}
				else if (id.contains("win0divUW_CO_STUD_INTV_UW_CO_ROOM_ID") && tableElements.get(i).hasText()) {
					roomList.add(tableElements.get(i).text());
				}
				else if (id.contains("win0divUW_CO_STUD_INTV_UW_CO_SCHED_INST") && tableElements.get(i).hasText()) {
					instructionsList.add(tableElements.get(i).text());
				}
				else if (id.contains("win0divUW_CO_STUD_INTV_UW_CO_JOB_STATUS") && tableElements.get(i).hasText()) {
					statusList.add(tableElements.get(i).text());
				}
			}
			
			table = webpage.getElementById("UW_CO_GRP_STU_V$scroll$0");
			tableElements = table.getAllElements();
			for(int i = 0; i<tableElements.size(); i++){
				if (tableElements.get(i).id().contains("UW_CO_GRP_STU_V_UW_CO_JOB_ID")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gIdList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("UW_CO_GRP_STU_V_UW_CO_PARENT_NAME")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gEmplyNameList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("UW_CO_JOBID_HL")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gTitleList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("UW_CO_GRP_STU_V_UW_CO_CHAR_DATE")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gDateList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("UW_CO_GRP_STU_V_UW_CO_CHAR_STIME") && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gStartTimeList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("UW_CO_GRP_STU_V_UW_CO_CHAR_ETIME")
						&& tableElements.get(i).hasText() && !(tableElements.get(i).text().equals(tableElements.get(i+1).text()))) {
					gEndTimeList.add(tableElements.get(i).text());
				}
				else if (tableElements.get(i).id().contains("win0divUW_CO_GRP_STU_V_UW_CO_ROOM_ID") && tableElements.get(i).hasText()) {
					gRoomList.add(tableElements.get(i).text());
				}
				
				else if (tableElements.get(i).id().contains("win0divUW_CO_GRP_STU_V_UW_CO_SCHED_DESCR") && tableElements.get(i).hasText()) {
					gInstructionsList.add(tableElements.get(i).text());
				}
			}
			
			
			
			for (int i = 0; i < emplyNameList.size(); i++) {
				Interview in = new Interview();
				in.employerName = emplyNameList.get(i);
				in.title = titleList.get(i);
				in.date = dateList.get(i);
				in.length = lengthList.get(i);
				in.time = timeList.get(i);
				in.interviewer = interviewerList.get(i);
				in.id = idList.get(i);
				in.type = typeList.get(i);
				in.room = roomList.get(i);
				in.instructions = instructionsList.get(i);
				in.status = statusList.get(i);
				
				if (!in.id.trim().isEmpty()) {
					interviews.add(in);
				}
				
			}
			
			for (int i = 0; i < gEmplyNameList.size(); i++) {
				Interview in = new Interview();
				SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
				in.type = "Group";
				in.employerName = gEmplyNameList.get(i);
				in.title = gTitleList.get(i);
				in.date = gDateList.get(i);
				in.time = gStartTimeList.get(i);
				in.length = ((Long) ((df.parse(gEndTimeList.get(i)).getTime() - df.parse(gStartTimeList.get(i)).getTime())/ (1000 * 60))).toString();
				
				in.id = gIdList.get(i);
				in.room = gRoomList.get(i);
				in.instructions = gInstructionsList.get(i);
				
				if (!in.id.trim().isEmpty()) {
					interviews.add(in);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			lastError =  UNKNOWN_ERROR;
			return null;
		}
		
		setPref (context, Constants.PREFERENCE_LAST_UPDATE_TIME_INTER, System.currentTimeMillis() / 1000l);
		lastError = SUCCESS;
		return interviews;
	}
	
	private static String httpStreamToString (InputStream stream, Header encoding) throws IOException {
		if (encoding != null && encoding.getValue().equals("gzip")) {
			return decryptGZIPStream(stream);
		} else {
			return decryptStream(stream);
		}
	}
	
	private static String decryptGZIPStream (InputStream input) throws IOException {
		GZIPInputStream stream = new GZIPInputStream(input);
		StringBuffer buffer = new StringBuffer ("");
		
		byte tempBytes [] = new byte [1024];
		int bytesRead = 0;
		
		while ((bytesRead = stream.read (tempBytes, 0, 1024)) >= 0) {
		    buffer.append (new String (tempBytes, 0, bytesRead));
		}

		return buffer.toString();
	}
	
	private static String decryptStream (InputStream stream) throws IOException {
    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
    	StringBuilder buffer = new StringBuilder("");
    	String line = null;
    	
    	while ((line = bufferedReader.readLine()) != null) {
    		buffer.append(line);
    	} 
    	
    	return buffer.toString();
	}
	
	private static HttpResponse login (Context context, DefaultHttpClient client) throws ClientProtocolException, IOException {
		
		String userName, pwd;
		
		//Set username/password
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		userName = settings.getString(Constants.userNameKey, "");
		pwd = settings.getString(Constants.pwdKey, "");
		
		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&" + "userid=" + userName + "&" + "pwd=" + pwd + "&" + "submit=Submit");
		post.addHeader("Accept-Encoding", "gzip,deflate");
		return client.execute(post);
	}
	
	private static HttpResponse logout (DefaultHttpClient client) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/EMPLOYEE/WORK/?cmd=logout");
		post.addHeader("Accept-Encoding", "gzip,deflate");
		return client.execute(post);
	}
	
	private static HttpResponse getApplicationsResponse (DefaultHttpClient client) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL?pslnkid=UW_CO_APP_SUMMARY_LINK&FolderPath=PORTAL_ROOT_OBJECT.UW_CO_APP_SUMMARY_LINK&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder&PortalActualURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL%3fpslnkid%3dUW_CO_APP_SUMMARY_LINK&PortalContentURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_APP_SUMMARY.GBL%3fpslnkid%3dUW_CO_APP_SUMMARY_LINK&PortalContentProvider=WORK&PortalCRefLabel=Applications&PortalRegistryName=EMPLOYEE&PortalServletURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsp%2fSS%2f&PortalURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2f&PortalHostNode=WORK&NoCrumbs=yes&PortalKeyStruct=yes");
		post.addHeader("Accept-Encoding", "gzip,deflate");
		return client.execute(post);
	}
	
	private static HttpResponse getJobDescriptionResponse (DefaultHttpClient client, String jobId) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_JOBDTLS?UW_CO_JOB_ID="+jobId);
		post.addHeader("Accept-Encoding", "gzip,deflate");
		return client.execute(post);
	}
	
	private static HttpResponse getInterviewsResponse (Context context, DefaultHttpClient client) throws ClientProtocolException, IOException {
		String userName;
		
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		userName = settings.getString(Constants.userNameKey, "");
		
		HttpPost post = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psc/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_STU_INTVS.GBL?UW_CO_STU_ID="+userName+"&amp;PortalActualURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_STU_INTVS.GBL%3fUW_CO_STU_ID%3d20378462&amp;PortalContentURL=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2fEMPLOYEE%2fWORK%2fc%2fUW_CO_STUDENTS.UW_CO_STU_INTVS.GBL&amp;PortalContentProvider=WORK&amp;PortalCRefLabel=Interviews&amp;PortalRegistryName=EMPLOYEE&amp;PortalServletURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsp%2fSS%2f&amp;PortalURI=https%3a%2f%2fjobmine.ccol.uwaterloo.ca%2fpsc%2fSS%2f&amp;PortalHostNode=WORK&amp;NoCrumbs=yes&amp;PortalKeyStruct=yes");
		post.addHeader("Accept-Encoding", "gzip,deflate");
		return client.execute(post);
	}
	
	private static long getPref (Context context, String key, long defaultValue) {
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		return settings.getLong(key, defaultValue);
	}
	
	private static void setPref (Context context, String key, long value) {
		SharedPreferences settings = new EncryptedSharedPreferences(context, context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE));
		Editor e = settings.edit();
		e.putLong(key, value);
		e.commit();
	}
	
	public static int getLastNetworkError () {
		return lastError;
	}
}