package org.cobbzilla.wizard.dao.shard.cache;

import org.cobbzilla.wizard.dao.shard.AbstractShardedDAO;
import org.cobbzilla.wizard.dao.shard.SingleShardDAO;
import org.cobbzilla.wizard.dao.shard.task.ShardFindFirstByFieldTask;
import org.cobbzilla.wizard.model.shard.Shardable;

public class ShardableShardCacheableUniqueFieldFinder<E extends Shardable, D extends SingleShardDAO<E>> extends ShardCacheableFinder<E, D> {

    public ShardableShardCacheableUniqueFieldFinder(AbstractShardedDAO<E, D> shardedDAO, long timeout) { super(shardedDAO, timeout); }

    @Override
    public E find(Object... args) {
        final String field = args[0].toString();
        final String value = (String) args[1];

        if (shardedDAO.getHashOn().equals(field)) return shardedDAO.getDAO(value).get(value);

        // have to search all shards for it
        return shardedDAO.queryShardsUnique(new ShardFindFirstByFieldTask.Factory(field, value), "findByUniqueField");
    }
}