package nl.han.ica.datastructures;

class HANLinkNode<T>{
    T element;
    HANLinkNode<T> next;

    public HANLinkNode(T element, HANLinkNode<T> next) {
        this.element = element;
        this.next = next;
    }

    public T getElement() {
        return element;
    }
    public void setElement(T element) {
        this.element = element;
    }
    public HANLinkNode<T> getNext() {
        return next;
    }
    public void setNext(HANLinkNode<T> next) {
        this.next = next;
    }
}