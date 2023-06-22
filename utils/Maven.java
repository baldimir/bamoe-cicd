//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-api-maven-embedded:3.1.6
//DEPS org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven-embedded:3.1.6

package utils;

import org.jboss.shrinkwrap.resolver.api.maven.embedded.BuiltProject;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;

import java.io.File;

public final class Maven {

    private Maven() {
        // It is forbidden to create instances of util classes.
    }

    public static int cleanAndInstallProject(final File pomXml, final File settingsXml, final boolean skipTests) {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(pomXml)
                .setGoals("clean", "install")
                .skipTests(skipTests);
        return runMavenConfiguration(mavenRunConfiguration, settingsXml);
    }

    public static int updateVersionOfProject(final File pomXml, final File settingsXml) {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(pomXml)
                .setGoals("versions:set")
                .addProperty("newVersion", BuildParameters.getVersionBAMOE())
                .addProperty("generateBackupPoms", "false")
                .setQuiet();
        return runMavenConfiguration(mavenRunConfiguration, settingsXml);
    }

    public static int setPropertyInPoms(final File pomXml, final String propertyName, final String propertyValue, final File settingsXml) {
        final ConfigurationDistributionStage mavenRunConfiguration = EmbeddedMaven.forProject(pomXml)
                .setGoals("versions:set-property")
                .addProperty("property", propertyName)
                .addProperty("newVersion", propertyValue)
                .setQuiet();
        return runMavenConfiguration(mavenRunConfiguration, settingsXml);
    }

    private static int runMavenConfiguration(final ConfigurationDistributionStage mavenRunConfiguration, final File settingsXml) {
        if (settingsXml != null) {
            mavenRunConfiguration.setUserSettingsFile(settingsXml);
        }
        final BuiltProject builtProject = mavenRunConfiguration.build();
        if (builtProject == null) {
            throw new IllegalStateException("The project was not built properly as it is null!");
        } else {
            return builtProject.getMavenBuildExitCode();
        }
    }
}
