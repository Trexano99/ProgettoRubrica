package rubrica.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AppConfigTest {

    private AppConfig conProprieta(String chiave, String valore) {
        Properties props = new Properties();
        props.setProperty(chiave, valore);
        return new AppConfig(props);
    }

    @Test
    void defaultSenzaProprietaEFile() {
        AppConfig config = new AppConfig(new Properties());
        assertEquals(PersistenceType.FILE, config.getPersistenceType());
    }

    @Test
    void tipoFileEsplicito() {
        assertEquals(PersistenceType.FILE,
                conProprieta("persistence.type", "file").getPersistenceType());
    }

    @Test
    void tipoDirectory() {
        assertEquals(PersistenceType.DIRECTORY,
                conProprieta("persistence.type", "directory").getPersistenceType());
    }

    @Test
    void tipoMysql() {
        assertEquals(PersistenceType.MYSQL,
                conProprieta("persistence.type", "mysql").getPersistenceType());
    }

    @Test
    void tipoInsensibileAlMaiuscoloEAgliSpazi() {
        assertEquals(PersistenceType.MYSQL,
                conProprieta("persistence.type", "  MySQL  ").getPersistenceType());
    }

    @Test
    void valoreSconosciutoRicadeSuFile() {
        assertEquals(PersistenceType.FILE,
                conProprieta("persistence.type", "cassandra").getPersistenceType());
    }

    @Test
    void mysqlSenzaProprietaUsaIDefault() {
        ImpostazioniMysql m = new AppConfig(new Properties()).getMysql();
        assertEquals("localhost", m.getHost());
        assertEquals(3306, m.getPort());
        assertEquals("rubrica", m.getDatabase());
        assertEquals("root", m.getUser());
        assertEquals("", m.getPassword());
    }

    @Test
    void mysqlLeggeLeProprietaConfigurate() {
        Properties props = new Properties();
        props.setProperty("db.host", "10.0.0.5");
        props.setProperty("db.port", "3307");
        props.setProperty("db.database", "contatti");
        props.setProperty("db.user", "app");
        props.setProperty("db.password", "segreta");

        ImpostazioniMysql m = new AppConfig(props).getMysql();
        assertEquals("10.0.0.5", m.getHost());
        assertEquals(3307, m.getPort());
        assertEquals("contatti", m.getDatabase());
        assertEquals("app", m.getUser());
        assertEquals("segreta", m.getPassword());
    }

    @Test
    void portaNonNumericaRicadeSulDefault() {
        assertEquals(3306,
                conProprieta("db.port", "tremila").getMysql().getPort());
    }

    @Test
    void leCredenzialiSovrascrivonoLaConfigurazione(@TempDir Path dir) throws IOException {
        Path config = dir.resolve("rubrica.properties");
        Files.writeString(config, "persistence.type=mysql\ndb.user=daConfig\n");
        Path credenziali = dir.resolve("credenziali_database.properties");
        Files.writeString(credenziali, "db.user=daCredenziali\ndb.password=segreta\n");

        AppConfig config2 = AppConfig.load(config.toFile(), credenziali.toFile());

        assertEquals(PersistenceType.MYSQL, config2.getPersistenceType());
        assertEquals("daCredenziali", config2.getMysql().getUser());
        assertEquals("segreta", config2.getMysql().getPassword());
    }

    @Test
    void fileAssentiRicadonoSuiDefault(@TempDir Path dir) {
        AppConfig config = AppConfig.load(
                dir.resolve("manca.properties").toFile(),
                dir.resolve("manca-anche.properties").toFile());

        assertEquals(PersistenceType.FILE, config.getPersistenceType());
        assertEquals("localhost", config.getMysql().getHost());
    }
}
