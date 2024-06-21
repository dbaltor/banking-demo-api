package online.dbaltor.demoapi.acceptance;

import static io.cucumber.core.options.Constants.*;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasspathResource("features")
@SelectPackages("online.dbaltor.demoapi.acceptance.steps")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value =
                "pretty,\n"
                        + "html:target/AcceptanceTestReports/report.html,\n"
                        + "json:target/AcceptanceTestReports/report.json,\n"
                        + "junit:target/AcceptanceTestReports/junit.xml")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @Ignore")
public class BankingAT {}
