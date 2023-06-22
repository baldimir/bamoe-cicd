package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildParameters {

    private static final String BUILD_PARAMETERS_FILE = "build-parameters.properties";

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
            try (final InputStream propertiesStream = BuildParameters.class.getClassLoader().getResourceAsStream("build-parameters.properties")) {
                buildParametersProperties.load(propertiesStream);
            } catch (IOException e) {
                throw new IllegalStateException("Exception reading properties file " + BUILD_PARAMETERS_FILE + "!", e);
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
