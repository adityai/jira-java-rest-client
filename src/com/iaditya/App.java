/**
 * 
 */
package com.bsc.scrm.jira;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
/**
 * @author ainapu01
 *
 */
public class App {


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getJiraUrl() {
		return jiraUrl;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}


	private String username;
	private String password;
	private String query;
	private String jiraUrl;
	private static Logger logger = Logger.getLogger("com.atlassian.jira_soapclient");


	/**
	 * Contructor with args to be set.
	 */
	public App(String args[]) {
		init(args);
	}
	
	/**
	 * Init to store the args into class members.
	 * @param args String args to be passed from command line as parameters.
	 */
	private void init(String[] args) {
		// Initialize
    	if(args.length == 0) {
    		System.out.println("Usage java -jar jire-restclient.jar -u <user> -p <pwd> -q <query>");
    		System.exit(1);
    	}
    	
    	//Collect and store parameter data
    	for(int i=0;i<args.length; i++) {
    		if(args[i].equalsIgnoreCase("-u")) {
    			String userenv=args[i+1];
    			username=System.getenv(userenv);
    			i=i+1;
    		} else if(args[i].equalsIgnoreCase("-p")) {
    			String pwdenv=args[i+1];
    			password=System.getenv(pwdenv);
    			i=i+1;
    		} else if(args[i].equalsIgnoreCase("-q")) {
    			query =args[i+1];  
    			System.out.println("\nJIRA Query = " + query);
    			i=i+1;
			}  
    	}
    	
    	//Set Jira URL
    	jiraUrl=System.getenv("JIRA_URL");
    	if (jiraUrl == null) {
			System.out.println("\nJIRA URL is required. " );
			System.exit(1);
    	}
    	System.out.println("JIRA URL: " + jiraUrl);
    	
    	/*
    	 * Initialize the logger
    	 */
    	logger.setLevel(Level.FINE);
    	FileHandler handler;
		try {
			handler = new FileHandler("debug.log");			
			logger.addHandler(handler);
		} catch (Exception e1) {		
			e1.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JiraRestClient restClient = null;

		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		App app = new App(args);
		try {
			// Initialize jira rest client
			restClient = factory.createWithBasicHttpAuthentication(new URI(app.getJiraUrl()), app.getUsername(), app.getPassword());

			// Get JQL search results
			Promise<SearchResult> resultPromise = restClient.getSearchClient().searchJql(app.getQuery());
			SearchResult result = resultPromise.claim();

			// Get Issue key, resolution date, components and fixversions
			for ( BasicIssue basicIssue : result.getIssues() )
			{
				// Get each Issue object
				Issue issue = restClient.getIssueClient().getIssue(basicIssue.getKey()).claim();
				
				// Get Resolution Date for each issue
				System.out.println("\n" + issue.getKey() + " " + issue.getField("resolutiondate").getValue());
				
				// Get Component names for each issue
				System.out.println( "Components: " );
				for ( BasicComponent component : issue.getComponents() ) {
					System.out.print( component.getName() + ", ");
				}
				
				// Get FixVersions for each issue
				System.out.println( "FixVersions: " );
				for ( Version fixVersions : issue.getFixVersions() ) {
					System.out.print( fixVersions.getName() + ", ");
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			restClient = null;
		}
	}

}
