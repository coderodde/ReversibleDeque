package com.github.coderodde.util;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link java.util.Deque} interface via a 
 * doubly-linked list. It runs the reversal operation in constant time.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Jan 16, 2022)
 * @since 1.6 (Jan 16, 2022)
 */
public class ReversibleDeque<E> implements Deque<E>, List<E> {

    private static final class Node<E> {
        E value;
        Node<E> prev;
        Node<E> next;
        
        Node(E value) {
            this.value = value;
        }
    }
    
    private int size;
    private int modCount;
    private boolean reverted;
    private Node<E> head;
    private Node<E> tail;
    
    // O(1)!
    public void revert() {
        reverted = !reverted;
        modCount++;
    }
    
    public boolean isReverted() {
        return reverted;
    }

    @Override
    public E get(int index) {
        checkNotEmpty();
        checkAccessIndex(index);
        return reverted ? getReverted(index) : getNonReverted(index);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addFirst(E e) {
        Node<E> newNode = new Node<>(e);
        
        if (reverted) {
            addFirstReverted(newNode);
        } else {
            addFirstNonReverted(newNode);
        }
        
        size++;
        modCount++;
    }
    
    @Override
    public void addLast(E e) {
        Node<E> newNode = new Node<>(e);
        
        if (reverted) {
            addLastReverted(newNode);
        } else {
            addLastNonReverted(newNode);
        }
        
        size++;
        modCount++;
    }

    @Override
    public boolean offerFirst(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offerLast(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E removeFirst() {
        checkNotEmpty();
        size--;
        modCount++;
        return reverted ? removeFirstReverted() : removeFirstNonReverted();
    }

    @Override
    public E removeLast() {
        checkNotEmpty();
        size--;
        modCount++;
        return reverted ? removeLastReverted() : removeLastNonReverted();
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E getFirst() {
        checkNotEmpty();
        return reverted ? tail.value : head.value;
    }

    @Override
    public E getLast() {
        checkNotEmpty();
        return reverted ? head.value : tail.value;
    }

    @Override
    public E peekFirst() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public E peekLast() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public E remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E poll() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public E element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public void push(E e) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public E pop() {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return reverted ? new BackwardIterator() : new ForwardIterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return reverted ? new ForwardIterator() : new BackwardIterator();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException(); 
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        size = 0;
        modCount++;
        
        // Help GC:
        for (Node<E> node = head; node != null;) {
            Node<E> nextNode = node.next;
            nullify(node);
            node = nextNode;
        }
        
        head = tail = null;
    }
    
    private void prependNode(Node<E> newNode) {
        if (size == 0) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    }

    private void appendNode(Node<E> newNode) {
        if (size == 0) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
    }
    
    private void addFirstReverted(Node<E> newNode) {
        appendNode(newNode);
    }
    
    private void addFirstNonReverted(Node<E> newNode) {
        prependNode(newNode);
    }
    
    private void addLastReverted(Node<E> newNode) {
        prependNode(newNode);
    }
    
    private void addLastNonReverted(Node<E> newNode) {
        appendNode(newNode);
    }
    
    private E removeHeadImpl() {
        Node<E> nodeToRemove = head;
        E returnValue = nodeToRemove.value;
        
        if (size == 0) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        
        // Help GC:
        nullify(nodeToRemove);
        return returnValue;
    }
    
    private E removeTailImpl() {
        Node<E> nodeToRemove = tail;
        E returnValue = nodeToRemove.value;
        
        if (size == 0) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        
        // Help GC:
        nullify(nodeToRemove);
        return returnValue;
    }
    
    private E removeFirstReverted() {
        return removeTailImpl();
    }
    
    private E removeFirstNonReverted() {
        return removeHeadImpl();
    }
    
    private E removeLastReverted() {
        return removeHeadImpl();
    }
    
    private E removeLastNonReverted() {
        return removeTailImpl();
    }
    
    private static <E> void nullify(Node<E> node) {
        node.value = null;
        node.prev = node.next = null;
    }
    
    private void checkNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("Accessing an empty deque.");
        }
    }
    
    private void checkAccessIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index = " + index);
        }
        
        if (index >= size) {
            throw new IllegalArgumentException(
                    "index = " + index + ", size = " + size);
        }
    }
    
    private E getForward(int index) {
        Node<E> node = head;
        
        while (index-- > 0) {
            node = node.next;
        }
        
        return node.value;
    }
    
    private E getBackward(int index) {
        Node<E> node = tail;
        
        while (index-- > 0) {
            node = node.prev;
        }
        
        return node.value;
    }
    
    private E getReverted(int index) {
        if (index > size / 2) {
            return getForward(size - index - 1);
        } else {
            return getBackward(index);
        }
    }
    
    private E getNonReverted(int index) {
        if (index > size / 2) {
            return getBackward(size - index - 1);
        } else {
            return getForward(index);
        }
    }
    
    private class ForwardIterator implements Iterator<E> {

        protected final int expectedModCount = ReversibleDeque.this.modCount;
        protected Node<E> currentNode = ReversibleDeque.this.head;
        protected int iterated;
        
        @Override
        public boolean hasNext() {
            checkForConcurrentModification();
            return iterated < ReversibleDeque.this.size;
        }

        @Override
        public E next() {
            checkForConcurrentModification();
            
            if (!hasNext()) {
                throw new NoSuchElementException(
                        "No more elements to iterate.");
            }
            
            iterated++;
            E value = currentNode.value;
            currentNode = currentNode.next;
            return value;
        }
        
        protected void checkForConcurrentModification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private class BackwardIterator extends ForwardIterator {

        private Node<E> currentNode = ReversibleDeque.this.tail;
        
        @Override
        public E next() {
            checkForConcurrentModification();
            
            if (!hasNext()) {
                throw new NoSuchElementException(
                        "No more elements to iterate.");
            }
            
            iterated++;
            E value = currentNode.value;
            currentNode = currentNode.prev;
            return value;
        }
    }
}
