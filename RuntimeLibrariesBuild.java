///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS info.picocli:picocli:4.6.3
//DEPS org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r
//SOURCES utils/BuildParameters.java
//SOURCES utils/Maven.java
//FILES build-parameters.properties=build-parameters.properties

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

    @Option(names = {"--gitURL"}, description = "URL pointing to IBM/bamoe git repository." , defaultValue = "https://github.com/IBM/bamoe.git")
    private String gitURL;

    @Option(names = {"--branch"}, description = "Branch name to clone", defaultValue = "main")
    private String branch;

    @Option(names = {"--tag"}, description = "Tag to clone. If both branch and tag are specified, tag has preference.")
    private String tag;

    @Option(names = {"--customSettingsXml"}, description = "Custom settings.xml file")
    private File settingsXml;

    public static void main(String... args) {
        int exitCode = new CommandLine(new RuntimeLibrariesBuild()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws GitAPIException {
        final File pomXml = cloneRepositoryIfNeeded("bamoe");
        int returnCode = Maven.updateVersionOfProject(pomXml, settingsXml, BuildParameters.getVersionBAMOE());
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

    private File cloneRepositoryIfNeeded(final String repositoryDirectoryPath) throws GitAPIException {
        final File repositoryDirectory = new File(repositoryDirectoryPath);
        if (!repositoryDirectory.exists()) {
            final Git gitRepository = Git.cloneRepository()
                    .setURI(gitURL)
                    .setBranch(branch)
                    .setDirectory(repositoryDirectory)
                    .call();
            if (tag != null) {
                gitRepository.checkout()
                        .setCreateBranch(true)
                        .setName(tag)
                        .setStartPoint("refs/tags/" + tag)
                        .call();
            }
            gitRepository.close();

        }
        return new File(repositoryDirectory.getAbsolutePath() + "/runtime-libraries/pom.xml");
    }
}
