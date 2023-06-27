package org.apache.dolphinscheduler.dao.upgrade.shell;

import org.apache.dolphinscheduler.dao.upgrade.FixTaskNodeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@ComponentScan(value = "org.apache.dolphinscheduler.dao")
@EnableAutoConfiguration(exclude = {QuartzAutoConfiguration.class})
public class FixTaskNodeScheduler {

    public static void main(String[] args) {
        new SpringApplicationBuilder(FixTaskNodeScheduler.class)
                .profiles("fix", "shell-cli", "fix-cli")
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Component
    @Profile("fix")
    static class FixRunner implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(FixRunner.class);

        private final FixTaskNodeManager fixTaskNodeManager;

        FixRunner(FixTaskNodeManager fixTaskNodeManager) {
            this.fixTaskNodeManager = fixTaskNodeManager;
        }

        @Override
        public void run(String... args) throws Exception {
            fixTaskNodeManager.fixDependentTaskCode();
            logger.info("fix dependent task success");
        }
    }
}
