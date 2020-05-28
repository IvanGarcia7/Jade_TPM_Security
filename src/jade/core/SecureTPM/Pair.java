package jade.core.SecureTPM;


import java.io.Serializable;


public class Pair<U, V> implements Serializable {

    public final U first;
    public final V second;


    public Pair(U first, V second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (!first.equals(pair.first))
            return false;
        return second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }


    public static <U, V> Pair <U, V> of(U a, V b)
    {
        return new Pair<>(a, b);
    }

    public U getKey() {
        return first;
    }

    public V getValue() {
        return second;
    }
}
