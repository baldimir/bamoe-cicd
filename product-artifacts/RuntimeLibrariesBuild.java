///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3
//DEPS org.apache.maven:maven-embedder:3.9.2
//SOURCES ../utils/BuildParameters.java
//SOURCES ../utils/PomUpdater.java
//FILES build-parameters.properties=../build-parameters.properties

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.maven.cli.MavenCli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import utils.PomUpdater;

@Command(name = "RuntimeLibraries", mixinStandardHelpOptions = true, version = "RuntimeLibraries 0.1",
        description = "Build of runtime-libraries projects from https://github.com/IBM/bamoe/tree/main/runtime-libraries.")
class RuntimeLibrariesBuild implements Callable<Integer> {

    private static final String VERSION_KOGITO_PROPERTY_KEY = "version.org.kogito";
    private static final String VERSION_DROOLS_PROPERTY_KEY = "version.org.drools";

    @Option(names = {"--customSettingsXml"}, description = "Custom setting.xml file", required = false)    
    private File settingsXml;

    public static void main(String... args) {
        int exitCode = new CommandLine(new RuntimeLibrariesBuild()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { 
        int returnCode = updateVersion();
        if (returnCode != 0) {
            return returnCode;
        }

        final Map<String, String> propertiesToChange = new HashMap<>();
        propertiesToChange.put(VERSION_KOGITO_PROPERTY_KEY, BuildParameters.getVersionKogito());
        propertiesToChange.put(VERSION_DROOLS_PROPERTY_KEY, BuildParameters.getVersionDrools());
        PomUpdater.updateValuesInPomXMLs(settingsXml, propertiesToChange);
        return buildTheRepository();
    }

    private int updateVersion() {
        final MavenCli mavenCli = new MavenCli();
        final List<String> mavenArguments = new ArrayList<>();
        mavenArguments.add("versions:set");
        mavenArguments.add("-DnewVersion=" + BuildParameters.getVersionBAMOE());
        mavenArguments.add("-DgenerateBackupPoms=false");
        addMavenSettingsIfSpecified(mavenArguments);
        // TODO project directory 
        return mavenCli.doMain(mavenArguments.toArray(new String[]{}), "project_dir", System.out, System.out);
    }

    private int buildTheRepository() {
        final MavenCli mavenCli = new MavenCli();
        final List<String> mavenArguments = new ArrayList<>();
        mavenArguments.add("clean");
        mavenArguments.add("install");
        mavenArguments.add("-DskipTests");
        addMavenSettingsIfSpecified(mavenArguments);
        // TODO project directory 
        return mavenCli.doMain(mavenArguments.toArray(new String[]{}), "project_dir", System.out, System.out);
    }

    private void addMavenSettingsIfSpecified(final List<String> mavenArguments) {
        if (settingsXml != null) {
            mavenArguments.add("-s");
            mavenArguments.add(settingsXml.getAbsolutePath());
        }
    }
}
