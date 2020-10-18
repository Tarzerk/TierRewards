/*
    A program that takes files of customers and orders inputted by the user,
    reads the data and based on the amount a customer spends we place them in
    a rewards tier for future purchases.

    NetID: EXR180014
    Author: Erik Rodriguez
 */

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws IOException {

        String filename;
        Scanner keyboard = new Scanner(System.in);

        int memberCount; // the amount of users in the regular customer file
        Customer[] regularCustomer;
        Customer[] preferredCustomer;

        System.out.print("Enter the regular customer file: "); // getting the files from the user
        filename = keyboard.nextLine();
        regularCustomer = ReadRegularCustomersFile(filename);

        memberCount = regularCustomer.length;

        System.out.print("Enter the preferred customer file: ");
        filename = keyboard.nextLine();
        preferredCustomer = ReadPreferredCustomerFile(filename, memberCount); // we pass in the amount of regular members


        System.out.print("Enter the orders file: "); // we validate and process the file
        filename = keyboard.nextLine();
        ReadOrders(filename, preferredCustomer, regularCustomer); // read and process orders

        WritePreferredFile(ResizeArray(preferredCustomer)); // resizing arrays and writing them to files
        WriteRegularFile(ResizeArray(regularCustomer));

    }

    /*
        A function that takes an array and copies it to another making
         sure only have valid data is inside
     */

    public static Customer[] ResizeArray (Customer [] customerArray) {
        int count = 0;

        for (Customer customer : customerArray) { // we count the amount of valid data inside
            if ((customer != null) && (customer.getGuestId() != -1)) {
                count++;
            }
        }
        Customer [] resizedArray = new Customer[count]; // create the resized array

        count = 0;

        for (Customer customer : customerArray) { // copies contents of old to new resized array
            if ((customer != null) && (customer.getGuestId() != -1)) {
                resizedArray[count] = customer;
                count++;
            }
        }
        return resizedArray;
    }



    public static void ReadOrders(String file, Customer[] preferredCustomer, Customer[] regularCustomer) throws IOException {
        Scanner scanFile = new Scanner(new File(file));

        String orderData; // will hold the data information
        while(scanFile.hasNextLine()) {

            orderData = scanFile.nextLine(); //  writes the order data to a string
            String[] orderDetails = orderData.split(" ");
            if (ValidateOrder(orderDetails, preferredCustomer, regularCustomer)) { // we validate those elements
                preferredCustomer = CalculateOrder(orderDetails, preferredCustomer,regularCustomer); // if validated we calculate the order
            }
        }
    }

    public static Customer[] CalculateOrder(String [] orderDetails, Customer[] preferred, Customer[] regular)  {
        int fileID = Integer.parseInt(orderDetails[0]); // the id in the file read
        int regularIndex = -1, preferredIndex = -1;
        for(int i=0;i<regular.length;i++) { // to find the location of the customer in the regular array
            if ((regular[i] != null ) && (fileID == regular[i].guestId ) ) {
                regularIndex = i;
                break;
            }
        }

        if (regularIndex == -1) { // if ID isn't found in the regular array
            for(int i=0;i<preferred.length;i++) { // to find the location of customer in the preferred array
                if (preferred[i] != null && fileID == preferred[i].guestId) {
                    preferredIndex = i;
                    break;
                }
            }
        }

        int availableSpot = 0; // save the spot where we can write in the preferred array
        while (availableSpot < preferred.length) {
            if (preferred[availableSpot] == null || preferred.length-availableSpot == 1)
                break;
            availableSpot++;

        }

        boolean userUpgraded = false;

        if (regularIndex != -1) { // if its a regular user check if he can be upgraded to gold or plat
            preferred = UpgradeRegularGuy(regularIndex, availableSpot, orderDetails, regular, preferred);
        }
        if (preferredIndex != -1 && preferred[preferredIndex] instanceof Gold) { // if he is a Gold apply and check for upgrade
            UpgradeGoldGuy(preferredIndex, preferred, orderDetails);
            userUpgraded = true;
        }
        if (preferredIndex != -1 && (preferred[preferredIndex]) instanceof Platinum && !userUpgraded) { // if he is a platinum
            GiveBucksToPlatinum(preferred, preferredIndex, orderDetails);
        }


        return preferred;
    }

    public static Customer[] UpgradeRegularGuy(int regularIndex, int availableSpot, String[] orderDetails, Customer[] regular, Customer[] preferred) {
        // add price
        float currentAmountSpent = regular[regularIndex].getAmountSpent() + (float)PriceOfCup(orderDetails);
        float originalOrderPrice = (float)PriceOfCup(orderDetails);
        float newPrice;
        if (currentAmountSpent>=230) { // regular to Platinum
            newPrice = originalOrderPrice*.85f; // in this case they get massive discount and upgraded to platinum
            regular[regularIndex].setAmountSpent(newPrice+regular[regularIndex].getAmountSpent());
            currentAmountSpent = regular[regularIndex].getAmountSpent();
            float Bucks = (float) Math.floor((currentAmountSpent - 200) / 5);
            preferred[availableSpot] = new Platinum(regular[regularIndex].getFirstName(),regular[regularIndex].getLastName(),
                    regular[regularIndex].getGuestId(),regular[regularIndex].getAmountSpent(),
                    Bucks);
            regular[regularIndex].setGuestId(-1);
        }
        if (currentAmountSpent >= 150) {
            newPrice = originalOrderPrice*.85f; // 15% discount
            regular[regularIndex].setAmountSpent(newPrice + regular[regularIndex].getAmountSpent());
            preferred[availableSpot] = new Gold(regular[regularIndex].getFirstName(),regular[regularIndex].getLastName(),
                    regular[regularIndex].getGuestId(),
                    regular[regularIndex].getAmountSpent(),15f);
            regular[regularIndex].setGuestId(-1);
        }
        else if (currentAmountSpent >= 100) {
            newPrice = originalOrderPrice*.90f; // 10% discount
            regular[regularIndex].setAmountSpent(newPrice + regular[regularIndex].getAmountSpent());
            preferred[availableSpot] = new Gold(regular[regularIndex].getFirstName(),regular[regularIndex].getLastName(),
                    regular[regularIndex].getGuestId(),
                    regular[regularIndex].getAmountSpent(),10f);
            regular[regularIndex].setGuestId(-1);
        }
        else if (currentAmountSpent >= 50) {
            newPrice = originalOrderPrice*.95f; // 5% discount
            regular[regularIndex].setAmountSpent(newPrice + regular[regularIndex].getAmountSpent());
            preferred[availableSpot] = new Gold(regular[regularIndex].getFirstName(),regular[regularIndex].getLastName(),
                    regular[regularIndex].getGuestId(),
                    regular[regularIndex].getAmountSpent(),5f);
            regular[regularIndex].setGuestId(-1);
        }
        else { // if the user didn't get a discount
            regular[regularIndex].setAmountSpent(currentAmountSpent);
        }
        return preferred;
    }
    public static void UpgradeGoldGuy(int preferredIndex, Customer[] preferred, String[] orderDetails) {

        DecimalFormat df = new DecimalFormat("#.##");
        //df.setRoundingMode(RoundingMode.DOWN);


        // we first add price of order + price of cup
        float currentAmountSpent = (float) (preferred[preferredIndex].getAmountSpent() +
                (float)PriceOfCup(orderDetails) *
                        (1 - (((Gold)preferred[preferredIndex]).getDiscountPercent()) / 100.00));
        String currentAmountSpentRounded = df.format(currentAmountSpent);
        currentAmountSpent = Float.parseFloat(currentAmountSpentRounded); // rounding our answer
        float originalOrderPrice = (float)PriceOfCup(orderDetails);
        float priceOrderWithDiscount = (float) ((float)PriceOfCup(orderDetails) *(1-(((Gold)preferred[preferredIndex]).getDiscountPercent()) / 100.00));
        float newPrice;


        if (currentAmountSpent >= 200) { // if his total order >=200 upgrade to platinum
            preferred[preferredIndex].setAmountSpent(currentAmountSpent);
            float Bucks = (float) Math.floor((currentAmountSpent - 200) / 5); // 1 bonusBucks for every 5 dollar spent on order over 200
            preferred[preferredIndex] = new Platinum(preferred[preferredIndex].getFirstName(),preferred[preferredIndex].getLastName(),
                    preferred[preferredIndex].getGuestId(),
                    preferred[preferredIndex].getAmountSpent(),Bucks);

        } else if (currentAmountSpent >= 150) { // give 15% discount
            newPrice = originalOrderPrice*.85f;
            preferred[preferredIndex].setAmountSpent((newPrice + preferred[preferredIndex].getAmountSpent()));
            ((Gold)preferred[preferredIndex]).setDiscountPercent(15f);
        } else if (currentAmountSpent >= 100) { // give 10% discount
            newPrice = originalOrderPrice*.90f;
            preferred[preferredIndex].setAmountSpent((newPrice + preferred[preferredIndex].getAmountSpent()));
            ((Gold)preferred[preferredIndex]).setDiscountPercent(10f);
        } else {
            preferred[preferredIndex].setAmountSpent(currentAmountSpent); // didn't get upgraded
        }

    }
    public static void GiveBucksToPlatinum(Customer[] preferred, int preferredIndex, String[] orderDetails) {
        float priceOfCup = (float)PriceOfCup(orderDetails);
        float totalCurrentAmountSpent = preferred[preferredIndex].getAmountSpent() + priceOfCup;
        float bonus = ((Platinum)preferred[preferredIndex]).getBonusBucks();
        if (bonus > 0) {
            priceOfCup = priceOfCup - bonus;
            totalCurrentAmountSpent = totalCurrentAmountSpent - bonus;
            bonus = (float) Math.floor(priceOfCup/5); // update bonus bucks
        } else { // when they are platinum but didn't have bonus bucks to start
            bonus = (float) Math.floor(priceOfCup / 5f); // update bonus bucks
        }

        preferred[preferredIndex].setAmountSpent(totalCurrentAmountSpent);
        ((Platinum)preferred[preferredIndex]).setBonusBucks(bonus);
    }


    public static double PriceOfCup (String[] orderDetails){
        double[] smallCup = new double [] {4,4.5,12};   // the cup dimensions as follow (Diameter, Height, Oz Capacity)
        double[] mediumCup = new double [] {4.5,5.75,20};
        double[] largeCup = new double [] {5.5,7,32};

        double surfaceArea = 0;
        double drinkPrice = -1.1;

        switch (orderDetails[2]){ // Price of Drink Per Ounce
            case "soda":
                drinkPrice = .20;
                break;
            case "tea":
                drinkPrice = .12;
                break;
            case "punch":
                drinkPrice = .15;
                break;
        }

        double cupCapacity = -1.1;
        switch (orderDetails[1]) {
            case "S":
                surfaceArea = CalculateSurfaceArea(smallCup); // calculate area of needed cup
                cupCapacity = smallCup[2];
                break;
            case "M":
                surfaceArea = CalculateSurfaceArea(mediumCup);
                cupCapacity = mediumCup[2];
                break;
            case "L":
                surfaceArea = CalculateSurfaceArea(largeCup);
                cupCapacity = largeCup[2];
                break;
        }

        float price;

        if (Double.parseDouble(orderDetails[3]) > 0) { // If the cup is customized
            price = (float) (surfaceArea * Double.parseDouble(orderDetails[3]) * Float.parseFloat(orderDetails[4]));
            price = (float) (price + (Float.parseFloat(orderDetails[4]) * drinkPrice * cupCapacity));
        } else { // cup isn't customized
            price = (float) (drinkPrice * cupCapacity * Float.parseFloat(orderDetails[4]));
        }



        return price;
    }

    public static double CalculateSurfaceArea(double[] cupDimensions){
        return (2.0 * Math.PI * (cupDimensions[0]/2) * cupDimensions[1]); // 2 * Pi * radius * height
    }


    public static boolean ValidateOrder(String [] orderDetails, Customer[] preferred, Customer[] regular) {

        try{ // validating there is numbers in appropriate places

            Double.parseDouble(orderDetails[3]);
            Integer.parseInt(orderDetails[0]);
            Integer.parseInt(orderDetails[4]);
        }catch (NumberFormatException | IndexOutOfBoundsException e){
            return false;
        }

        if (orderDetails.length != 5){ // checks it has the right amount of data
            return false;
        }

        int regularUsers = regular.length;
        int preferredUsers = preferred.length; // I did this because I made the preferred array the size
        // or regular + preferred users

        boolean foundInRegular = false;


        for(int i=0;i<regularUsers && !foundInRegular;i++){
            if (Integer.parseInt(orderDetails[0]) == regular[i].guestId) { // checks if its in the regular array
                foundInRegular = true;
            }
        }

        if (!foundInRegular){
            for(int i=0;i<preferredUsers && preferred[i] != null ;i++) {
                if (Integer.parseInt(orderDetails[0]) == preferred[i].guestId) { // checks if its in preferred array
                    foundInRegular = true;
                }
            }
        }


        if (!foundInRegular) // if it isn't on either then stop checking
            return false;

        switch (orderDetails[1]) { // check they have a valid cup size
            case "S":
            case "M":
            case "L":
                break;
            default:
                return false;
        }

        switch (orderDetails[2]) { // valid flavor
            case "soda":
            case "tea":
            case "punch":
                break;
            default:
                return false;
        }


        if ((Double.parseDouble(orderDetails[3]) < 0) || (Integer.parseInt(orderDetails[4]) < 0)) // check for positive values
            return false;

        return true;
    }


    public static Customer[] ReadRegularCustomersFile(String filename) throws FileNotFoundException {

        Scanner scanFile = new Scanner(new File(filename));
        Scanner counter = new Scanner(new File(filename));

        String userData; // will hold a line in the file containing user information
        int memberCount = 0;
        while (counter.hasNextLine()) { // counts users inside file
            memberCount++;
            userData = counter.nextLine();
        }
        counter.close();



        Customer[] regularCustomers = new Customer[memberCount]; // creates an array of regular customers
        memberCount = 0; // we will use this to create each member

        while (scanFile.hasNextLine()) { // fills in the array with appropriate information
            userData = scanFile.nextLine();
            String[] userInfo = userData.split(" ");
            regularCustomers[memberCount] = new Customer(userInfo[1],userInfo[2],Integer.parseInt(userInfo[0]),
                    Float.parseFloat(userInfo[3]));

            memberCount++;
        }

        return regularCustomers;
    }

    public static Customer[] ReadPreferredCustomerFile(String filename, int memberCount) throws FileNotFoundException {

        File file = new File(filename);
        if (!file.isFile()) { // checking if the file exists, if not then just return a smaller array
            Customer[] preferredArray = new Customer[memberCount];
            return preferredArray;
        }

        Scanner scnF = new Scanner(new File(filename));
        Scanner counterPreferred = new Scanner(new File(filename));

        int preferredCount = 0;
        String userData;
        while (counterPreferred.hasNextLine()) { // counts users inside file
            preferredCount++;
            userData = counterPreferred.nextLine();
        }
        counterPreferred.close();

        Customer[] preferredCustomers = new Customer[preferredCount + memberCount];  // max number of possible preferred
        preferredCount = 0; // we will use this to create each profile
        while (scnF.hasNextLine()) { // Creates array of preferred customers
            userData = scnF.nextLine();
            String[] userInfo = userData.split(" ");

            if (userData.charAt(userData.length()-1) == '%') { // checks if they have discount or BonusBucks using % sign
                preferredCustomers[preferredCount] = new Gold(userInfo[1],userInfo[2], // if they have sign make Gold user
                        Integer.parseInt(userInfo[0]),
                        Float.parseFloat(userInfo[3]),
                        Float.parseFloat(RemoveLastCharacter(userInfo[4])));
            }
            else {
                preferredCustomers[preferredCount] = new Platinum(userInfo[1],userInfo[2], // make platinum user
                        Integer.parseInt(userInfo[0]),
                        Float.parseFloat(userInfo[3]),
                        Float.parseFloat(userInfo[4]));
            }
            preferredCount++;
        }

        return preferredCustomers;
    }

    public static String RemoveLastCharacter(String str) { // a method that chops the last character of string
        return str.substring(0,str.length()- 1);
    }

    public static void WriteRegularFile(Customer [] regular) throws IOException { // writes regular file
        PrintWriter file = new PrintWriter("customer.dat");

        for (Customer customer : regular) {
            file.println(customer.toString());

        }

        file.close();
    }

    public static void WritePreferredFile(Customer [] preferred) throws IOException { // writes preferred file
        PrintWriter file = new PrintWriter("preferred.dat");

        for (Customer customer : preferred) {
            if (customer instanceof Gold) {
                file.println(customer.toString());
            } else if (customer instanceof Platinum) {
                file.println(customer.toString());
            }
        }

        file.close();
    }

}
