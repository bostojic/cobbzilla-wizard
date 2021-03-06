package org.cobbzilla.wizard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.cobbzilla.util.string.StringUtil.snakeCaseToCamelCase;

@AllArgsConstructor @ToString
public class SqlViewField {

    @Getter @Setter private Class<? extends Identifiable> type;
    @Getter @Setter private String name;
    @Getter @Setter private String property;
    @Getter @Setter private boolean encrypted;

    @Getter @Setter private SqlViewFieldSetter setter;
    public boolean hasSetter () { return setter != null; }

    public SqlViewField(String name)                    { this(null, name, snakeCaseToCamelCase(name), false, null, null); }
    public SqlViewField(String name, String property)   { this(null, name, property, false, null, null); }
    public SqlViewField(String name, SqlViewFieldSetter setter)   { this(null, name, snakeCaseToCamelCase(name), false, setter, null); }
    public SqlViewField(String name, boolean encrypted) { this(null, name, snakeCaseToCamelCase(name), encrypted, null, null); }
    public SqlViewField(String name, String property, boolean encrypted) { this(null, name, property, encrypted, null, null); }

    public SqlViewField(Class<? extends Identifiable> type, String name, String property) {
        this(type, name, property, false, null, null);
    }
    public SqlViewField(Class<? extends Identifiable> type, String name, String property, String entity) {
        this(type, name, property, false, null, entity);
    }
    public SqlViewField(Class<? extends Identifiable> type, String name, boolean encrypted) {
        this(type, name, snakeCaseToCamelCase(name), encrypted, null, null);
    }
    public SqlViewField(Class<? extends Identifiable> type, String name, String property, boolean encrypted) {
        this(type, name, property, encrypted, null, null);
    }
    public SqlViewField(Class<? extends Identifiable> type, String name, String property, boolean encrypted, String entity) {
        this(type, name, property, encrypted, null, entity);
    }

    private String entity;

    public String getEntity () {
        if (entity != null) return entity;
        if (type == null) return null;
        final int dotPos = property.indexOf('.');
        return dotPos == -1 ? null : property.substring(0, dotPos);
    }

    public boolean hasEntity () { return getEntity() != null; }

    public String getEntityProperty () {
        if (type == null) return property;
        final int dotPos = property.indexOf('.');
        return dotPos == -1 ? property : property.substring(dotPos+1);
    }

}
