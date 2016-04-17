package alertmed;

import alertmed.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Main application directory {}", System.getProperty("user.dir"));
        System.setProperty("user.timezone", "UTC");

        String homeDir = ensureHomeIsSet();
        System.setProperty("log4j.configurationFile", log4jConfigPath(homeDir));
        log.info("Working directory {}", System.getProperty("user.dir"));
        log.info("ALERTMED_HOME is {}", homeDir);

        // bootstrap application with Spring.
        String configPath = configPath(homeDir);
        log.info("Using config file {}", configPath);
        System.setProperty("spring.config.name", configPath);

        JacksonConfig.configure();

        CommandLinePropertySource clps = new SimpleCommandLinePropertySource(args);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(clps);
        context.registerShutdownHook();
        context.register(Config.class);
        context.refresh();


    }

    private static String ensureHomeIsSet() {
        String homeDirectory = System.getenv("ALERTMED_HOME");

        if (homeDirectory == null) {
            System.err.println("Missing ALERTMED_HOME environment variable.\n" +
                    "Please make sure you set ALERTMED_HOME to where you installed jbakery");
            System.exit(-1);
        }
        return homeDirectory;
    }

    private static String configPath(String homeDirectory) {
        return String.format("file:%s/conf/application.properties", homeDirectory);
    }

    private static String log4jConfigPath(String homeDirectory) {
        return String.format("%s/conf/log4j2.xml", homeDirectory);
    }
}
