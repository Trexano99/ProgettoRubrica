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
}
