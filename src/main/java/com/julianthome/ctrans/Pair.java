package com.julianthome.ctrans;

public class Pair<T,K> {

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    private T first;
    private K second;


    public T getFirst() { return first; }
    public K getSecond() { return second; }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Pair))
            return false;

        return this.first.equals(((Pair) o).first) && this.second.equals((
                (Pair) o).second);
    }
}
