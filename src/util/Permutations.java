package util;

import java.util.ArrayList;

public class Permutations<T> {
    public final ArrayList<ArrayList<T>> permutations;
    
    public Permutations(T[] elements) {
        this.permutations = new ArrayList<ArrayList<T>>(elements.length);
        this.permute(elements);
    }
    
    private void permute(T[] elements) {
        int n = elements.length;
        
        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }
        
        addList(elements);
    
        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swap(elements, i % 2 == 0 ?  0: indexes[i], i);
                addList(elements);
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }
    }
    
    private void swap(T[] input, int a, int b) {
        T tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }
    
    private void addList(T[] input) {
        ArrayList<T> toAdd = new ArrayList<>();
        
        for(int i = 0; i < input.length; i++) {
            toAdd.add(input[i]);
        }
        
        this.permutations.add(toAdd);
    }
    
    private void printAll() {
        for(ArrayList<T> permutation : this.permutations) {
            for(T element : permutation) System.out.print(element + " ");
            System.out.println();
        }
    }
}
