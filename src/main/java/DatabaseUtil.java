import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.function.Function;

public class DatabaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseUtil.class);
    private static final String HOME_DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    public static Connection DATABASE_CONNECTION;


    static {
        try {

            DATABASE_CONNECTION = DriverManager.getConnection(HOME_DATABASE_URL, "postgres", "postgres");
            LOGGER.info("Successfully created connection to database HOME");

        } catch (Exception exc) {
            LOGGER.info("ERROR while creating connection to database {}", exc.getMessage());
        }
    }

    public static <T> T executeQueryAndReturnResponse(String query, Function<ResultSet, T> mappingFunction) {
        if (DATABASE_CONNECTION != null) {
            try (final Statement statement = DATABASE_CONNECTION.createStatement(); final ResultSet rs = statement.executeQuery(query)) {
                return mappingFunction.apply(rs);
            } catch (Exception exc) {
                LOGGER.info("ERROR while sending query[" + query + "] to database", exc);
            }
        }
        return null;
    }
    public static void executeQuery(String query) {
        if (DATABASE_CONNECTION != null) {
            try (final Statement statement = DATABASE_CONNECTION.createStatement()) {
                statement.execute(query);
            } catch (Exception exc) {
                LOGGER.info("ERROR while sending query[" + query + "] to database", exc);
            }
        }
    }
}