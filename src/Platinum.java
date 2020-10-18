/*
    NetID: EXR180014
    Author: Erik Rodriguez
 */
public class Platinum extends Customer {
    private float bonusBucks;

    Platinum(String firstName, String lastName, int guestId, float amountSpent, float bonusBucks) {
        super(firstName, lastName, guestId, amountSpent);
        this.bonusBucks = bonusBucks;
    }

    public float getBonusBucks() { return bonusBucks; } // getter
    public void setBonusBucks(float bonusBucks) { this.bonusBucks = bonusBucks;} // setter

    @Override
    public String toString() {
        return super.toString() + " " + (int)getBonusBucks();

    }
}
