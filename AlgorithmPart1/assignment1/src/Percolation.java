public class Percolation {

    private static final  boolean OPEN = true;
    
    private int N;
    private boolean[][] grid;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF vuf;
    private int virtualTopId;
    private int virtualBottomId;

    public Percolation(int N) {
        if (N < 1)
            throw new IllegalArgumentException();
        // create N-by-N grid, with all sites blocked
        this.N = N;
        this.grid = new boolean[N][N];
        this.uf = new WeightedQuickUnionUF(N * N + 2);
        this.vuf = new WeightedQuickUnionUF(N * N + 2);
        this.virtualTopId = N * N;
        this.virtualBottomId = N * N + 1;
    }

    private int xyTo1D(int i, int j) {
        return j + i * N;
    }

    private void checkIndex(int i, int j) {
        if (i < 1 || i > N)
            throw new IndexOutOfBoundsException();
        if (j < 1 || j > N)
            throw new IndexOutOfBoundsException();
    }

    public void open(int i, int j) {
        // open site (row i, column j) if it is not already
        int row = i;
        int col = j;

        checkIndex(row--, col--);

        int cur = this.xyTo1D(row, col);

        if (row == 0) {
            uf.union(cur, virtualTopId);
            vuf.union(cur, virtualTopId);
        }  
        
        if (row == N - 1) {
            uf.union(cur, virtualBottomId);
        }

        grid[row][col] |= OPEN;
        

        if ((row != 0) && (grid[row-1][col] == OPEN)) {
            uf.union(cur, this.xyTo1D(row-1, col));
            vuf.union(cur, this.xyTo1D(row-1, col));
        }
        if ((row != N-1) && (grid[row+1][col] == OPEN)) {
            uf.union(cur, this.xyTo1D(row+1, col));
            vuf.union(cur, this.xyTo1D(row+1, col));
        }
        if ((col != 0) && (grid[row][col-1] == OPEN)) {
            uf.union(cur, this.xyTo1D(row, col-1));
            vuf.union(cur, this.xyTo1D(row, col-1));
        }
        if ((col != N-1) && (grid[row][col+1] == OPEN)) {
            uf.union(cur, this.xyTo1D(row, col+1));
            vuf.union(cur, this.xyTo1D(row, col+1));
        }

    }

    public boolean isOpen(int i, int j) {
        // is site (row i, column j) open?
        int row = i;
        int col = j;
        checkIndex(row--, col--);
        return grid[row][col];
    }

    public boolean isFull(int i, int j) {
        // is site (row i, column j) full?
        int row = i;
        int col = j;
        checkIndex(row--, col--);
        return vuf.connected(xyTo1D(row, col), virtualTopId) && isOpen(i, j);
    }

    public boolean percolates() {
        // does the system percolate?
        return uf.connected(virtualTopId, virtualBottomId);
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(1);
        p.open(1, 1);
        System.out.println(p.isOpen(1, 1));
        System.out.println(p.percolates());
    }

}
