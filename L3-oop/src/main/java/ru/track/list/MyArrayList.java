package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 *
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List {

    int[] elementData;
    int size;

    public MyArrayList() {
        this(10);
    }

    public MyArrayList(int capacity) {
        this.elementData = new int[capacity];
    }

    @Override
    void add(int item) {
        if (this.size < this.elementData.length) {
            this.elementData[this.size++] = item;
        } else {
            int[] newArray = new int[this.elementData.length+1];
            System.arraycopy(this.elementData, 0, newArray, 0, this.elementData.length);
            newArray[newArray.length-1] = item;
            this.elementData = newArray;
            this.size++;
        }
    }

    @Override
    int remove(int idx) throws NoSuchElementException {
        if (idx<0 | idx >= this.size) throw new NoSuchElementException();
        int[] newArray = new int[this.elementData.length-1];
        System.arraycopy(this.elementData, 0, newArray, 0, idx);
        System.arraycopy(this.elementData, idx+1, newArray, idx, this.size-idx-1);
        int result = this.get(idx);
        this.elementData = newArray;
        this.size--;
        return result;
    }

    @Override
    int get(int idx) throws NoSuchElementException {
        if (idx<0 | idx >= this.size) throw new NoSuchElementException();
        return this.elementData[idx];
    }

    @Override
    int size() {
        return this.size;
    }
}

