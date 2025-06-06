package interfaces;

public class Contractor implements Payable{
    //String name, double hourlyRate, int hoursWorked
    private final String name;
    private final double hourlyRate = 100;
    private final int hoursWorked = 40;

    public Contractor(String name) {
        this.name = name;
    }

    @Override
    public double calculatePay() {
        return hourlyRate * hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public String toString() {
        return "Contractor{" +
                "name='" + name + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", hoursWorked=" + hoursWorked +
                '}';
    }
}
