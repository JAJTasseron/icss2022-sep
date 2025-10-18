package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HANLinkedListTest {

    // NOTE: These tests have been made with the help of ChatGPT to cover edge cases and ensure my code works as intended.

    // ------------------------------------------------------------
    // Tests for addFirst
    // ------------------------------------------------------------

    @Test
    void addFirst_ShouldPlaceNewElementAtFront() {
        HANLinkedList<String> list = new HANLinkedList<>();

        list.addFirst("A");

        assertEquals("A", list.getFirst());
        assertEquals(1, list.getSize());
    }

    @Test
    void addFirst_ShouldLinkNewFirstToOldFirst() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("A");

        list.addFirst("B");

        assertEquals("B", list.getFirst());
        assertEquals("A", list.get(1));
        assertEquals(2, list.getSize());
    }

    // ------------------------------------------------------------
    // Tests for insert
    // ------------------------------------------------------------

    @Test
    void insert_AtNegativeIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        assertThrows(RuntimeException.class, () -> list.insert(-1, "NEGATIVE"));
    }

    @Test
    void insert_AtZero_ShouldInsertAtFront() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        list.insert(0, "X");

        assertEquals("X", list.get(0));
        assertEquals("A", list.get(1));
        assertEquals("B", list.get(2));
        assertEquals(3, list.getSize());
    }

    @Test
    void insert_AtValidEnd_ShouldAppendElement() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        list.insert(2, "C");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
        assertEquals(3, list.getSize());
    }

    @Test
    void insert_BeyondEnd_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        assertThrows(RuntimeException.class, () -> list.insert(3, "TOO_FAR"));
    }
}
