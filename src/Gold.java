/*
    NetID: EXR180014
    Author: Erik Rodriguez
 */
public class Gold extends Customer{
    private float discountPercentage;

    Gold(String firstName, String lastName, int guestId, float amountSpent, float discountPercentage) {
        super(firstName, lastName, guestId, amountSpent);
        this.discountPercentage = discountPercentage;
    }

    public float getDiscountPercent() { return discountPercentage; } // getter
    public void setDiscountPercent(float discountPercentage) { this.discountPercentage = discountPercentage; } // setter

    @Override
    public String toString() {
        return super.toString() + " " + (int)getDiscountPercent() + "%";

    }

}
