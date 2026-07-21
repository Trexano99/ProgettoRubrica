package rubrica.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ImpostazioniMysqlTest {

    @Test
    void jdbcUrlComponeHostPortEDatabase() {
        ImpostazioniMysql m = new ImpostazioniMysql("db.example.com", 3307, "rubrica", "app", "segreta");
        assertEquals(
                "jdbc:mysql://db.example.com:3307/rubrica?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                m.jdbcUrl());
    }

    @Test
    void iValoriSonoEsposiDaiGetter() {
        ImpostazioniMysql m = new ImpostazioniMysql("localhost", 3306, "rubrica", "root", "pw");
        assertEquals("localhost", m.getHost());
        assertEquals(3306, m.getPort());
        assertEquals("rubrica", m.getDatabase());
        assertEquals("root", m.getUser());
        assertEquals("pw", m.getPassword());
    }

    @Test
    void toStringNonEsponeLaPassword() {
        ImpostazioniMysql m = new ImpostazioniMysql("localhost", 3306, "rubrica", "root", "top-secret");
        assertFalse(m.toString().contains("top-secret"));
    }
}
