package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * Односвязный список
 */
public class MyLinkedList extends List {
    /**
     * private - используется для сокрытия этого класса от других.
     * Класс доступен только изнутри того, где он объявлен
     * <p>
     * static - позволяет использовать Node без создания экземпляра внешнего класса
     */

    int size;
    Node header;

    private static class Node {
        Node prev;
        Node next;
        int val;

        Node(Node prev, Node next, int val) {
            this.prev = prev;
            this.next = next;
            this.val = val;
        }
    }

    public MyLinkedList() {
        this.header = new Node(null, null, 0);
        this.header.next = header;
        this.header.prev = header;
    }

    @Override
    void add(int item) {
        Node newNode = new Node(this.header.prev, this.header, item);
        newNode.prev.next = newNode;
        newNode.next.prev = newNode;
        this.size++;
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx<0 | idx>=this.size) throw new NoSuchElementException();
        Node itNode = header.next;
        int i = 0;
        while (i<idx) {
            itNode = itNode.next;
            i++;
        }
        itNode.prev.next = itNode.next;
        itNode.next.prev =  itNode.prev;
        this.size--;
        return itNode.val;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx<0 | idx>=this.size) throw new NoSuchElementException();
        Node itNode = header.next;
        int i = 0;
        while (i<idx) {
            itNode = itNode.next;
            i++;
        }
        return itNode.val;
    }

    @Override
    int size() {
        return this.size;
    }
}
