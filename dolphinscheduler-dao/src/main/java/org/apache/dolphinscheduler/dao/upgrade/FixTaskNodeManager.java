package org.apache.dolphinscheduler.dao.upgrade;

import org.apache.dolphinscheduler.spi.enums.DbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Component
@Profile("fix-cli")
public class FixTaskNodeManager {

    private static final Logger logger = LoggerFactory.getLogger(FixTaskNodeManager.class);

    private final UpgradeDao upgradeDao;

    public FixTaskNodeManager(DataSource dataSource, List<UpgradeDao> daos) throws Exception {
        final DbType type = getCurrentDbType(dataSource);
        upgradeDao = daos.stream()
                .filter(it -> it.getDbType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Cannot find UpgradeDao implementation for db type: " + type
                ));
    }

    private DbType getCurrentDbType(DataSource dataSource) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            String name = conn.getMetaData().getDatabaseProductName().toUpperCase();
            return DbType.valueOf(name);
        }
    }

    public void fixDependentTaskCode() {
        logger.info("fix dependent task code start");
        upgradeDao.upgradeDepTaskCode();
        logger.info("fix dependent task code end");
    }
}
