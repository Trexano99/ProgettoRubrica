package rubrica.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import org.junit.jupiter.api.Test;

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
        props.setProperty("persistence.mysql.host", "10.0.0.5");
        props.setProperty("persistence.mysql.port", "3307");
        props.setProperty("persistence.mysql.database", "contatti");
        props.setProperty("persistence.mysql.user", "app");
        props.setProperty("persistence.mysql.password", "segreta");

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
                conProprieta("persistence.mysql.port", "tremila").getMysql().getPort());
    }
}
