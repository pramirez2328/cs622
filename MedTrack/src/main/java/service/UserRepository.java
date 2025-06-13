package service;

import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository<T extends User> {
    private final List<T> users = new ArrayList<>();

    public void addUser(T user) {
        users.add(user);
    }

    public Optional<T> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public List<T> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean exists(String id) {
        return users.stream().anyMatch(user -> user.getId().equals(id));
    }
}
