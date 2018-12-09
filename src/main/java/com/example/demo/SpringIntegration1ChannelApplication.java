package com.example.demo;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringIntegration1ChannelApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringIntegration1ChannelApplication.class, args);
		 Scanner scanner = new Scanner(System.in);
		    System.out.print("Please enter q and press <enter> to exit the program: ");
		     
		    while (true) {
		       String input = scanner.nextLine();
		       if("q".equals(input.trim())) {
		          break;
		      }
		    }
		    scanner.close();
	}
}
