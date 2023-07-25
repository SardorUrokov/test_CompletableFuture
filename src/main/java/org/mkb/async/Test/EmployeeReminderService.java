package org.mkb.async.Test;

import org.mkb.async.dto.Employee;
import org.mkb.async.database.EmployeeDatabase;

import java.util.stream.Collectors;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EmployeeReminderService {

    public CompletableFuture<Void> sendReminderToEmployee() {
        Executor executor = Executors.newFixedThreadPool(5);

        return CompletableFuture.supplyAsync(() -> {
            System.out.println("fetchEmployee : " + Thread.currentThread().getName());
            return EmployeeDatabase.fetchEmployees();
        }, executor).thenApplyAsync((employees) -> {
            System.out.println("filter new joiner employee  : " + Thread.currentThread().getName());
            return employees.stream()
                    .filter(employee -> "true".equals(employee.isNewJoiner()))
                    .collect(Collectors.toList());
        }, executor).thenApplyAsync((employees) -> {
            System.out.println("filter training not complete employee  : " + Thread.currentThread().getName());
            return employees.stream()
                    .filter(employee -> "true".equals(employee.isLearningPending()))
                    .collect(Collectors.toList());
        }, executor).thenApplyAsync((employees) -> {
            System.out.println("get emails  : " + Thread.currentThread().getName());
            return employees.stream()
                    .map(Employee::getEmail)
                    .collect(Collectors.toList());
        }, executor).thenAcceptAsync((emails) -> {
            System.out.println("send email  : " + Thread.currentThread().getName());
            emails.forEach(EmployeeReminderService::sendEmail);
        }, executor);
    }

    public static void sendEmail(String email) {
        System.out.println("sending training reminder email to : " + email);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        EmployeeReminderService service = new EmployeeReminderService();
        service.sendReminderToEmployee().get();
    }
}