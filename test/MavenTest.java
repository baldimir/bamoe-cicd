///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17
//DEPS org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r
//DEPS org.assertj:assertj-core:3.24.2
//SOURCES ../utils/Maven.java
//SOURCES TestsConstants.java
//FILES failing-settings.xml=failing-settings.xml

import org.assertj.core.api.Assertions;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import utils.Maven;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenTest {

    public static void cleanAndInstallProjectNoCustomSettingsXmlSkipTests() throws GitAPIException, IOException {
        setup();
        cleanAndInstallProject(true);
    }

    public static void cleanAndInstallProjectNoCustomSettingsXmlRunTests() throws GitAPIException, IOException {
        setup();
        cleanAndInstallProject(false);
    }

    private static void cleanAndInstallProject(final boolean skipTests) throws GitAPIException {
        final File bamoeRepositoryDirectory = new File(TestsConstants.TESTS_OUTPUT_DIRECTORY_NAME + "/bamoe");
        try (final Git gitRepository = Git.cloneRepository()
                .setURI("https://github.com/IBM/bamoe.git")
                .setBranch("main")
                .setDirectory(bamoeRepositoryDirectory)
                .call()) {
            final File pomXml = new File(gitRepository.getRepository().getWorkTree().getAbsolutePath() + "/runtime-libraries/pom.xml");
            assertThat(Maven.cleanAndInstallProject(pomXml, null, skipTests)).isEqualTo(0);
        }
        assertThat(new File(bamoeRepositoryDirectory.getAbsolutePath() + "/runtime-libraries/ilmt-compliance/ilmt-compliance-common/target")).exists();
        assertThat(new File(bamoeRepositoryDirectory.getAbsolutePath() + "/runtime-libraries/ilmt-compliance/ilmt-compliance-common/target")).isNotEmptyDirectory();
        if (skipTests) {
            assertThat(new File(bamoeRepositoryDirectory.getAbsolutePath() + "/runtime-libraries/ilmt-compliance/ilmt-compliance-common/target/surefire-reports/")).doesNotExist();
        } else {
            assertThat(new File(bamoeRepositoryDirectory.getAbsolutePath() + "/runtime-libraries/ilmt-compliance/ilmt-compliance-common/target/surefire-reports/")).exists();
            assertThat(new File(bamoeRepositoryDirectory.getAbsolutePath() + "/runtime-libraries/ilmt-compliance/ilmt-compliance-common/target/surefire-reports/")).isNotEmptyDirectory();
        }
    }

    public static void cleanAndInstallProjectCustomSettingsXml() throws GitAPIException, IOException {
        setup();
        final File bamoeRepositoryDirectory = new File(TestsConstants.TESTS_OUTPUT_DIRECTORY_NAME + "/bamoe");
        try (final Git gitRepository = Git.cloneRepository()
                .setURI("https://github.com/IBM/bamoe.git")
                .setBranch("main")
                .setDirectory(bamoeRepositoryDirectory)
                .call()) {
            final File pomXml = new File(gitRepository.getRepository().getWorkTree().getAbsolutePath() + "/runtime-libraries/pom.xml");
            Assertions.assertThatThrownBy(() -> Maven.cleanAndInstallProject(pomXml, new File("test/failing-settings.xml"), true))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    public static void updateVersionOfProject() throws GitAPIException, IOException {
        setup();
        final File bamoeRepositoryDirectory = new File(TestsConstants.TESTS_OUTPUT_DIRECTORY_NAME + "/bamoe");
        try (final Git gitRepository = Git.cloneRepository()
                .setURI("https://github.com/IBM/bamoe.git")
                .setBranch("main")
                .setDirectory(bamoeRepositoryDirectory)
                .call()) {
            final File pomXml = new File(gitRepository.getRepository().getWorkTree().getAbsolutePath() + "/runtime-libraries/pom.xml");
            final String newVersion = "test1";
            assertThat(Maven.updateVersionOfProject(pomXml, null, newVersion)).isEqualTo(0);
            Assertions.assertThat(pomXml).content(StandardCharsets.UTF_8).contains("<version>" + newVersion + "</version>");
            Assertions.assertThatThrownBy(() -> Maven.updateVersionOfProject(pomXml, new File("test/failing-settings.xml"), "test2"))
                    .isInstanceOf(IllegalStateException.class);
            Assertions.assertThatThrownBy(() -> Maven.updateVersionOfProject(pomXml, null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    public static void setPropertyInPoms() throws GitAPIException, IOException {
        setup();
        final File bamoeRepositoryDirectory = new File(TestsConstants.TESTS_OUTPUT_DIRECTORY_NAME + "/bamoe");
        try (final Git gitRepository = Git.cloneRepository()
                .setURI("https://github.com/IBM/bamoe.git")
                .setBranch("main")
                .setDirectory(bamoeRepositoryDirectory)
                .call()) {
            final File pomXml = new File(gitRepository.getRepository().getWorkTree().getAbsolutePath() + "/runtime-libraries/pom.xml");
            final String propertyName = "version.org.kogito";
            final String newPropertyValue = "test1";
            assertThat(Maven.setPropertyInPoms(pomXml, propertyName, newPropertyValue, null)).isEqualTo(0);
            Assertions.assertThat(pomXml).content(StandardCharsets.UTF_8).contains("<" + propertyName + ">" + newPropertyValue + "</" + propertyName + ">");
            Assertions.assertThatThrownBy(() -> Maven.setPropertyInPoms(pomXml, propertyName, newPropertyValue, new File("test/failing-settings.xml")))
                    .isInstanceOf(IllegalStateException.class);
            Assertions.assertThatThrownBy(() -> Maven.setPropertyInPoms(pomXml, null,null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    public static void main(String... args) throws GitAPIException, IOException {
        cleanAndInstallProjectNoCustomSettingsXmlSkipTests();
        cleanAndInstallProjectNoCustomSettingsXmlRunTests();
        cleanAndInstallProjectCustomSettingsXml();
        updateVersionOfProject();
        setPropertyInPoms();
    }

    private static void setup() throws IOException {
        final File testsOutputDirectory = new File(TestsConstants.TESTS_OUTPUT_DIRECTORY_NAME);
        if (testsOutputDirectory.exists()) {
            try (final Stream<Path> pathsToDelete = Files.walk(testsOutputDirectory.toPath())) {
                pathsToDelete.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> assertThat(file.delete()).isTrue());
            }
        }
        assertThat(testsOutputDirectory.mkdir()).isTrue();
    }
}
