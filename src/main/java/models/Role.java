package models;

public enum Role {
    ADMINISTRADOR(1),
    MARKETING(2),
    GESTION_SERVICIOS(3),
    EMPLEADO(4),
    CLIENTE(5);

    private final int id;

    Role(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}