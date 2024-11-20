public class KoL extends User {
    public KoL(int id, String name, String username) {
        super(id, name, username);
    }

    @Override
    public String toString() {
        return "KoL{" + "id=" + id + ", name='" + super.toString() + '}';
    }
}
