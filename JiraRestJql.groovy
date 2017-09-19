//Using Grapes to import dependencies from mvnrepository.com
@Grapes(
        @Grab(group='joda-time', module='joda-time', version='2.3')
)
@Grapes(
        @Grab(group='com.atlassian.jira', module='jira-rest-java-client', version='2.0.0-m2')
)
@Grapes(
        @Grab(group='com.google.guava', module='guava', version='23.0')
)
@Grapes(
        @Grab(group='com.atlassian.httpclient', module='atlassian-httpclient-apache-httpcomponents', version='0.13.2', scope='runtime')
)
@Grapes(
        @Grab(group='com.atlassian.util.concurrent', module='atlassian-util-concurrent', version='3.0.0', scope='provided')
)
@Grapes(
        @Grab(group='com.atlassian.jira', module='jira-rest-java-client-core', version='2.0.0-m19')
)
@Grapes(
        @Grab(group='stax', module='stax-api', version='1.0.1')
)
@Grapes(
        @Grab(group='com.atlassian.httpclient', module='atlassian-httpclient-api', version='0.13.2')
)
@Grapes(
        @Grab(group='com.google.guava', module='guava-parent', version='13.0', type='pom')
)



import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;

def env = System.getenv()
def userName = System.getenv("JIRA_USER_NAME");
def password = System.getenv("JIRA_PASSWORD");
println(userName)

AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
AsynchronousJiraRestClient restClient = factory.createWithBasicHttpAuthentication(new URI('https://issues.jenkins-ci.org/'), 'adityai', 'tester01');

// Get JQL search results
Promise<SearchResult> resultPromise = restClient.getSearchClient().searchJql('Assignee = currentUser() AND Project = "ALM Support and Development"');
SearchResult result = resultPromise.claim();


println('Testing');
