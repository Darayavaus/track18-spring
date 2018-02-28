package ru.track;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;


/**
 * Задание 1: Реализовать два метода
 *
 * Формат файла: текстовый, на каждой его строке есть (или/или)
 * - целое число (int)
 * - текстовая строка
 * - пустая строка (пробелы)
 *
 * Числа складываем, строки соединяем через пробел, пустые строки пропускаем
 *
 *
 * Пример файла - words.txt в корне проекта
 *
 * ******************************************************************************************
 *  Пожалуйста, не меняйте сигнатуры методов! (название, аргументы, возвращаемое значение)
 *
 *  Можно дописывать новый код - вспомогательные методы, конструкторы, поля
 *
 * ******************************************************************************************
 *
 */
public class CountWords {

    String skipWord;

    public CountWords(String skipWord) {

        this.skipWord = skipWord;

    }

    /**
     * Метод на вход принимает объект File, изначально сумма = 0
     * Нужно пройти по всем строкам файла, и если в строке стоит целое число,
     * то надо добавить это число к сумме
     * @param file - файл с данными
     * @return - целое число - сумма всех чисел из файла
     */
    public long countNumbers(File file) throws Exception {
        StringBuilder builder = new StringBuilder();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String currLine;
        int num;
        long sum=0;
        while ((currLine = br.readLine())!=null) {
            try {
                num = Integer.parseInt(currLine);
                sum += num;
            } catch (NumberFormatException e) {

            }


        }



        return sum;
    }


    /**
     * Метод на вход принимает объект File, изначально результат= ""
     * Нужно пройти по всем строкам файла, и если в строка не пустая и не число
     * то надо присоединить ее к результату через пробел
     * @param file - файл с данными
     * @return - результирующая строка
     */
    public String concatWords(File file) throws Exception {
        StringBuilder builder = new StringBuilder();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String currLine;
        int num;
        while ((currLine = br.readLine())!=null) {
            try {
                num = Integer.parseInt(currLine);

            } catch (NumberFormatException e) {
                if (!(currLine.equals(this.skipWord))) {
                    builder.append(currLine);
                    builder.append(" ");
                }

            }


        }
        String result = builder.toString();

        return result;
    }

    public static void main(String[] args) throws Exception {
        File src = new File("/home/chern/tehnotr/track18-spring/L2-objects/words.txt");
        CountWords cw = new CountWords("");
        System.out.println(cw.concatWords(src));

    }

}

