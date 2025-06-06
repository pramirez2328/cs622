package interfaces;

public class FullTimeEmployee implements Payable{
    private String name;
    private final double annualSalary = 100000;

    public FullTimeEmployee(String name) {
        this.name = name;
    }

    @Override
    public double calculatePay() {
        return annualSalary / 12;
    }

    public double getAnnualSalary() {
        return annualSalary;
    }

    public String toString() {
        return "FullTimeEmployee{" +
                "name='" + name + '\'' +
                ", annualSalary=" + annualSalary +
                '}';
    }
}
