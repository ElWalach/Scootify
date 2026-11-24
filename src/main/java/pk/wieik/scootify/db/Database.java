package pk.wieik.scootify.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public final class Database {
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final Path DB_FILE = Path.of(
            System.getProperty("user.home"),
            "data", "scootify", "scootify.db"
    ).toAbsolutePath().normalize();

    private Database() {}

    public static Path getDatabaseFile() {
        return DB_FILE;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("No JDBC driver");
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
        } catch (SQLException ex) {
            connection.close();
            throw ex;
        }
        return connection;
    }

    public static void initialize() throws IOException, SQLException {
        Files.createDirectories(DB_FILE.getParent());

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // uprawnienia: 0 - klient, 1 - serwisant, 2 - administrator
            statement.execute("""
                CREATE TABLE IF NOT EXISTS uzytkownicy (
                    id INTEGER PRIMARY KEY,
                    login TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    haslo TEXT NOT NULL,
                    uprawnienia INTEGER NOT NULL DEFAULT 0
                        CHECK (uprawnienia IN (0, 1, 2)),
                    saldo REAL NOT NULL DEFAULT 0,
                    zablokowany INTEGER NOT NULL DEFAULT 0
                        CHECK (zablokowany IN (0, 1))
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS modele (
                    id INTEGER PRIMARY KEY,
                    producent TEXT NOT NULL,
                    nazwa TEXT NOT NULL,
                    max_predkosc INTEGER NOT NULL CHECK (max_predkosc > 0),
                    zasieg_km INTEGER NOT NULL CHECK (zasieg_km > 0),
                    UNIQUE (producent, nazwa)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS strefy (
                    id INTEGER PRIMARY KEY,
                    nazwa TEXT NOT NULL UNIQUE
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS hulajnogi (
                    id INTEGER PRIMARY KEY,
                    kod_qr TEXT NOT NULL UNIQUE,
                    model_id INTEGER NOT NULL,
                    strefa_id INTEGER NOT NULL,
                    bateria INTEGER NOT NULL CHECK (bateria BETWEEN 0 AND 100),
                    status TEXT NOT NULL DEFAULT 'dostepna'
                        CHECK (status IN ('dostepna', 'w_uzyciu', 'w_serwisie', 'do_naladowania')),
                    szerokosc REAL NOT NULL,
                    dlugosc REAL NOT NULL,
                    FOREIGN KEY (model_id) REFERENCES modele(id)
                        ON UPDATE CASCADE ON DELETE RESTRICT,
                    FOREIGN KEY (strefa_id) REFERENCES strefy(id)
                        ON UPDATE CASCADE ON DELETE RESTRICT
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS przejazdy (
                    id INTEGER PRIMARY KEY,
                    uzytkownik_id INTEGER NOT NULL,
                    hulajnoga_id INTEGER NOT NULL,
                    rozpoczecie TEXT NOT NULL,
                    zakonczenie TEXT,
                    koszt REAL NOT NULL DEFAULT 0 CHECK (koszt >= 0),
                    FOREIGN KEY (uzytkownik_id) REFERENCES uzytkownicy(id)
                        ON UPDATE CASCADE ON DELETE RESTRICT,
                    FOREIGN KEY (hulajnoga_id) REFERENCES hulajnogi(id)
                        ON UPDATE CASCADE ON DELETE RESTRICT
                )
                """);

            statement.execute("CREATE INDEX IF NOT EXISTS idx_hulajnogi_model ON hulajnogi(model_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_hulajnogi_strefa ON hulajnogi(strefa_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_przejazdy_uzytkownik ON przejazdy(uzytkownik_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_przejazdy_hulajnoga ON przejazdy(hulajnoga_id)");
        }
    }
}