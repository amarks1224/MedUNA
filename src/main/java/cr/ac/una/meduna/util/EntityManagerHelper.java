package cr.ac.una.meduna.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Helper para gestionar el EntityManager de JPA
 * @author ccarranza
 */
public class EntityManagerHelper {
    
    private static EntityManagerHelper INSTANCE = null;
    private static EntityManagerFactory emf;
    
    static {
        try {
            emf = Persistence.createEntityManagerFactory("MedUNA");
        } catch (Exception ex) {
            System.err.println("Error al inicializar EntityManagerFactory:");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    private EntityManagerHelper() {}
    
    /**
     * Obtiene la instancia única de EntityManagerHelper (Singleton)
     * @return Instancia de EntityManagerHelper
     */
    public static EntityManagerHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityManagerHelper();
        }
        return INSTANCE;
    }
    
    /**
     * Obtiene un nuevo EntityManager
     * @return EntityManager
     */
    public EntityManager getManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Cierra el EntityManagerFactory
     */
    public static void closeFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}