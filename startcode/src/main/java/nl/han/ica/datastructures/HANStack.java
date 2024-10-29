package nl.han.ica.datastructures;

public class HANStack<T> implements nl.han.ica.datastructures.IHANStack<T> {

    HANLinkedList<T> list = new HANLinkedList<>();

    @Override
    public void push(T value) {
        list.insert(0,value);
    }

    @Override
    public T pop() {
        T ElementToPop = list.getFirst();
        list.delete(0);
        return ElementToPop;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }
}

