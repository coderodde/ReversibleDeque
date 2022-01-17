package com.github.coderodde.util.dequeapp;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {

    private static final String PROMPT = ">>> ";
    private static final String ERROR = "ERROR: ";
    
    public static void main(String[] args) {
        Application app = new Application();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print(PROMPT);
            String cmd = scanner.nextLine();
            String[] tokens = cmd.split("\\s+");
            
            if (exitRequested(tokens)) {
                break;
            }
            
            try {
                app.processCommand(tokens);
            } catch (NoSuchElementException ex) {
                System.out.println(ERROR + ex.getMessage());
            }
        }
        
        System.out.println("Bye!");
    }
    
    private static boolean exitRequested(String[] tokens) {
        return tokens[0].trim().toLowerCase().equals("quit");
    }
}

