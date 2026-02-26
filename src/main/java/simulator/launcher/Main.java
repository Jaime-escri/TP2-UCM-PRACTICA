package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;



public class Main {

  private static Factory<SelectionStrategy> selectionStrategyFactory;
  private static Factory<Region> selectionRegionFactory;
  private static Factory<Animal> selecionAnimalFactory;

  private static Simulator sim;
  private static Controller controller;

  private final static Double DEFAULT_DELTA_TIME = 0.03; 
  private final static String outFile = null;
  private final static boolean simpleViewer= false;
  private static Double deltaTime = 0.1;

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

  private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
    String dt = line.getOptionValue("dt", "0.1");
    try {
        deltaTime = Double.parseDouble(dt);
    } catch (Exception e) {
        throw new ParseException("Invalid value for delta-time: " + dt);
    }
  }

    private static void parseOutFileOption(CommandLine line) {
      outFile = line.getOptionValue("o");
    }

    private static void parseSimpleViewerOption(CommandLine line) {
      simpleViewer = line.hasOption("sv");
    }

  // default values for some parameters
  //
  private final static Double DEFAULT_TIME = 10.0; // in seconds

  // some attributes to stores values corresponding to command-line parameters
  //
  private static Double time = null;
  private static String inFile = null;
  private static ExecMode mode = ExecMode.BATCH;

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
      parseInFileOption(line);
      parseTimeOption(line);
      parseDeltaTimeOption(line);
      parseOutFileOption(line);
      parseSimpleViewerOption(line);

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

  private static Options buildOptions() {
    Options cmdLineOptions = new Options();

    //dt
    cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").desc("A real number representing the time step. Default value: 0.1.").build());

    //output file -o
    cmdLineOptions.addOption(Option.builder("o").longOpt("output").desc("A file where output is written.").build());

    //Simple viewer -sv
    cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("If present, show the graphical viewer.").build());

    // help
    cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

    // input file
    cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

    // steps
    cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
      .desc("An real number representing the total simulation time in seconds. Default value: "
        + DEFAULT_TIME + ".")
      .build());

    return cmdLineOptions;
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

  private static void initFactories() {
    // initialize the strategies factory
    List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();
    selectionStrategyBuilders.add(new SelectFirstBuilder());
    selectionStrategyBuilders.add(new SelectClosestBuilder());
    selectionStrategyFactory = new BuilderBasedFactory<SelectionStrategy>(selectionStrategyBuilders);

    //initialize the regions factory
    List<Builder<Region>> selectionRegionBuilders = new ArrayList<>();
    selectionRegionBuilders.add(new DefaultRegionBuilder());
    selectionRegionBuilders.add(new DynamicSupplyRegionBuilder());
    selectionRegionFactory= new BuilderBasedFactory<Region>(selectionRegionBuilders);

    //initialize the animals factory
    List<Builder<Animal>> selectionAnimalBuilders = new ArrayList<>();
    selectionAnimalBuilders.add(new SheepBuilder(selectionStrategyFactory));
    selectionAnimalBuilders.add(new WolfBuilder(selectionStrategyFactory));
    selecionAnimalFactory = new BuilderBasedFactory<Animal>(selectionAnimalBuilders);
  }


  private static JSONObject loadJSONFile(InputStream in) {
    return new JSONObject(new JSONTokener(in));
  }


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
    controller.run(time, DEFAULT_DELTA_TIME, simpleViewer, os);

  }

  private static void start_GUI_mode() throws Exception {
    throw new UnsupportedOperationException("GUI mode is not ready yet ...");
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
