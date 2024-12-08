import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Car {
    private List<String> variants;
    private List<String> price_range;

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }

    public List<String> getPrice_range() {
        return price_range;
    }

    public void setPrice_range(List<String> price_range) {
        this.price_range = price_range;
    }
}

public class Main {
    private static Map<String, Car> carData;
    private static Map<String, Map<String, String>> accessoriesData;
    private static float selectedCarPrice = 0;
    private static String selectedCarName = "";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Read car data from JSON file
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            carData = objectMapper.readValue(new File("C:\\Users\\KratiMandloi\\IdeaProjects\\Question1\\src\\Car.json"), new TypeReference<Map<String, Car>>() {});

            accessoriesData = objectMapper.readValue(new File("C:\\Users\\KratiMandloi\\IdeaProjects\\Question1\\src\\Accessories.json"), new TypeReference<Map<String, Map<String, String>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Please enter your budget (in Lakhs):");
        float budget = sc.nextFloat();
        findCarsBasedOnBudget(budget);
        if (!selectedCarName.isEmpty()) {
            System.out.println("Enter the name of the car you want to select:");
            sc.nextLine();
            selectedCarName = sc.nextLine();

            if (carData.containsKey(selectedCarName)) {
                Car car = carData.get(selectedCarName);
                selectedCarPrice = Float.parseFloat(car.getPrice_range().get(0)); // Assuming you select the first price in the range
                System.out.println("You selected " + selectedCarName + " with a price of ₹" + selectedCarPrice + " Lakhs.");
            } else {
                System.out.println("Invalid car name entered.");
                return;
            }
        } else {
            System.out.println("No cars available within your budget.");
            return;
        }
        showCarVariants(selectedCarName);
        WantAccesories();
    }

    public static void WantAccesories() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want accessories? (yes/no)");
        String yn = sc.nextLine();
       //selected car price ko lakhs me convert krna pdega: 4.99
        selectedCarPrice=selectedCarPrice*100000;
        float insurance = 0, reg = 0, handlingFee = 0;
        insurance = (float) ((selectedCarPrice*0.07));
        reg = (float) ( (selectedCarPrice * 0.12));
        handlingFee = (float) ((selectedCarPrice * 0.02));

        if (yn.equals("no")) {

            System.out.println("Final price of your car after adding Registration, Insurance, and Handling Fees is: ₹" + (selectedCarPrice+insurance+handlingFee+reg));
        } else {
            System.out.println("List of accessories are:");
            displayAccessoriesList();
            float totalAccessoriesCost = 0;
            String accessoryChoice;
            do {
                System.out.println("Enter the accessory you want (or type 'done' to finish):");
                accessoryChoice = sc.nextLine();

                if (!accessoryChoice.equalsIgnoreCase("done") && accessoriesData != null) {
                    float accessoryPrice = getAccessoryPrice(accessoryChoice);
                    if (accessoryPrice != -1) {
                        totalAccessoriesCost += accessoryPrice;
                        System.out.println("Added " + accessoryChoice + " with price ₹" + accessoryPrice + " Lakhs.");
                    } else {
                        System.out.println("Accessory not found.");
                    }
                }
            } while (!accessoryChoice.equalsIgnoreCase("done"));
            System.out.println("Final price of your car after accessories is: ₹" + (selectedCarPrice+insurance+handlingFee+reg) + totalAccessoriesCost );
        }
    }

    public static void displayAccessoriesList() {
        if (accessoriesData == null) {
            System.out.println("No data available to display accessories.");
            return;
        }
        for (Map.Entry<String, Map<String, String>> categoryEntry : accessoriesData.entrySet()) {
            String category = categoryEntry.getKey();
            Map<String, String> accessories = categoryEntry.getValue();
            System.out.println(category + ":");
            for (Map.Entry<String, String> accessoryEntry : accessories.entrySet()) {
                String accessoryName = accessoryEntry.getKey();
                String priceRange = accessoryEntry.getValue();
                System.out.println("  - " + accessoryName + ": " + priceRange);
            }
            System.out.println();
        }
    }

    public static float getAccessoryPrice(String accessoryName) {
        for (Map.Entry<String, Map<String, String>> categoryEntry : accessoriesData.entrySet()) {
            Map<String, String> accessories = categoryEntry.getValue();
            if (accessories.containsKey(accessoryName)) {
                String priceRange = accessories.get(accessoryName);
                priceRange = priceRange.replace("₹", "").replace(",", "");
                String[] prices = priceRange.split(" - ");
                try {
                    float minPrice = Float.parseFloat(prices[0]);
                    float maxPrice = Float.parseFloat(prices[1]);
                    return (minPrice + maxPrice) / 2;
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing price for accessory: " + accessoryName);
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
    public static void findCarsBasedOnBudget(float budget) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Cars available within your budget of " + budget + " Lakhs:");
        for (String carName : carData.keySet()) {
            Car car = carData.get(carName);
            List<String> priceRange = car.getPrice_range();
            for (String price : priceRange) {
                float priceValue = Float.parseFloat(price);
                if (priceValue <= budget) {
                    System.out.println("Car: " + carName + " - Price: ₹" + priceValue + " Lakhs");
                    selectedCarName = carName;
                    selectedCarPrice = priceValue;
                    break;
                }
            }
        }
        if (selectedCarName.isEmpty()) {
            System.out.println("No cars available within your budget.");
        }
    }

    public static void showCarVariants(String carName) {
        if (carData.containsKey(carName)) {
            Car car = carData.get(carName);
            List<String> variants = car.getVariants();
            System.out.println("Variants available for " + carName + ":");
            for (int i = 0; i < variants.size(); i++) {
                System.out.println((i + 1) + ". " + variants.get(i));
            }
            System.out.println("Enter the name of the variant you like:");
            Scanner sc = new Scanner(System.in);
            String variantName = sc.nextLine();
            System.out.println("You chose " + carName + " " + variantName);
        } else {
            System.out.println("Car not found.");
        }
    }
}
