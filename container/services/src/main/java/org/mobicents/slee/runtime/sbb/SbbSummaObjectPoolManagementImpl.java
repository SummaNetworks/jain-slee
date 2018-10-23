package org.mobicents.slee.runtime.sbb;

import javax.slee.SbbID;
import javax.slee.ServiceID;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;
import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.container.component.sbb.SbbComponent;
import org.mobicents.slee.container.transaction.SleeTransactionManager;
import org.mobicents.slee.container.transaction.TransactionalAction;
import org.vibur.objectpool.ConcurrentPool;
import org.vibur.objectpool.PoolService;
import org.vibur.objectpool.util.ConcurrentLinkedQueueCollection;

/**
 * The type Sbb summa object pool management.
 *
 * @author ajimenez, created on 28/09/18.
 */
public class SbbSummaObjectPoolManagementImpl {


    private final static Logger logger = Logger
            .getLogger(SbbObjectPoolManagementImpl.class);

    private final ConcurrentHashMap<SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey, SbbSummaObjectPoolImpl> pools;
    private final SleeContainer sleeContainer;


    /**
     * Instantiates a new Sbb summa object pool management.
     *
     * @param sleeContainer the slee container
     */
    public SbbSummaObjectPoolManagementImpl(SleeContainer sleeContainer) {
        this.sleeContainer = sleeContainer;
        // create pools map
        pools = new ConcurrentHashMap<SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey, SbbSummaObjectPoolImpl>();
    }


    /**
     * Retrieves the object pool for the specified sbb and service.
     *
     * @param serviceID the service id
     * @param sbbID     the sbb id
     * @return object pool
     */
    public SbbSummaObjectPoolImpl getObjectPool(ServiceID serviceID, SbbID sbbID) {
        return pools.get(new SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey(serviceID,sbbID));
    }

    /**
     * Creates an object pool for the specified service and sbb. If a
     * transaction manager is used then, and if the tx rollbacks, the pool will
     * be removed.
     *
     * @param serviceID              the service id
     * @param sbbComponent           the sbb component
     * @param sleeTransactionManager the slee transaction manager
     */
    public void createObjectPool(final ServiceID serviceID, final SbbComponent sbbComponent,
                                 final SleeTransactionManager sleeTransactionManager) {

        if (logger.isTraceEnabled()) {
            logger.trace("Creating Pool for  " + serviceID +" and "+ sbbComponent);
        }

        createObjectPool(serviceID,sbbComponent);

        if (sleeTransactionManager != null && sleeTransactionManager.getTransactionContext() != null) {
            // add a rollback action to remove sbb object pool
            TransactionalAction action = new TransactionalAction() {
                public void execute() {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Due to tx rollback, removing pool for " + serviceID +" and "+ sbbComponent);
                    }
                    try {
                        removeObjectPool(serviceID,sbbComponent.getSbbID());
                    } catch (Throwable e) {
                        logger.error("Failed to remove " + serviceID +" and "+ sbbComponent + " object pool", e);
                    }
                }
            };
            sleeTransactionManager.getTransactionContext().getAfterRollbackActions().add(action);
        }
    }

    /**
     *
     * @param serviceID
     * @param sbbComponent
     */
    private void createObjectPool(final ServiceID serviceID, final SbbComponent sbbComponent) {
        // create the pool for the given SbbID

        PoolService cp = new ConcurrentPool
                (new ConcurrentLinkedQueueCollection(),
                        new SbbSummaObjectPoolFactory(serviceID, sbbComponent), 50, 1000, true);



        final SbbSummaObjectPoolImpl oldObjectPool = pools.put(
                new SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey(serviceID,sbbComponent.getSbbID()),
                new SbbSummaObjectPoolImpl(sbbComponent,serviceID,cp));
        if (oldObjectPool != null) {
            // there was an old pool, close it
            try {
                oldObjectPool.close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to close old pool for " + serviceID + "and " + sbbComponent);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Created Pool for " + serviceID + "and " + sbbComponent);
        }
    }

    /**
     * Removes the object pool for the sbb with the specified component and the specified service. If a
     * transaction manager is used then, and if the tx rollbacks, the pool will
     * be restored.
     *
     * @param serviceID              the service id
     * @param sbbComponent           the sbb component
     * @param sleeTransactionManager the slee transaction manager
     * @throws Exception
     */
    public void removeObjectPool(final ServiceID serviceID, final SbbComponent sbbComponent,
                                 final SleeTransactionManager sleeTransactionManager) {

        if (logger.isTraceEnabled()) {
            logger.trace("Removing Pool for " + serviceID + "and " + sbbComponent);
        }

        removeObjectPool(serviceID,sbbComponent.getSbbID());

        if (sleeTransactionManager != null) {
            // restore object pool if tx rollbacks
            TransactionalAction action = new TransactionalAction() {
                public void execute() {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("Due to tx rollback, restoring pool for " + serviceID + "and " + sbbComponent);
                    }
                    createObjectPool(serviceID,sbbComponent);
                }
            };
            sleeTransactionManager.getTransactionContext().getAfterRollbackActions().add(action);
        }
    }

    /**
     * Removes the pool for the specified ids
     *
     * @param serviceID
     * @param sbbID
     * @throws Exception
     */
    private void removeObjectPool(final ServiceID serviceID, final SbbID sbbID) {
        SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey key = new SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey(serviceID,sbbID);
        final SbbSummaObjectPoolImpl objectPool = pools.remove(key);
        if (objectPool != null) {
            try {
                objectPool.close();
            } catch (Exception e) {
                logger.error("failed to close pool",e);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Removed Pool for " + key);
        }
    }


    /**
     * Key class for map.
     */
    private static class ObjectPoolMapKey {

        private final ServiceID serviceID;
        private final SbbID sbbID;

        /**
         * Instantiates a new Object pool map key.
         *
         * @param serviceID the service id
         * @param sbbID     the sbb id
         */
        public ObjectPoolMapKey(ServiceID serviceID, SbbID sbbID) {
            this.serviceID = serviceID;
            this.sbbID = sbbID;
        }

        @Override
        public int hashCode() {
            return serviceID.hashCode()*31+sbbID.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj != null && obj.getClass() == this.getClass()) {
                SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey other = (SbbSummaObjectPoolManagementImpl.ObjectPoolMapKey) obj;
                return this.serviceID.equals(other.serviceID) && this.sbbID.equals(other.sbbID);
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return serviceID.toString() + " & "+sbbID.toString();
        }
    }

}
