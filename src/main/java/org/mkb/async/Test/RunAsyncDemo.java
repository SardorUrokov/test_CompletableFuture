package org.mkb.async.Test;

import org.mkb.async.dto.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RunAsyncDemo {

    public void saveEmployees(File jsonFile) throws ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
            try {
                List<Employee> employees = mapper
                        .readValue(jsonFile, new TypeReference<List<Employee>>() {
                        });
                //write logic t save list of employee to database
                //repository.saveAll(employees);
                System.out.println("Thread : " + Thread.currentThread().getName());
                System.out.println(employees.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        runAsyncFuture.get();
    }


    public void saveEmployeesWithCustomExecutor(File jsonFile) throws ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Executor executor = Executors.newFixedThreadPool(5);
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(
                () -> {
                    try {
                        List<Employee> employees = mapper
                                .readValue(jsonFile, new TypeReference<List<Employee>>() {
                                });
                        //write logic t save list of employee to database
                        //repository.saveAll(employees);
                        System.out.println("Thread : " + Thread.currentThread().getName());
                        System.out.println(employees.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },executor);

        runAsyncFuture.get();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RunAsyncDemo runAsyncDemo = new RunAsyncDemo();
        runAsyncDemo.saveEmployees(new File("employees.json"));
        runAsyncDemo.saveEmployeesWithCustomExecutor(new File("employees.json"));

    }
}