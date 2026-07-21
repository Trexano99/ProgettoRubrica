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
    void tipoDirectoryCreaRepositorySuCartella() {
        RubricaRepository repo = RepositoryFactory.create(configConTipo("directory"));
        assertTrue(repo instanceof DirectoryRubricaRepository);
    }

    @Test
    void tipoMysqlCreaRepositorySuDatabase() {
        RubricaRepository repo = RepositoryFactory.create(configConTipo("mysql"));
        assertTrue(repo instanceof MysqlRubricaRepository);
    }

    @Test
    void gliUtentiVannoSuFileSiaConFileSiaConDirectory() {
        assertTrue(RepositoryFactory.createUtenti(configConTipo(null))
                instanceof FileUtenteRepository);
        assertTrue(RepositoryFactory.createUtenti(configConTipo("file"))
                instanceof FileUtenteRepository);
        assertTrue(RepositoryFactory.createUtenti(configConTipo("directory"))
                instanceof FileUtenteRepository);
    }

    @Test
    void utentiSuMysqlVannoSuDatabase() {
        assertTrue(RepositoryFactory.createUtenti(configConTipo("mysql"))
                instanceof MysqlUtenteRepository);
    }
}
