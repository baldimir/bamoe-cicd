///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS info.picocli:picocli:4.6.3
//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-embedded:3.1.6
//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-embedded:3.1.6
//SOURCES utils/BuildParameters.java
//FILES build-parameters.properties=build-parameters.properties

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import utils.BuildParameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "RuntimeLibraries", mixinStandardHelpOptions = true, version = "RuntimeLibraries 0.1",
        description = "Build of runtime-libraries projects from https://github.com/IBM/bamoe/tree/main/runtime-libraries.")
class RuntimeLibrariesBuild implements Callable<Integer> {

    private static final String VERSION_KOGITO_PROPERTY_KEY = "version.org.kogito";
    private static final String VERSION_DROOLS_PROPERTY_KEY = "version.org.drools";

    @Option(names = {"--mavenProjectDirectory"}, description = "Directory containing bamoe repository." , required = true)
    private File runtimeLibrariesDirectory;

    @Option(names = {"--customSettingsXml"}, description = "Custom setting.xml file", required = false)    
    private File settingsXml;

    public static void main(String... args) {
        int exitCode = new CommandLine(new RuntimeLibrariesBuild()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        int returnCode = updateVersion();
        if (returnCode != 0) {
            return returnCode;
        }
        returnCode = alignVersionKogito();
        if (returnCode != 0) {
            return returnCode;
        }
        returnCode = alignVersionDrools();
        if (returnCode != 0) {
            return returnCode;
        }
        return buildTheRepository();
    }

    private int updateVersion() {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(new File(runtimeLibrariesDirectory, "pom.xml"))
                .setGoals("versions:set")
                .addProperty("newVersion", BuildParameters.getVersionBAMOE())
                .addProperty("generateBackupPoms", "false")
                .setQuiet();
        return runMavenConfiguration(mavenRunConfiguration);
    }

    private int alignVersionKogito() {
        return setPropertyInPoms(VERSION_KOGITO_PROPERTY_KEY, BuildParameters.getVersionKogito());
    }

    private int alignVersionDrools() {
        return setPropertyInPoms(VERSION_DROOLS_PROPERTY_KEY, BuildParameters.getVersionDrools());
    }

    // TODO - these methods could be extracted to a util class named like "MavenUtils" or similar.
    //  Basically covering basic Maven operations that are needed.
    private int setPropertyInPoms(final String propertyName, final String propertyValue) {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(new File(runtimeLibrariesDirectory, "pom.xml"))
                .setGoals("versions:set-property")
                .addProperty("property", propertyName)
                .addProperty("newVersion", propertyValue)
                .setQuiet();
        return runMavenConfiguration(mavenRunConfiguration);
    }

    private int buildTheRepository() {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(new File(runtimeLibrariesDirectory, "pom.xml"))
                .setGoals("clean", "install")
                .skipTests(true);
        return runMavenConfiguration(mavenRunConfiguration);
    }

    private int runMavenConfiguration(final ConfigurationDistributionStage mavenRunConfiguration) {
        addMavenSettingsIfSpecified(mavenRunConfiguration);
        final BuiltProject builtProject = mavenRunConfiguration.build();
        if (builtProject == null) {
            throw new IllegalStateException("The project was not built properly as it is null!");
        } else {
            return builtProject.getMavenBuildExitCode();
        }
    }

    private void addMavenSettingsIfSpecified(final ConfigurationDistributionStage mavenRunConfiguration) {
        if (settingsXml != null) {
            mavenRunConfiguration.setUserSettingsFile(settingsXml);
        }
    }
}
