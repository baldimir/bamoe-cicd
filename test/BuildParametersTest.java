///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS org.assertj:assertj-core:3.24.2
//SOURCES ../utils/BuildParameters.java
//FILES build-parameters.properties=build-parameters-test.properties

import utils.BuildParameters;

import static org.assertj.core.api.Assertions.assertThat;

public final class BuildParametersTest {

    private static final String EXPECTED_BAMOE_VERSION="bamoe.1.Final";
    private static final String EXPECTED_DROOLS_VERSION="drools.1.Final";
    private static final String EXPECTED_KOGITO_VERSION="kogito.1.Final";
    private static final String EXPECTED_GIT_URL_BAMOE_UPSTREAM="https://github.com/IBM/bamoe.git";
    private static final String EXPECTED_GIT_URL_BAMOE_MIRROR="https://mirror/org/bamoe.git";
    private static final String EXPECTED_GIT_URL_DROOLS_UPSTREAM="https://github.com/kiegroup/drools.git";
    private static final String EXPECTED_GIT_URL_DROOLS_MIRROR="https://mirror/org/drools.git";
    private static final String EXPECTED_GIT_URL_KOGITO_RUNTIMES_UPSTREAM="https://github.com/kiegroup/kogito-runtimes.git";
    private static final String EXPECTED_GIT_URL_KOGITO_RUNTIMES_MIRROR="https://mirror/org/kogito-runtimes.git";
    private static final String EXPECTED_GIT_URL_KOGITO_APPS_UPSTREAM="https://github.com/kiegroup/kogito-apps.git";
    private static final String EXPECTED_GIT_URL_KOGITO_APPS_MIRROR="https://mirror/org/kogito-apps.git";

    public static void getVersionBAMOE() {
        assertThat(BuildParameters.getVersionBAMOE()).isEqualTo(EXPECTED_BAMOE_VERSION);
    }

    public static void getVersionDrools() {
        assertThat(BuildParameters.getVersionDrools()).isEqualTo(EXPECTED_DROOLS_VERSION);
    }

    public static void getVersionKogito() {
        assertThat(BuildParameters.getVersionKogito()).isEqualTo(EXPECTED_KOGITO_VERSION);
    }

    public static void getGitURLBAMOEUpstream() {
        assertThat(BuildParameters.getGitURLBAMOEUpstream()).isEqualTo(EXPECTED_GIT_URL_BAMOE_UPSTREAM);
    }

    public static void getGitURLBAMOEMirror() {
        assertThat(BuildParameters.getGitURLBAMOEMirror()).isEqualTo(EXPECTED_GIT_URL_BAMOE_MIRROR);
    }

    public static void getGitURLDroolsUpstream() {
        assertThat(BuildParameters.getGitURLDroolsUpstream()).isEqualTo(EXPECTED_GIT_URL_DROOLS_UPSTREAM);
    }

    public static void getGitURLDroolsMirror() {
        assertThat(BuildParameters.getGitURLDroolsMirror()).isEqualTo(EXPECTED_GIT_URL_DROOLS_MIRROR);
    }

    public static void getGitURLKogitoRuntimesUpstream() {
        assertThat(BuildParameters.getGitURLKogitoRuntimesUpstream()).isEqualTo(EXPECTED_GIT_URL_KOGITO_RUNTIMES_UPSTREAM);
    }

    public static void getGitURLKogitoRuntimesMirror() {
        assertThat(BuildParameters.getGitURLKogitoRuntimesMirror()).isEqualTo(EXPECTED_GIT_URL_KOGITO_RUNTIMES_MIRROR);
    }

    public static void getGitURLKogitoAppsUpstream() {
        assertThat(BuildParameters.getGitURLKogitoAppsUpstream()).isEqualTo(EXPECTED_GIT_URL_KOGITO_APPS_UPSTREAM);
    }

    public static void getGitURLKogitoAppsMirror() {
        assertThat(BuildParameters.getGitURLKogitoAppsMirror()).isEqualTo(EXPECTED_GIT_URL_KOGITO_APPS_MIRROR);
    }

    public static void main(String... args) {
        getVersionBAMOE();
        getVersionDrools();
        getVersionKogito();
        getGitURLBAMOEUpstream();
        getGitURLBAMOEMirror();
        getGitURLDroolsUpstream();
        getGitURLDroolsMirror();
        getGitURLKogitoRuntimesUpstream();
        getGitURLKogitoRuntimesMirror();
        getGitURLKogitoAppsUpstream();
        getGitURLKogitoAppsMirror();
    }
}
