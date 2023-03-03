package run.duke.info;

import java.net.URI;
import java.nio.file.Files;
import run.duke.Program;
import run.duke.Tool;
import run.duke.ToolFinder;
import run.duke.ToolInstaller;
import run.duke.Workbench;

public record MavenInstaller() implements ToolInstaller {
  @Override
  public String name() {
    return "maven";
  }

  @Override
  public ToolFinder install(Workbench workbench, String version) throws Exception {
    var namespace = namespace();
    var nameAndVersion = name() + "@" + version;
    var folder = workbench.folders().tool(namespace, nameAndVersion);
    Files.createDirectories(folder);
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
    return ToolFinder.of(Tool.of(namespace, nameAndVersion, provider));
  }
}
