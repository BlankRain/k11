package mapindex;

public class Dog implements ByteAbleObject {
    String id;
    String name;

    @Override
    public String toString() {
        return "Dog{"
                +
                "id='" + id + '\''
                +
                ", name='" + name + '\''
                +
                '}';
    }

    @Override
    public byte[] bytes() {
        return toString().getBytes();
    }
}
