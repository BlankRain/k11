package mapindex;

public class Person {
    String id;
    String name;
    String dogName;

    @Override
    public String toString() {
        return "Person{"
                +
                "id='" + id + '\''
                +
                ", name='" + name + '\''
                +
                ", dogName='" + dogName + '\''
                +
                '}';
    }
}
