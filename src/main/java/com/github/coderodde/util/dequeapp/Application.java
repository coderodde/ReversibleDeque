package com.github.coderodde.util.dequeapp;

import com.github.coderodde.util.ReversibleDeque;

public class Application {

    private static final class CommandNames {
        static final String ADD_FIRST    = "af";
        static final String ADD_LAST     = "al";
        static final String REMOVE_FIRST = "rf";
        static final String REMOVE_LAST  = "rl";
        static final String REVERT       = "rev";
        static final String IS_REVERTED  = "reverted";
    }
    
    private final ReversibleDeque<String> deque = new ReversibleDeque<>();

    public void processCommand(String[] tokens) {
        try {
            switch (tokens.length) {
                case 1:
                    processSingleTokenCommand(tokens[0].toLowerCase());
                    break;

                case 2:
                    processDoubleTokenCommand(tokens[0].toLowerCase(), 
                                              tokens[1]);
                    break;

                default:
                    reportBadCommand(tokens);
            }
        } catch (BadCommandException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private String stringify() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        boolean first = true;

        for (int i = 0; i < deque.size(); i++) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }

            sb.append(deque.get(i));
        }

        return sb.append("]").toString();
    }

    private void revertDeque() {
        deque.revert();
    }

    private boolean isReverted() {
        return deque.isReverted();
    }

    private void addFirst(String s) {
        deque.addFirst(s);
    }

    private void addLast(String s) {
        deque.addLast(s);
    }

    private void removeFirst() {
        deque.removeFirst();
    }

    private void removeLast() {
        deque.removeLast();
    }
    
    private void printDeque() {
        System.out.println(stringify());
    }
    
    private void processSingleTokenCommand(String token) 
            throws BadCommandException {
        
        switch (token) {
            case CommandNames.IS_REVERTED:
                commandIsReverted();
                break;
                
            case CommandNames.REVERT:
                commandRevert();
                break;
                
            case CommandNames.REMOVE_FIRST:
                commandRemoveFirst();
                break;
                
            case CommandNames.REMOVE_LAST:
                commandRemoveLast();
                break;
                
            default:
                throw new BadCommandException(token + ": unknown command.");
        }
    }
    
    private void processDoubleTokenCommand(String cmd, String arg) 
            throws BadCommandException {
        
        switch (cmd) {
            case CommandNames.ADD_FIRST:
                commandAddFirst(arg);
                break;
                
            case CommandNames.ADD_LAST:
                commandAddLast(arg);
                break;
        
            default:
                throw new BadCommandException(cmd + ": unknowon command.");
        }
    }
    
    private void commandAddFirst(String arg) {
        addFirst(arg);
        printDeque();
    }
    
    private void commandAddLast(String arg) {
        addLast(arg);
        printDeque();
    }
    
    private void commandIsReverted() {
        System.out.println(isReverted());
    }
    
    private void commandRevert() {
        revertDeque();
        printDeque();
    }
    
    private void commandRemoveFirst() {
        removeFirst();
        printDeque();
    }
    
    private void commandRemoveLast() {
        removeLast();
        printDeque();
    }
    
    private void reportBadCommand(String[] tokens) {
        String line = String.join(" ", tokens);
        System.out.println("Unknown command: " + line);
    }
}

class BadCommandException extends Exception {
    public BadCommandException(String message) {
        super(message);
    }
}