package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import DataBase.DBConnection;
import java.io.File;

public class Main extends Application {
    private static Logger logger;

    static {
        // Setup Log4j programmatically if no config file is present
        String logDir = DBConnection.getDatabaseDirectory();
        String logFile = logDir + File.separator + "QuickBillPro.log";
        File configFile = new File("log4j2.xml");
        if (!configFile.exists()) {
            ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
            builder.setStatusLevel(org.apache.logging.log4j.Level.ERROR);
            builder.setConfigurationName("DefaultLogger");
            LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n");
            AppenderComponentBuilder appenderBuilder = builder.newAppender("logfile", "File")
                .addAttribute("fileName", logFile)
                .add(layoutBuilder);
            builder.add(appenderBuilder);
            builder.add(builder.newRootLogger(org.apache.logging.log4j.Level.INFO)
                .add(builder.newAppenderRef("logfile")));
            Configurator.initialize(builder.build());
        }
        logger = LogManager.getLogger(Main.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Application started");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
