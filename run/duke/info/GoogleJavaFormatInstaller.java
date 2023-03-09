package run.duke.info;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import run.duke.Program;
import run.duke.Tool;
import run.duke.ToolFinder;
import run.duke.ToolInstaller;
import run.duke.Workbench;

public record GoogleJavaFormatInstaller(String name) implements ToolInstaller {
  public GoogleJavaFormatInstaller() {
    this("google-java-format");
  }

  private Map<String, String> assets(String version) {
    if (version.equals("1.15.0")) {
      return Map.of(
          "google-java-format-1.15.0-all-deps.jar",
          "https://github.com/google/google-java-format/releases/download/v1.15.0/google-java-format-1.15.0-all-deps.jar#SIZE=3519780&SHA-256=a356bb0236b29c57a3ab678f17a7b027aad603b0960c183a18f1fe322e4f38ea",
          "README.md",
          "https://github.com/google/google-java-format/raw/v1.15.0/README.md#SIZE=6270");
    }
    if (version.equals("1.16.0")) {
      return Map.of(
          "google-java-format-1.16.0-all-deps.jar",
          "https://github.com/google/google-java-format/releases/download/v1.16.0/google-java-format-1.16.0-all-deps.jar#SIZE=3511159",
          "README.md",
          "https://github.com/google/google-java-format/raw/v1.16.0/README.md#SIZE=6023");
    }
    return Map.of(
        "google-java-format-%s-all-deps.jar".formatted(version),
        "https://github.com/google/google-java-format/releases/download/v%1$s/google-java-format-%1$s-all-deps.jar"
            .formatted(version),
        "README.md",
        "https://github.com/google/google-java-format/raw/v%s/README.md".formatted(version));
  }

  @Override
  public ToolFinder install(Workbench workbench, Path folder, String version) {
    var browser = workbench.browser();
    for (var asset : assets(version).entrySet()) {
      var source = URI.create(asset.getValue());
      var target = folder.resolve(asset.getKey());
      browser.download(source, target);
    }
    var program = Program.findJavaProgram(folder).orElseThrow();
    return ToolFinder.of(Tool.of(namespace(), name() + '@' + version, program));
  }
}
