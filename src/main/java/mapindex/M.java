package mapindex;

public class M {
    public static void main(String[] args) throws Exception {

        Person xiaoming = new Person();
        xiaoming.id = "p1";
        xiaoming.name = "xiaoming";
        xiaoming.dogName = "XD";
        Dog dog = new Dog();
        dog.id = "d1";
        dog.name = "XD";

        Dog dog2 = new Dog();
        dog2.id = "d2";
        dog2.name = "XD";

        Dog dog3 = new Dog();
        dog3.id = "d3";
        dog3.name = "XD3";


        Person laowang = new Person();
        laowang.id = "p2";
        laowang.name = "laowang";
        laowang.dogName = "XD3";
        Index<Person, Dog> index = new Index<>(new IndexNodeFactory() {
            @Override
            public IndexNode makeIndexNode() {
                return new FileBasedIndexNode();
            }
        }, x -> x.dogName, y -> y.name);
        index.addSource(xiaoming);
        index.addTarget(dog);
        index.addTarget(dog2);
        index.addTarget(dog3);
        index.addSource(laowang);

        System.out.println(index);

    }
}
