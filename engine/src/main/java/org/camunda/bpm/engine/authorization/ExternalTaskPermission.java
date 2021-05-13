package org.camunda.bpm.engine.authorization;

public enum ExternalTaskPermission implements Permission {
    /** The none permission means 'no action', 'doing nothing'.
     * It does not mean that no permissions are granted. */
    NONE("NONE", 0),

    /**
     * Indicates that  all interactions are permitted.
     * If ALL is revoked it means that the user is not permitted
     * to do everything, which means that at least one permission
     * is revoked. This does not implicate that all individual
     * permissions are revoked.
     *
     * Example: If the UPDATE permission is revoke also the ALL
     * permission is revoked, because the user is not authorized
     * to execute all actions anymore.
     */
    ALL("ALL", Integer.MAX_VALUE),

    /** Indicates that READ interactions are permitted. */
    READ("READ", 2),

    /** Indicates that UPDATE interactions are permitted. */
    UPDATE("UPDATE", 4),

    /** Indicates that READ interactions are permitted. */
    READ_INSTANCE("READ_INSTANCE", 8),

    /** Indicates that READ interactions are permitted. */
    UPDATE_INSTANCE("UPDATE_INSTANCE", 16),

    /** Indicates that UPDATE_INSTANCE_VARIABLE interactions are permitted. */
    UPDATE_INSTANCE_VARIABLE("UPDATE_INSTANCE_VARIABLE", 32),

     /** Indicates that READ_INSTANCE_VARIABLE interactions are permitted. */
    READ_INSTANCE_VARIABLE("READ_INSTANCE_VARIABLE", 64);


    private static final Resource[] RESOURCES = new Resource[] { Resources.EXTERNAL_TASK };

    private String name;
    private int id;

    ExternalTaskPermission(String name, int id) {
        this.name = name;
        this.id = id;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return id;
    }

    @Override
    public Resource[] getTypes() {
        return RESOURCES;
    }
}
