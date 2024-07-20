package org.rapidTransit.model;

public class User extends Person {
    private float balance;
    private boolean isBlocked;

    public User(long id, String email, String password, String name, float balance, boolean isBlocked) {
        super(id, email, password, name);
        this.balance = balance;
        this.isBlocked = isBlocked;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
