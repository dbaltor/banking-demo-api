package online.dbaltor.demoapi.acceptance;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features/banking")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty,\n" +
        "html:target/AcceptanceTestReports/report.html,\n" +
        "json:target/AcceptanceTestReports/report.json,\n" +
        "junit:target/AcceptanceTestReports/junit.xml")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "online.dbaltor.demoapi.acceptance.steps")
public class BankingAT {
}
