package wolox.bootstrap.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import wolox.bootstrap.configuration.AppConfig;
import wolox.bootstrap.models.Log;
import wolox.bootstrap.repositories.LogRepository;

@Service
@ComponentScan
@ContextConfiguration(classes = {AppConfig.class})
public class InformationLoggingService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Value("${log.file.output}")
    private String fileDestination;

    @Autowired
    private LogRepository logRepository;

    public String getFileDestination() {
        return fileDestination;
    }

    public void setFileDestination(String fileDestination) {
        this.fileDestination = fileDestination;
    }

    private void clearHandlers() throws IOException {
        Handler[] handlers = logger.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            logger.removeHandler(handlers[i]);
        }
        logger.addHandler(new FileHandler(fileDestination, false));
        logger.addHandler(new ConsoleHandler());
    }

    public void log(String message) throws IOException {
        clearHandlers();
        logger.info(message);
    }

    public void logAndStoreInDatabase(String message) throws IOException {
        Log log = new Log();
        log.setDate(LocalDate.now());
        log.setMessage(message);
        logRepository.save(log);
        log(message);
    }

    public Iterable findOldLogsByMessageContaining(String message) {
        return logRepository.findByMessageContaining(message);
    }

    public Iterable findOldLogsByDateBetween(LocalDate startDate, LocalDate finishDate) {
        return logRepository.findByDateBetween(startDate, finishDate);
    }

}
