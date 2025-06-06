package inheritanceAndPolymorphym.animals;

public class Dog extends Animal {

    //override the makeSound method from the Animal class
    @Override
    public String makeSound() {
        return "I am a dog, bark, bark!";
    }
}
