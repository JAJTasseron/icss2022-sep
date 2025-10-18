package nl.han.ica.datastructures;

public class HANLinkedList<T> implements nl.han.ica.datastructures.IHANLinkedList<T> {

    HANLinkNode<T> header = new HANLinkNode<>(null, null);

    @Override
    public void addFirst(T value) {
        HANLinkNode<T> newNode = new HANLinkNode<>(value, header.getNext());
        header.setNext(newNode);
    }

    @Override
    public void clear() {
        header.setNext(null);
    }

    @Override
    public void insert(int index, T value) throws RuntimeException {
        if(this.getSize()<index || index<0){
            throw new IndexOutOfBoundsException("Index is out of bounds.");
        }
        HANLinkNode<T> currentNode = header;
        for(int i = 0; i<index; i++){
            currentNode = currentNode.getNext();
        }
        HANLinkNode<T> newNode = new HANLinkNode<>(value, currentNode.getNext());
        currentNode.setNext(newNode);
    }

    @Override
    public void delete(int pos) throws RuntimeException {
        if(this.getSize()-1<pos || pos<0){
            throw new IndexOutOfBoundsException("Position is out of bounds.");
        }
        HANLinkNode<T> currentNode = header;
        for(int i = 0; i<pos;i++){
            currentNode = currentNode.getNext();
        }
        if(currentNode.getNext().getNext() == null){
            currentNode.setNext(null);
        } else {
            currentNode.setNext(currentNode.getNext().getNext());
        }
    }

    @Override
    public T get(int pos) throws RuntimeException{
        if(this.getSize()-1<pos || pos<0){
            throw new IndexOutOfBoundsException("Position is out of bounds.");
        }
        HANLinkNode<T> currentNode = header;
        for(int i = -1; i<pos;i++){
            currentNode = currentNode.getNext();
        }
        return currentNode.getElement();
    }

    @Override
    public void removeFirst() {
        header.setNext(header.getNext().getNext());
    }

    @Override
    public T getFirst() {
        return header.getNext().getElement();
    }

    @Override
    public int getSize() {
        HANLinkNode<T> currentNode = header;
        int nextCounter = 0;
        while(currentNode.getNext() != null){
            currentNode = currentNode.getNext();
            nextCounter++;
        }
        return nextCounter;
    }
}