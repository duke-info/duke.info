module run.duke.info {
  requires run.duke;

  exports run.duke.info;

  provides run.duke.ToolInstaller with
      run.duke.info.GoogleJavaFormatInstaller,
      run.duke.info.MavenInstaller,
      run.duke.info.EchoInstaller;
}
