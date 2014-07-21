package arquitetura.helpers;


import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * Configure log level. If you dont wanna log anything on "production" use: {@code setLogLevel(Level.DEBUG}
 * 
 * @author elf
 *
 */
public class LogConfiguration {
    
    public static void setLogLevel(Level level){
	LogManager.getRootLogger().setLevel(level);
    }

}
