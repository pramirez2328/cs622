package interfaces;

import java.util.List;

public class Main {
    public static void main(String[] args) {


        List<Payable> payables = List.of(new Contractor("John"), new FullTimeEmployee("Pedro"));

        for (Payable payable : payables) {
            System.out.println(payable.toString());
            System.out.println(payable instanceof Contractor ? "Weekly payment " + payable.calculatePay() : "Monthly payment " + payable.calculatePay());
        }
    }
}
