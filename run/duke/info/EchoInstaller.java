package run.duke.info;

import java.nio.file.Files;
import run.duke.Program;
import run.duke.Tool;
import run.duke.ToolFinder;
import run.duke.ToolInstaller;
import run.duke.ToolRunner;
import run.duke.Workbench;

public record EchoInstaller() implements ToolInstaller {
  private static final String ECHO_JAVA =
      // language=java
      """
      class Echo {
        public static void main(String... args) {
          System.out.println(args.length == 0 ? "<silence...>" : String.join(" ", args));
        }
      }
      """;

  @Override
  public String namespace() {
    return getClass().getModule().getName();
  }

  @Override
  public String name() {
    return "echo";
  }

  @Override
  public ToolFinder install(Workbench workbench, String version) throws Exception {
    var folder = workbench.folders().tool(namespace(), name() + "@" + version);
    var echoJava = Files.createDirectories(folder).resolve("Echo.java");
    if (Files.notExists(echoJava)) {
      workbench.log("Writing Java source file -> " + folder.toUri());
      Files.writeString(echoJava, ECHO_JAVA);
    }
    var echoJar = folder.resolve("echo.jar");
    if (Files.notExists(echoJar)) {
      workbench.log("Compiling executable Java archive -> " + folder.toUri());
      var runner = new ToolRunner(workbench, ToolFinder.ofSystem());
      runner.run("javac", "--release", 8, echoJava);
      runner.run("jar", "--create", "--file", echoJar, "--main-class", "Echo", "-C", folder, ".");
    }
    var echoSource = Program.findJavaDevelopmentKitTool("java", echoJava).orElseThrow();
    var echoBinary = Program.findJavaDevelopmentKitTool("java", "-jar", echoJar).orElseThrow();
    return ToolFinder.of(
        Tool.of(namespace(), name() + "@" + version, echoSource),
        Tool.of(namespace(), name() + ".java@" + version, echoSource),
        Tool.of(namespace(), name() + ".jar@" + version, echoBinary));
  }
}
