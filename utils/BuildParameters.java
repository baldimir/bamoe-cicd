package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildParameters {

    private static final String BUILD_PARAMETERS_FILE = "build-parameters.properties";

    private static final String VERSION_BAMOE_KEY = "version.bamoe";
    private static final String VERSION_KOGITO_KEY = "version.kogito";
    private static final String VERSION_DROOLS_KEY = "version.drools";
    private static final String GIT_URL_BAMOE_UPSTREAM="git.url.bamoe.upstream";
    private static final String GIT_URL_BAMOE_MIRROR="git.url.bamoe.mirror";
    private static final String GIT_URL_DROOLS_UPSTREAM="git.url.drools.upstream";
    private static final String GIT_URL_DROOLS_MIRROR="git.url.drools.mirror";
    private static final String GIT_URL_KOGITO_RUNTIMES_UPSTREAM="git.url.kogito.runtimes.upstream";
    private static final String GIT_URL_KOGITO_RUNTIMES_MIRROR="git.url.kogito.runtimes.mirror";
    private static final String GIT_URL_KOGITO_APPS_UPSTREAM="git.url.kogito.apps.upstream";
    private static final String GIT_URL_KOGITO_APPS_MIRROR="git.url.kogito.apps.mirror";

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

    public static String getGitURLBAMOEUpstream() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_BAMOE_UPSTREAM);
    }

    public static String getGitURLBAMOEMirror() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_BAMOE_MIRROR);
    }

    public static String getGitURLDroolsUpstream() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_DROOLS_UPSTREAM);
    }

    public static String getGitURLDroolsMirror() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_DROOLS_MIRROR);
    }

    public static String getGitURLKogitoRuntimesUpstream() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_KOGITO_RUNTIMES_UPSTREAM);
    }

    public static String getGitURLKogitoRuntimesMirror() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_KOGITO_RUNTIMES_MIRROR);
    }

    public static String getGitURLKogitoAppsUpstream() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_KOGITO_APPS_UPSTREAM);
    }

    public static String getGitURLKogitoAppsMirror() {
        initBuildParameters();
        return buildParametersProperties.getProperty(GIT_URL_KOGITO_APPS_MIRROR);
    }
}
