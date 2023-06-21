import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class BuildParameters {

    private static final String VERSION_BAMOE_KEY = "version.bamoe";
    private static final String VERSION_KOGITO_KEY = "version.kogito";
    private static final String VERSION_DROOLS_KEY = "version.drools";

    private static Properties buildParametersProperties;

    private BuildParameters() {
        // It is forbidden to create instances of util classes.
    }

    private static synchronized void initBuildParameters() {
        if (buildParametersProperties == null) {
            buildParametersProperties = new Properties();
            final String propertiesPath = Thread.currentThread().getContextClassLoader().getResource("build-parameters.properties").getPath();
            try (final FileInputStream propertiesStream = new FileInputStream(propertiesPath)) {
                buildParametersProperties.load(propertiesStream);
            } catch (IOException e) {
                throw new IllegalStateException("Exception reading properties file build-parameters.properties!");
            }
        }
    }

    public static String getVersionBAMOE() {
        initBuildParameters();
        return buildParametersProperties.getProperty(VERSION_BAMOE_KEY);
    }

    public static String getVersionKogito() {
        initBuildParameters();
        return buildParametersProperties.getProperty(VERSION_KOGITO_KEY);
    }

    public static String getVersionDrools() {
        initBuildParameters();
        return buildParametersProperties.getProperty(VERSION_DROOLS_KEY);
    }
}
