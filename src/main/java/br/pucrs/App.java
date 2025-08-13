package br.pucrs;

import java.util.Arrays;
import java.util.Random;

public class App {
    
    private static long mergeSortIterations = 0;
    private static long maxVal1Iterations = 0;
    private static long maxVal2Iterations = 0;
    private static long multiplyIterations = 0;
    
    public static void main(String[] args) {
        System.out.println("=== EXERCÍCIO DE DIVISÃO E CONQUISTA ===\n");
        
        int[] sizes = {32, 2048, 1048576};
        
        System.out.println("Algoritmo               | Tamanho | Iterações   | Tempo (ms)  | Resultado");
        System.out.println("------------------------|---------|-------------|-------------|----------");
        
        for (int i = 0; i < sizes.length; i++) {
            testAlgorithms(sizes[i], i == sizes.length - 1);
        }
        
        testMultiplication();
    }
    
    private static void testAlgorithms(int size, boolean isLast) {
        long[] array = generateRandomArray(size);
        long[] arrayCopy1 = Arrays.copyOf(array, array.length);
        long[] arrayCopy2 = Arrays.copyOf(array, array.length);
        
        mergeSortIterations = 0;
        long startTime = System.nanoTime();
        long[] sortedArray = mergeSort(arrayCopy1);
        long endTime = System.nanoTime();
        double mergeSortTime = (endTime - startTime) / 1_000_000.0;
        
        System.out.printf("Merge Sort              | %7d | %11d | %11.2f | Ordenado\n", 
                         size, mergeSortIterations, mergeSortTime);
        
        maxVal1Iterations = 0;
        startTime = System.nanoTime();
        long max1 = maxVal1(arrayCopy2, size);
        endTime = System.nanoTime();
        double maxVal1Time = (endTime - startTime) / 1_000_000.0;
        
        System.out.printf("MaxVal1 (Iterativo)     | %7d | %11d | %11.2f | %d\n", 
                         size, maxVal1Iterations, maxVal1Time, max1);
        
        maxVal2Iterations = 0;
        startTime = System.nanoTime();
        long max2 = maxVal2(array, 0, size - 1);
        endTime = System.nanoTime();
        double maxVal2Time = (endTime - startTime) / 1_000_000.0;
        
        System.out.printf("MaxVal2 (Div&Conq)      | %7d | %11d | %11.2f | %d\n", 
                         size, maxVal2Iterations, maxVal2Time, max2);
        
        if (!isLast) {
            System.out.println("------------------------|---------|-------------|-------------|----------");
        }
    }
    
    public static long[] mergeSort(long[] array) {
        mergeSortIterations++;
        
        if (array.length <= 1) {
            return array;
        }
        
        int mid = array.length / 2;
        long[] left = Arrays.copyOfRange(array, 0, mid);
        long[] right = Arrays.copyOfRange(array, mid, array.length);
        
        left = mergeSort(left);
        right = mergeSort(right);
        
        return merge(left, right);
    }
    
    private static long[] merge(long[] left, long[] right) {
        long[] result = new long[left.length + right.length];
        int i = 0, j = 0, k = 0;
        
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[j++];
            }
        }
        
        while (i < left.length) {
            result[k++] = left[i++];
        }
        
        while (j < right.length) {
            result[k++] = right[j++];
        }
        
        return result;
    }
    
    public static long maxVal1(long[] A, int n) {
        maxVal1Iterations++;
        long max = A[0];
        for (int i = 1; i < n; i++) {
            maxVal1Iterations++;
            if (A[i] > max) {
                max = A[i];
            }
        }
        return max;
    }
    
    public static long maxVal2(long[] A, int init, int end) {
        maxVal2Iterations++;
        
        if (end - init <= 1) {
            return Math.max(A[init], A[end]);
        } else {
            int m = (init + end) / 2;
            long v1 = maxVal2(A, init, m);
            long v2 = maxVal2(A, m + 1, end);
            return Math.max(v1, v2);
        }
    }
    
    public static long multiply(long x, long y, int n) {
        multiplyIterations++;
        
        if (n == 1) {
            return x * y;
        } else {
            int m = (int) Math.ceil(n / 2.0);
            long powerOf2m = 1L << m;         // 2^m shiftando bites
            long powerOf4m = 1L << (2 * m);   // 2^(2m)
            
            long a = x >> m;                  // x / 2^m
            long b = x & ((1L << m) - 1);     // x mod 2^m usando mask de bits
            long c = y >> m;                  // y / 2^m
            long d = y & ((1L << m) - 1);     // y mod 2^m
            
            long e = multiply(a, c, m);
            long f = multiply(b, d, m);
            long g = multiply(b, c, m);
            long h = multiply(a, d, m);
            
            return powerOf4m * e + powerOf2m * (g + h) + f;
        }
    }
    
    private static void testMultiplication() {
        System.out.println("\n=== MULTIPLICAÇÃO INTEIRA ===");
        System.out.println("Operação        | Bits | Iterações | Tempo (ns) | Resultado");
        System.out.println("----------------|------|-----------|------------|----------");
        
        int[] bitSizes = {4, 16, 64};
        Random random = new Random();
        
        for (int i = 0; i < bitSizes.length; i++) {
            int bits = bitSizes[i];
            long maxValue = (1L << bits) - 1;        // mask p/ limitar aos bits req
            long x = random.nextLong() & maxValue;
            long y = random.nextLong() & maxValue;
            
            if (bits == 4) {
                x = 13;
                y = 11;
            }
            
            multiplyIterations = 0;
            long startTime = System.nanoTime();
            long result = multiply(x, y, bits);
            long endTime = System.nanoTime();
            long multiplyTime = endTime - startTime;
            
            System.out.printf("%d × %d     | %4d | %9d | %10d | %d\n", 
                             x, y, bits, multiplyIterations, multiplyTime, result);
        }
    }
    
    private static long[] generateRandomArray(int size) {
        Random random = new Random();
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextLong() % 1000000;
        }
        return array;
    }
}