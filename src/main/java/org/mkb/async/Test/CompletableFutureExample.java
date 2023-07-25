package org.mkb.async.Test;

import org.mkb.async.dto.Employee;
import org.mkb.async.database.EmployeeDatabase;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExample {

    public static void main(String[] args) {
        List<Employee> customers = generateCustomersData(999);
        List<CompletableFuture<Void>> processingFutures = new ArrayList<>();

        for (Employee customer : customers) {
            CompletableFuture<Void> future = processCustomerAsync(customer);
            processingFutures.add(future);
        }
        // Wait for all CompletableFuture instances to complete (for demonstration purposes).
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(
                processingFutures.toArray(new CompletableFuture[0]));
        try {
            allOfFuture.get(); // Blocking call to wait for all tasks to complete.
            System.out.println("All customer data processed successfully.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Simulates generating a list of customer data.
    private static List<Employee> generateCustomersData(int numCustomers) {

        List<Employee> customers = new ArrayList<>();

        for (int i = 1; i <= numCustomers; i++) {

            final Employee employee = Objects.requireNonNull(EmployeeDatabase.fetchEmployees()).get(i);

            customers.add(
                    Employee.builder()
                            .employeeId(employee.getEmployeeId())
                            .firstName(employee.getFirstName())
                            .lastName(employee.getLastName())
                            .email(employee.getEmail())
                            .salary(employee.getSalary())
                            .build()
            );
        }
        customers.forEach(System.out::println);
        return customers;
    }

    // Simulates processing a customer asynchronously.
    private static CompletableFuture<Void> processCustomerAsync(Employee customer) {

        return CompletableFuture.runAsync(() -> {
                    // Simulate some time-consuming processing here (e.g., fetching data, calculations, etc.).
                    System.out.println(" Processing customer: " + customer.getFirstName() + " " + customer.getLastName() + " " +
                            "(ID: " + customer.getEmployeeId() +
                            ";  FullName: " + customer.getFirstName() + " " + customer.getLastName() +
                            ";  Email: " + customer.getEmail() + ")");

                    // Simulate some processing time (500 milliseconds to 2 seconds).
                    try {
                        Thread.sleep(500 + (long) (Math.random() * 1500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}