package inheritanceAndPolymorphym;

import inheritanceAndPolymorphym.animals.Animal;
import inheritanceAndPolymorphym.animals.Cat;
import inheritanceAndPolymorphym.animals.Dog;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        List<Animal> animals = List.of(new Cat(), new Dog());

        for (Animal animal : animals) {
            System.out.println(animal.makeSound());
        }
    }
}