
/*
    NetID: EXR180014
    Author: Erik Rodriguez
 */
public class Customer {
    protected String firstName;
    protected String lastName;
    protected int guestId;
    protected float amountSpent;

    Customer(String firstName, String lastName, int guestId, float amountSpent) { // base customer object
        this.firstName = firstName;
        this.lastName = lastName;
        this.guestId = guestId;
        this.amountSpent = amountSpent;
    }

    public void setFirstName(String firstName){  this.firstName = firstName; } // Setters
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public void setAmountSpent(float amountSpent) { this.amountSpent = amountSpent; }


    public String getFirstName() { return firstName; } // Getters
    public String getLastName() { return lastName; }
    public int getGuestId() { return guestId; }
    public float getAmountSpent() { return amountSpent; }


    @Override
    public String toString() {
        return getGuestId() + " " + getFirstName() + " " + getLastName() + " " + String.format("%.2f", getAmountSpent());

    }
}
