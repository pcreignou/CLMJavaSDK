package clmworkflow;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.docusign.clm.api.WorkflowsApi;
import com.docusign.clm.model.Workflow;
import com.docusign.clm.model.WorkflowDefinition;
import com.docusign.clm.model.WorkflowSummary;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.OAuthToken;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;

public class StartWorkflow {

	static String DevCenterPage = "https://developers.docusign.com/platform/auth/consent";

	/**
	 * Application entry point.
	 *
	 * @param args application command line arguments
	 */
	public static void main(String[] args) throws java.io.IOException {

		

		// Get information fro app.config
		Properties prop = new Properties();
		String fileName = "app.config";
		String documentId = "50bec23f-88f6-ee11-a136-9440c98d8e91";
		final byte[] privateKeyBytes = Base64.getDecoder().decode(
				"LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQ0KTUlJRXBBSUJBQUtDQVFFQXE2d1hJR1R0TTcyd25pM2Z0bnNzS1RHS2JsYVRlaTJOb2RoRndtbTNaY3RYU1NxRw0KelpHcFRaRGxPZHJsN2htckNiZHpGZUVCVkhGQThBNFRsUDg4YjRRSEF0WnUrOVR4UkRSTk9sSDBYVEJFSFlTSg0KK3pkMjNidjNuVUxZaEozZ1cyRlhBQi9uem5xcVd3MTk2NUFpR0RZTEgyNUdsUmhJZkVWMEQxYUhvTjFmZzU1Vw0KRjBOUFh5cDA4NDNoQkIxNXEzMWl0ckdKakRZOWRYOGZJclFKSWZuK0V1NDlBMExQd1BLOWd6elZtR0hLWWRCVw0KMmRCa2JDMFVjTWtzWTJEQ3A2ZXlTRHBlci8vSjlscXFPK2VGeVhGUENjWHFLckhySlNlTWtYampmRDhEdWJUcg0KN0dwclN4V1c4ZWgzYm90Y2FtcktmNzcrbWRWYUNrTVVtaEJ5aFFJREFRQUJBb0lCQURjd2l2cktGNHZUeHMwRA0KZUhGRTVleVVWYW1sUWFJOHROUzhINmtocldrWFZ3ZFptWjdLM2ovbWZocjhhWUVEVlg0NU1ORnUxYXVkQVVCcg0KZGhZbmUvbHAyNHNvL3VNTkVVZFkvUjdyckFDdGZLaDBySnRMdUhZT2NNdG82d3l2YUtwWC84MDFkS05ud2c2bA0KcHI2dFJwaTcveFZxc1Z4TFF6cFlaYmhzbkF1MXVIRERFVW51VEdSMVpXeWdLK1RRcDRkbzRiNVg4bjZpQ295aQ0KSXdmSktDTE1uNVNZYVB4NWFJSmRzcDBySWFnbWlDeFhZRHEzK29YODNLa2JIVjJHMmFURE9iczhIemF6cC90ZA0KMkplbXl5SytuVjArdHhYWmZkYTRzVkZtSmsvUTVESFNuL0FFRUxEek9zMjVpMXZoay9qUmVHdERFaWN4cXN4RA0KU3V3alpwMENnWUVBM25GV29LdC9lUHYxeE9OaGFjOUZjZWo5RnhPc1VUeHEvdkZtM1M2QXN6bVZaaHBNUjVUbw0KS3lYakxNbEQvSEV5VjRZb2hQdmlNSTFHcTVhWURCb2FZSEgxbzdwY01McWN5ckVBNVhqcktsY0xqaHdERzdzZg0KR2Fxai9rclUrL1hsYU5YM2RkbkwzK2NDU0Z2NWQ2eE4zeDdpS1g5aTBGNWJNNVZsT0Z3RkJ2c0NnWUVBeFpJRA0KUUhqT3U0bXFOWlhZeW1iRGlsRVNITVQxN0tTbG1YMjdUMXRlWEpRM2dxNUlsWmJERThML1JZMTN1ZmlvbVErWA0KclVyb0FMM1lpYlV5T2VFdzBIZjBQejk4aUVCZWpjdnVxWGNwWUtwQnNJQyt4OUd1cFphVFFQa1crVUM4cmpaag0KN1R6UmNseFA2cnVsQ2FISmJkdnAwWHhBMlZWTW5Dd0N4WDNGTkg4Q2dZRUF2UUFIM3RSWXB1OU9UMC96Z1BlLw0KeFoxMURacUZtekIrcU1kcjR4cVQ5N1JCb0ZOUDVMSTg0Z3g3RFo2a3lXVlVwTkUwdEdsZ01taVhWMHZKUE44Zw0KOGpsaHdRK2pMVFFoN0lhZ2dGbFJWdFJHMlNRcjcrLzU2TkQ0TGlSZSt3WmZPY2d6VGtzVDB0WnhOaElQMFFMeA0KMTFaaVMyZ1ZDWDVpZDEzRWR6S1lndWtDZ1lFQXRzeHpQOHJIRkpPNkxadzRtb1RjZFhTdk9ua3VSVWJOaGFmaw0Kai9jczVxR29mcnY4b3BOOGFTTnBoeFB1YTk1Yk9FdEovbWVwSTA2RU5GMjNYQUEwWng0bG1nT0dtSlplSmZwRA0KRnY4UEJNWml3d2xMKzV2UmFKcGFJcXFWakFkT3pDMjg1VHI1VzBONEdlcUdOOVEraCt3S2VlM3FIbXdpMXZpTg0KaytiYmVNY0NnWUFKa2hQZ0t2cUI4S21CTkdyNHkvVUpDS3VXNzBzRmRGc0lpM2NuaFg4Um9QZ1ZjOHZUWGtWQg0Kd0JiUy9scHRNc3FwRWFkYlgrWHMxdVhLTFF0RWdKUkxWZEhYbUVNTEVEUG05emZkdlRPckNOZnBqV1BYQmFRZQ0KeEptbGdqWFk2d2FScURJcXY1UkNFamFwVXQxWTBTZlJiM0N3TGlIWXlzcUV6VVNISnZMZ213PT0NCi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0t");

		FileInputStream fis = new FileInputStream(fileName);
		prop.load(fis);
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Welcome to the JWT Code example! ");
			System.out.print("Enter the Workflow Name: ");
			String workflowName = scanner.nextLine();
			System.out.print("Enter the Worklows Payload(params) in JSON or XML: ");
			String params = scanner.nextLine();
			System.out.print("Enter documentId to download(default to 50bec23f-88f6-ee11-a136-9440c98d8e91 ): ");
			String document = scanner.nextLine();
			if(!document.isBlank()) {
				documentId = document;
			}			
			
			// Get access token and accountId
			ApiClient apiClient = new ApiClient(ApiClient.CLM_UAT_REST_BASEPATH);
			apiClient.setOAuthBasePath("account-d.docusign.com");
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add("signature");
			scopes.add("impersonation");
			scopes.add("spring_read");
			scopes.add("spring_write");

			OAuthToken oAuthToken = apiClient.requestJWTUserToken(prop.getProperty("clientId"),
					prop.getProperty("userId"), scopes, privateKeyBytes, 3600);
			String accessToken = oAuthToken.getAccessToken();
			
			UserInfo userInfo = apiClient.getUserInfo(accessToken);
			String accountId = userInfo.getAccounts().get(0).getAccountId();
			System.out.println("userInfo: " + userInfo);

			WorkflowDefinition wf = new WorkflowDefinition();
			wf.setName(workflowName);
			wf.setParams("\""+params+"\"");

			apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
			apiClient.setBasePath(ApiClient.CLM_UAT_REST_BASEPATH);
			apiClient.setOAuthBasePath(OAuth.DEMO_OAUTH_BASEPATH);
			WorkflowsApi workflowsApi = new WorkflowsApi(apiClient);
			WorkflowSummary results = workflowsApi.createWorkflow(accountId, wf);
			System.out.println("Successfully triggered WF: " + results.getHref());
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
			// retrieve WF
			Workflow myworkflow = workflowsApi.getWorkflow(accountId, results.getHref());
			System.out.println("WF status: " + myworkflow.getStatus());

			// Download document attention change the basePath !!!!
			apiClient.setBasePath(ApiClient.CLM_UAT_DOWNLOAD_BASEPATH);
			byte[] doc = workflowsApi.getDocument(accountId, documentId);
			FileUtils.writeByteArrayToFile(new File(accountId + "_" + documentId + ".pdf"), doc);

			System.out.println("Test completed !!!!");

		}
		/*
		 * catch (ApiException exp) { if (exp.getMessage().contains("consent_required"))
		 * { try { System.out.println
		 * ("Consent required, please provide consent in browser window and then run this app again."
		 * ); Desktop.getDesktop().browse(new URI(
		 * "https://account-d.docusign.com/oauth/auth?response_type=code&scope=impersonation%20signature&client_id="
		 * + prop.getProperty("clientId") + "&redirect_uri=" + DevCenterPage)); } catch
		 * (Exception e) { System.out.print ("Error!!!  "); System.out.print
		 * (e.getMessage()); } } }
		 */
		catch (Exception e) {
			System.out.print("Error!!!  ");
			System.out.print(e.getMessage());
		}
	}
}