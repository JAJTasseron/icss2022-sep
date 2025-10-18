package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class HANLinkedListTest {

    // NOTE: These tests have been made with the help of ChatGPT to cover edge cases and ensure my code works as intended.

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

    @Test
    void insert_AtNegativeIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(-1, "NEGATIVE"));
    }

    @Test
    void insert_BeyondEnd_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("B");
        list.addFirst("A");

        assertThrows(IndexOutOfBoundsException.class, () -> list.insert(3, "TOO_FAR"));
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
    void delete_AtNegativeIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();

        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(-1));
    }

    @Test
    void delete_AtOverflowIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("A");
        list.addFirst("B");

        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(2));
    }

    @ParameterizedTest
    @CsvSource({
            "2, C, B",
            "1, C, A",
            "0, B, A"
    })
    void delete_Node_ShouldLinkSurroundingNodes(int posToDelete, String expectedFirstNode, String expectedSecondNode) {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("A");
        list.addFirst("B");
        list.addFirst("C");

        list.delete(posToDelete);

        assertEquals(2, list.getSize());
        assertEquals(expectedFirstNode, list.get(0));
        assertEquals(expectedSecondNode, list.get(1));
    }

    @Test
    void get_AtNegativeIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void get_AtOverflowIndex_ShouldThrowException() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("A");
        list.addFirst("B");

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    void get_Node_ShouldReturnContainedElement() {
        HANLinkedList<String> list = new HANLinkedList<>();
        list.addFirst("A");
        list.addFirst("B");
        list.addFirst("C");

        assertEquals("C", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("A", list.get(2));
    }

    @Test
    void getSize_OfChangingList_ShouldReturnCorrectSize() {
        HANLinkedList<String> list = new HANLinkedList<>();
        assertEquals(0, list.getSize());

        list.addFirst("A");
        assertEquals(1, list.getSize());

        list.addFirst("B");
        assertEquals(2, list.getSize());

        list.delete(0);
        assertEquals(1, list.getSize());

        list.clear();
        assertEquals(0, list.getSize());
    }
}
