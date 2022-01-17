
package com.github.coderodde.util;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReversibleDequeTest {

    private final ReversibleDeque<Integer> deque = new ReversibleDeque<>();
    
    @Before
    public void before() {
        deque.clear();
    }
    
    @Test
    public void revert() {
        final int num = 5;
        load(num);
        
        for (int i = 1; i <= num; i++) {
            assertEquals(Integer.valueOf(i), deque.get(i - 1));
        }
        
        deque.revert();
        
        for (int i = 1; i <= num; i++) {
            assertEquals(Integer.valueOf(num - i + 1), deque.get(i - 1));
        }
        
        deque.revert();
        
        for (int i = 1; i <= num; i++) {
            assertEquals(Integer.valueOf(i), deque.get(i - 1));
        }
    }
    
    @Test
    public void addFirst() {
        load(5);
        deque.addFirst(100);
        
        assertEquals(Integer.valueOf(100), deque.getFirst());
        
        deque.revert();
        
        assertEquals(Integer.valueOf(100), deque.getLast());
        
        deque.revert();
        
        assertEquals(Integer.valueOf(100), deque.getFirst());
    }
    
    @Test
    public void addLast() {
        load(5);
        deque.addLast(100);
        
        assertEquals(Integer.valueOf(100), deque.getLast());
        
        deque.revert();
        
        assertEquals(Integer.valueOf(100), deque.getFirst());
        
        deque.revert();
        
        assertEquals(Integer.valueOf(100), deque.getLast());
    }
    
    @Test
    public void revertThenRemoveFirstThenRevert() {
        load(6);
        deque.revert();
        deque.removeFirst();
        
        assertEquals(Integer.valueOf(5), deque.get(0));
        assertEquals(Integer.valueOf(4), deque.get(1));
        assertEquals(Integer.valueOf(3), deque.get(2));
        assertEquals(Integer.valueOf(2), deque.get(3));
        assertEquals(Integer.valueOf(1), deque.get(4));
        
        deque.revert();
        
        assertEquals(Integer.valueOf(1), deque.get(0));
        assertEquals(Integer.valueOf(2), deque.get(1));
        assertEquals(Integer.valueOf(3), deque.get(2));
        assertEquals(Integer.valueOf(4), deque.get(3));
        assertEquals(Integer.valueOf(5), deque.get(4));
    }
    
    @Test
    public void revertThenRemoveLastThenRevert() {
        load(6);
        deque.revert();
        deque.removeLast();
        
        assertEquals(Integer.valueOf(6), deque.get(0));
        assertEquals(Integer.valueOf(5), deque.get(1));
        assertEquals(Integer.valueOf(4), deque.get(2));
        assertEquals(Integer.valueOf(3), deque.get(3));
        assertEquals(Integer.valueOf(2), deque.get(4));
        
        deque.revert();
        
        assertEquals(Integer.valueOf(2), deque.get(0));
        assertEquals(Integer.valueOf(3), deque.get(1));
        assertEquals(Integer.valueOf(4), deque.get(2));
        assertEquals(Integer.valueOf(5), deque.get(3));
        assertEquals(Integer.valueOf(6), deque.get(4));
    }
    
    @Test
    public void testIterator() {
        final int num = 5;
        load(num);
        
        Iterator<Integer> iterator = deque.iterator();
        
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertEquals(Integer.valueOf(5), iterator.next());
        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testDescndingIterator() {
        final int num = 5;
        load(num);
        
        Iterator<Integer> iterator = deque.descendingIterator();
        
        assertEquals(Integer.valueOf(5), iterator.next());
        assertEquals(Integer.valueOf(4), iterator.next());
        assertEquals(Integer.valueOf(3), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(1), iterator.next());
        
        assertFalse(iterator.hasNext());
    }
    
    private void load(int num) {
        for (int i = 1; i <= num; i++) {
            deque.addLast(Integer.valueOf(i));
        }
    }
}
