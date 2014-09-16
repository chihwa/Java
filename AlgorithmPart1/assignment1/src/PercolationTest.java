import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PercolationTest {

    @Test
    public void singleCellFull() throws Exception {
        Percolation p = new Percolation(1);
        assertFalse(p.isFull(1, 1));
        assertFalse(p.isOpen(1, 1));
        assertFalse(p.percolates());
    }

    @Test
    public void singleCellOpen() throws Exception {
        Percolation p = new Percolation(1);
        p.open(1, 1);
        assertTrue(p.isOpen(1, 1));
        assertTrue(p.isFull(1, 1));
        assertTrue(p.percolates());
    }

    @Test
    public void twoBy() throws Exception {
        Percolation p = new Percolation(2);
        assertFalse(p.percolates());

        p.open(1, 1);
        assertTrue(p.isFull(1, 1));

        assertFalse( p.percolates());

        p.open(2, 2);
        assertFalse( p.percolates());
        assertTrue(p.isFull(1, 1));
        assertFalse(p.isFull(2, 2));

        p.open(1, 2);
        assertTrue(p.isFull(1, 1));
        assertTrue(p.isFull(1, 2));
        assertTrue(p.isFull(2, 2));

        assertTrue( p.percolates());
    }

    @Test
    public void backwash() throws Exception {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        p.open(2, 1);
        p.open(3, 1);
        p.open(3, 3);
        assertFalse(p.isFull(3, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWithBadSizeToCtor() throws Exception {
        new Percolation(0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsWithBadOpenIndex() throws Exception {
        Percolation p = new Percolation(5);
        p.open(6, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsWithBadIndexIsFull() throws Exception {
        Percolation p = new Percolation(1);
        p.isFull(2, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsWithBadIndexInOpen() throws Exception {
        Percolation p = new Percolation(1);
        p.open(2, 2);
    }
}