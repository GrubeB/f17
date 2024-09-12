package pl.app.battle.adapter.out;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import pl.app.battle.application.domain.tower_attack.TowerAttack;
import pl.app.battle.application.port.out.TowerAttackDomainRepository;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Component
class InMemoryTowerAttackDomainRepositoryImpl implements TowerAttackDomainRepository {

    private final ConcurrentHashMap<ObjectId, SoftReference<TowerAttack>> memory;
    private final DelayQueue<DelayedObject<TowerAttack>> cleaningUpQueue;
    private Thread cleanerThread;

    public InMemoryTowerAttackDomainRepositoryImpl() {
        memory = new ConcurrentHashMap<>();
        cleaningUpQueue = new DelayQueue<>();
        initCleanerThread();
    }

    private void initCleanerThread() {
        this.cleanerThread = new Thread(() -> {
            DelayedObject<TowerAttack> delayedCacheObject;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    delayedCacheObject = this.cleaningUpQueue.take();
                    this.memory.remove(new ObjectId(delayedCacheObject.getKey()), delayedCacheObject.getReference());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        this.cleanerThread.setDaemon(true);
        this.cleanerThread.start();
    }

    @Override
    public TowerAttack getTowerAttack(ObjectId id) {
        return Optional.ofNullable(memory.get(id)).map(SoftReference::get).orElse(null);
    }

    @Override
    public TowerAttack save(TowerAttack domain) {
        long expiryTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
        SoftReference<TowerAttack> reference = new SoftReference<>(domain);
        memory.put(domain.getInfo().getTowerAttackId(), reference);
        cleaningUpQueue.put(new DelayedObject<>(domain.getInfo().getTowerAttackId().toHexString(), reference, expiryTime));
        return domain;
    }

    @Override
    public void remove(ObjectId id) {
        memory.remove(id);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    private static class DelayedObject<T> implements Delayed {

        private final String key;
        private final SoftReference<T> reference;
        private final long expiryTime;

        @Override
        @SuppressWarnings("unchecked")
        public int compareTo(@NotNull Delayed o) {
            return Long.compare(expiryTime, ((DelayedObject<T>) o).expiryTime);
        }

        @Override

        public long getDelay(TimeUnit unit) {
            return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
    }
}
