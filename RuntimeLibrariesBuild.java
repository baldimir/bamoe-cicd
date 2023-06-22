///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS info.picocli:picocli:4.6.3
//SOURCES utils/BuildParameters.java
//SOURCES utils/Maven.java
//FILES build-parameters.properties=build-parameters.properties

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import utils.BuildParameters;
import utils.Maven;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "RuntimeLibraries", mixinStandardHelpOptions = true, version = "RuntimeLibraries 0.1",
        description = "Build of runtime-libraries projects from https://github.com/IBM/bamoe/tree/main/runtime-libraries.")
class RuntimeLibrariesBuild implements Callable<Integer> {

    private static final String VERSION_KOGITO_PROPERTY_KEY = "version.org.kogito";
    private static final String VERSION_DROOLS_PROPERTY_KEY = "version.org.drools";

    @Option(names = {"--mavenProjectDirectory"}, description = "Directory containing bamoe repository." , required = true)
    private File runtimeLibrariesDirectory;

    @Option(names = {"--customSettingsXml"}, description = "Custom settings.xml file")
    private File settingsXml;

    public static void main(String... args) {
        int exitCode = new CommandLine(new RuntimeLibrariesBuild()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        final File pomXml = new File(runtimeLibrariesDirectory, "pom.xml");
        int returnCode = Maven.updateVersionOfProject(pomXml, settingsXml);
        if (returnCode != 0) {
            return returnCode;
        }
        returnCode = Maven.setPropertyInPoms(pomXml, VERSION_KOGITO_PROPERTY_KEY, BuildParameters.getVersionKogito(), settingsXml);
        if (returnCode != 0) {
            return returnCode;
        }
        returnCode = Maven.setPropertyInPoms(pomXml, VERSION_DROOLS_PROPERTY_KEY, BuildParameters.getVersionDrools(), settingsXml);
        if (returnCode != 0) {
            return returnCode;
        }
        return Maven.cleanAndInstallProject(pomXml, settingsXml, true);
    }
}
