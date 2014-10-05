package org.cobbzilla.wizard.dao;

import org.cobbzilla.util.collection.MapBuilder;
import org.cobbzilla.wizard.model.UniquelyNamedEntity;
import org.cobbzilla.wizard.validation.UniqueValidatorDaoHelper;

import java.util.Map;

public abstract class UniquelyNamedEntityDAO<E extends UniquelyNamedEntity> extends AbstractUniqueCRUDDAO<E> {

    public boolean forceLowercase () { return true; }

    public E findByName (String name) { return findByUniqueField("name", forceLowercase() ? name.toLowerCase() : name); }

    protected Map<String, UniqueValidatorDaoHelper.Finder<E>> getUniqueHelpers() {
        return MapBuilder.build(new Object[][]{
            {"name", new UniqueValidatorDaoHelper.Finder<E>() { @Override public E find(Object query) { return findByName(query.toString()); } }}
        });
    }
}
