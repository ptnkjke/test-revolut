package revolut.test.configuration;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import spark.utils.IOUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBInit {
    private DataSource dataSource;

    @Inject
    public DBInit(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();


            QueryRunner queryRunner = new QueryRunner();

            String sql = IOUtils.toString(this.getClass().getResourceAsStream("/init-db.sql"));


            queryRunner.update(connection, sql);
        } catch (SQLException e) {
            throw new RuntimeException("sql error", e);
        } catch (IOException e) {
            throw new RuntimeException("ioexception", e);
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }
}
