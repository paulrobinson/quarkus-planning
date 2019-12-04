package eu.paulrobinson.quarkusplanning;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AppLifecycleBean {

    @Inject ReportGenerator reportGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger("ListenerBean");

    void onStart(@Observes StartupEvent ev) {
        try {
            LOGGER.info("Pre-loading Epics");
            reportGenerator.loadData();
            LOGGER.info("Pre-loading Epics Complete");
        } catch (QuarkusPlanningException e) {
            LOGGER.error("Error pre-loading Epics", e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application is stopping...");
    }

}
