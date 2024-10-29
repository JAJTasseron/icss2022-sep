package nl.han.ica.datastructures;

public class HANLinkedList<T> implements nl.han.ica.datastructures.IHANLinkedList<T> {

    HANLinkNode<T> header = new HANLinkNode<T>(null, null);

    @Override
    public void addFirst(T value) {
        HANLinkNode<T> newNode = new HANLinkNode<T>(value, header.getNext());
        header.setNext(newNode);
    }

    @Override
    public void clear() {
        header.setNext(null);
    }

    @Override
    public void insert(int index, T value) throws RuntimeException {
//        if(this.getSize()>index+1){
//            throw new RuntimeException("Index is out of bounds.");
//        }
        HANLinkNode<T> currentNode = header;
        for(int i = 0; i<index-1;i++){
            currentNode = currentNode.getNext();
        }
        HANLinkNode<T> newNode = new HANLinkNode<T>(value, currentNode.getNext());
        currentNode.setNext(newNode);
    }

    @Override
    public void delete(int pos) throws RuntimeException {
//        if(this.getSize()-1>pos){
//            throw new RuntimeException("Position is out of bounds.");
//        }
        HANLinkNode<T> currentNode = header;
        for(int i = 0; i<pos-1;i++){
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
        if(this.getSize()<=pos){
            throw new RuntimeException("Position is out of bounds.");
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