package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.DryRegionBuilder;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.DryRegion;
import simulator.model.PopulationStats;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

public class Main {

  public static Factory<SelectionStrategy> selectionStrategyFactory;
  public static Factory<Region> selectionRegionFactory;
  public static Factory<Animal> selecionAnimalFactory;

  private static Simulator sim;
  private static Controller controller;

  private final static Double DEFAULT_DELTA_TIME = 0.03;
  public static Double deltaTime = 0.1;
  private static String outFile = null;
  private static boolean simpleViewer = false;
  private final static Double DEFAULT_TIME = 10.0;
  private static Double time = null;
  private static String inFile = null;
  private static ExecMode mode = ExecMode.BATCH;
  private static Double populationLimit = null;

  private enum ExecMode {
    BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

    private String tag;
    private String desc;

    private ExecMode(String modeTag, String modeDesc) {
      tag = modeTag;
      desc = modeDesc;
    }

    public String getTag() {
      return tag;
    }

    public String getDesc() {
      return desc;
    }
  }

  // Parse
  private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
    String dt = line.getOptionValue("dt", "0.1");
    try {
      deltaTime = Double.parseDouble(dt);
    } catch (Exception e) {
      throw new ParseException("Invalid value for delta-time: " + dt);
    }
  }

  private static void parsePopulationSurpass(CommandLine line){
    if(line.hasOption("ps")){
      populationLimit = Double.parseDouble(line.getOptionValue("ps"));
    }
  }

  private static void parseOutFileOption(CommandLine line) {
    outFile = line.getOptionValue("o");
  }

  private static void parseSimpleViewerOption(CommandLine line) {
    simpleViewer = line.hasOption("sv");
  }

  private static void parseModeOption(CommandLine line) throws ParseException {
    String modeValue = line.getOptionValue("m", "gui");
    for (ExecMode m : ExecMode.values()) {
      if (modeValue.equals(m.getTag())) {
        mode = m;
        return;
      }
    }
    throw new ParseException("Invalid mode: " + modeValue);
  }

  private static void parseArgs(String[] args) {

    // define the valid command line options
    //
    Options cmdLineOptions = buildOptions();

    // parse the command line as provided in args
    //
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine line = parser.parse(cmdLineOptions, args);
      parseHelpOption(line, cmdLineOptions);
      parseModeOption(line);
      parseInFileOption(line);
      parseTimeOption(line);
      parseDeltaTimeOption(line);
      parseOutFileOption(line);
      parseSimpleViewerOption(line);
      parsePopulationSurpass(line);

      // if there are some remaining arguments, then something wrong is
      // provided in the command line!
      //
      String[] remaining = line.getArgs();
      if (remaining.length > 0) {
        String error = "Illegal arguments:";
        for (String o : remaining)
          error += (" " + o);
        throw new ParseException(error);
      }

    } catch (ParseException e) {
      System.err.println(e.getLocalizedMessage());
      System.exit(1);
    }

  }

  private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
    if (line.hasOption("h")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
      System.exit(0);
    }
  }

  private static void parseInFileOption(CommandLine line) throws ParseException {
    inFile = line.getOptionValue("i");
    if (mode == ExecMode.BATCH && inFile == null) {
      throw new ParseException("In batch mode an input configuration file is required");
    }
  }

  private static void parseTimeOption(CommandLine line) throws ParseException {
    String t = line.getOptionValue("t", DEFAULT_TIME.toString());
    try {
      time = Double.parseDouble(t);
      assert (time >= 0);
    } catch (Exception e) {
      throw new ParseException("Invalid value for time: " + t);
    }
  }

  private static Options buildOptions() {
    Options cmdLineOptions = new Options();

    // dt
    cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
        .desc("A real number representing the time step. Default value: 0.1.").build());

    // output file -o
    cmdLineOptions
        .addOption(Option.builder("o").longOpt("output").hasArg().desc("A file where output is written.").build());

    // -ps
    cmdLineOptions
        .addOption(Option.builder("ps").longOpt("population-surpass").hasArg().desc("A double representing the population limit to track surpasses").build());
    // Simple viewer -sv
    cmdLineOptions.addOption(
        Option.builder("sv").longOpt("simple-viewer").desc("If present, show the graphical viewer.").build());

    // help
    cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

    // input file
    cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

    // -m/--mode
    cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
        .desc("Execution mode. Possible values: 'batch' (batch mode) and 'gui' (GUI mode). Default value: gui.")
        .build());

    // steps
    cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
        .desc("An real number representing the total simulation time in seconds. Default value: "
            + DEFAULT_TIME + ".")
        .build());

    return cmdLineOptions;
  }

  private static void initFactories() {
    // strategies factory
    List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();
    selectionStrategyBuilders.add(new SelectFirstBuilder());
    selectionStrategyBuilders.add(new SelectClosestBuilder());
    selectionStrategyBuilders.add(new SelectYoungestBuilder());
    selectionStrategyFactory = new BuilderBasedFactory<SelectionStrategy>(selectionStrategyBuilders);

    // regions factory
    List<Builder<Region>> selectionRegionBuilders = new ArrayList<>();
    selectionRegionBuilders.add(new DefaultRegionBuilder());
    selectionRegionBuilders.add(new DynamicSupplyRegionBuilder());
    selectionRegionBuilders.add(new DryRegionBuilder());
    selectionRegionFactory = new BuilderBasedFactory<Region>(selectionRegionBuilders);

    // animals factory
    List<Builder<Animal>> selectionAnimalBuilders = new ArrayList<>();
    selectionAnimalBuilders.add(new SheepBuilder(selectionStrategyFactory));
    selectionAnimalBuilders.add(new WolfBuilder(selectionStrategyFactory));
    selecionAnimalFactory = new BuilderBasedFactory<Animal>(selectionAnimalBuilders);
  }

  private static JSONObject loadJSONFile(InputStream in) {
    return new JSONObject(new JSONTokener(in));
  }

  // Start
  private static void start_batch_mode() throws Exception {
    InputStream is = new FileInputStream(new File(inFile));
    JSONObject jo = loadJSONFile(is);
    is.close();

    int cols = jo.getInt("cols");
    int rows = jo.getInt("rows");
    int width = jo.getInt("width");
    int height = jo.getInt("height");
    sim = new Simulator(cols, rows, width, height, selecionAnimalFactory, selectionRegionFactory);
    controller = new Controller(sim);
    controller.loadData(jo);

    OutputStream os = outFile == null ? System.out : new FileOutputStream(new File(outFile));
    PopulationStats ps = null;
    if (populationLimit != null) {
      ps = new PopulationStats(populationLimit);
      controller.addObserver(ps);
    }

    controller.run(time, deltaTime, simpleViewer, os);

    if(ps!= null) ps.printResults();
    if (os != System.out) {
      os.close();
    }

  }

  private static void start_GUI_mode() throws Exception {
    int cols = 20;
    int rows = 15;
    int width = 800;
    int height = 600;
    sim = new Simulator(cols, rows, width, height, selecionAnimalFactory, selectionRegionFactory);
    controller = new Controller(sim);
    if (inFile != null) {
      InputStream is = new FileInputStream(new File(inFile));
      JSONObject jo = loadJSONFile(is);
      is.close();
      cols = jo.getInt("cols");
      rows = jo.getInt("rows");
      width = jo.getInt("width");
      height = jo.getInt("height");
      sim = new Simulator(cols, rows, width, height, selecionAnimalFactory, selectionRegionFactory);
      controller = new Controller(sim);
      controller.loadData(jo);
    }
    SwingUtilities.invokeAndWait(() -> new MainWindow(controller));
  }

  private static void start(String[] args) throws Exception {
    initFactories();
    parseArgs(args);
    switch (mode) {
      case BATCH:
        start_batch_mode();
        break;
      case GUI:
        start_GUI_mode();
        break;
    }
  }

  public static void main(String[] args) {
    Utils.RAND.setSeed(2147483647l);
    try {
      start(args);
    } catch (Exception e) {
      System.err.println("Something went wrong ...");
      System.err.println();
      e.printStackTrace();
    }
  }

}
