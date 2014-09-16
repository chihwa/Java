
public class PercolationStats {

    private int N;
    private int T;
    private double[] result;

    public PercolationStats(int N, int T) {
        // perform T independent computational experiments on an N-by-N grid
        if (N <= 0 || T <= 0)
            throw new IllegalArgumentException();

        this.N = N;
        this.T = T;
        this.result = new double[T];

        for (int i = 0; i < T; i++) {
            double count = 0d;
            Percolation p = new Percolation(N);
            while (!p.percolates()) {
                openNext(p);
                count++;
            }
            result[i] = count / (double) (N * N);
        }
    }

    private void openNext(Percolation p) {

        int row = 0;
        int col = 0;
        do {
            row = 1 + StdRandom.uniform(N);
            col = 1 + StdRandom.uniform(N);
            if (!p.isFull(row, col) && !p.isOpen(row, col))
                break;
        } while (true);
        p.open(row, col);
    }

    public double mean() {
        // sample mean of percolation threshold
        return StdStats.mean(result);
    }

    public double stddev() {
        // sample standard deviation of percolation threshold
        return StdStats.stddev(result);
    }

    public double confidenceLo() {
        // returns lower bound of the 95% confidence interval
        return mean() - (1.96 * stddev() / Math.sqrt(T));
    }

    public double confidenceHi() {
        // returns upper bound of the 95% confidence interval
        return mean() + (1.96 * stddev() / Math.sqrt(T));
    }

    public static void main(String[] args) {
        // test client, described below
        PercolationStats ps = new PercolationStats(100, 50);
        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println("95% confidence interval = " + ps.confidenceLo()
                + ", " + ps.confidenceHi());
    }

}
