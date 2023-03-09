package run.duke.info;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import run.duke.Program;
import run.duke.Tool;
import run.duke.ToolFinder;
import run.duke.ToolInstaller;
import run.duke.Workbench;

public record MavenInstaller(String name) implements ToolInstaller {
  public MavenInstaller() {
    this("maven");
  }

  @Override
  public ToolFinder install(Workbench workbench, Path folder, String version) throws Exception {
    var base = "https://repo.maven.apache.org/maven2/org/apache/maven/";
    var mavenWrapperProperties = folder.resolve("maven-wrapper.properties");
    if (Files.notExists(mavenWrapperProperties)) {
      Files.writeString(
          mavenWrapperProperties,
          // language=properties
          """
          distributionUrl=%sapache-maven/%s/apache-maven-%s-bin.zip
          """
              .formatted(base, version, version));
    }
    var uri = URI.create(base + "wrapper/maven-wrapper/3.1.1/maven-wrapper-3.1.1.jar#SIZE=59925");
    var mavenWrapperJar = folder.resolve("maven-wrapper.jar");
    workbench.browser().download(uri, mavenWrapperJar);
    var provider =
        Program.findJavaDevelopmentKitTool(
                "java",
                "-D" + "maven.multiModuleProjectDirectory=.",
                "--class-path=" + mavenWrapperJar,
                "org.apache.maven.wrapper.MavenWrapperMain")
            .orElseThrow();
    return ToolFinder.of(Tool.of(namespace(), name() + '@' + version, provider));
  }
}
