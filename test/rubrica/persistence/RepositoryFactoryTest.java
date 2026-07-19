package rubrica.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Properties;
import org.junit.jupiter.api.Test;
import rubrica.config.AppConfig;

class RepositoryFactoryTest {

    private AppConfig configConTipo(String tipo) {
        Properties props = new Properties();
        if (tipo != null) {
            props.setProperty("persistence.type", tipo);
        }
        return new AppConfig(props);
    }

    @Test
    void defaultCreaRepositorySuFile() {
        RubricaRepository repo = RepositoryFactory.create(configConTipo(null));
        assertTrue(repo instanceof FileRubricaRepository);
    }

    @Test
    void tipoFileCreaRepositorySuFile() {
        RubricaRepository repo = RepositoryFactory.create(configConTipo("file"));
        assertTrue(repo instanceof FileRubricaRepository);
    }

    @Test
    void tipoMysqlNonAncoraSupportato() {
        assertThrows(UnsupportedOperationException.class,
                () -> RepositoryFactory.create(configConTipo("mysql")));
    }
}
