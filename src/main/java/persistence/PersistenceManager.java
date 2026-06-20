package persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Owns the application-wide {@link EntityManagerFactory} for the "uno"
 * persistence unit.
 *
 * <p>Connection settings come from {@code persistence.xml} but can be
 * overridden at runtime through environment variables (or system properties),
 * so database credentials never need to live in source control:
 * <ul>
 *   <li>{@code UNO_DB_URL}</li>
 *   <li>{@code UNO_DB_USER}</li>
 *   <li>{@code UNO_DB_PASSWORD}</li>
 * </ul>
 */
public final class PersistenceManager {

    public static final String PERSISTENCE_UNIT = "uno";

    private static volatile EntityManagerFactory emf;

    private PersistenceManager() {
    }

    /** Lazily creates (once) and returns the shared factory. */
    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory local = emf;
        if (local == null) {
            synchronized (PersistenceManager.class) {
                local = emf;
                if (local == null) {
                    emf = local = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, envOverrides());
                }
            }
        }
        return local;
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

    static Map<String, String> envOverrides() {
        Map<String, String> overrides = new HashMap<>();
        putIfPresent(overrides, "jakarta.persistence.jdbc.url", config("UNO_DB_URL"));
        putIfPresent(overrides, "jakarta.persistence.jdbc.user", config("UNO_DB_USER"));
        putIfPresent(overrides, "jakarta.persistence.jdbc.password", config("UNO_DB_PASSWORD"));
        return overrides;
    }

    private static void putIfPresent(Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) {
            map.put(key, value);
        }
    }

    /** Prefers a system property, falling back to an environment variable. */
    private static String config(String name) {
        String sys = System.getProperty(name);
        return sys != null ? sys : System.getenv(name);
    }
}
