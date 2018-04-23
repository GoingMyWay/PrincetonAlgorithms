import edu.princeton.cs.algs4.Picture;


public class SeamCarver {
    private int[][] pic;
    private double[][] distTo;
    private int[][] edgeTo;
    private double[][] energy;
    private static final double DEFAULT_ENERGY_VAL = 1000.0;

    // TODO
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (null == picture) throw new java.lang.IllegalArgumentException("null pointer");

        energy = new double[picture.width()][picture.height()];
        pic = new int[picture.width()][picture.height()];
        // init pic
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                pic[i][j] = picture.getRGB(i, j);
            }
        }
        // calculate energy
        for (int i = 0; i < picture.width(); i++) {
            for (int j = 0; j < picture.height(); j++) {
                energy[i][j] = energy(i, j);
            }
        }
    }

    // TODO
    // current picture
    public Picture picture() {
        Picture newPic = new Picture(width(), height());
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                newPic.setRGB(i, j, pic[i][j]);
            }
        }
        return newPic;
    }

    // TODO
    // width of current picture
    public int width() {
        return pic.length;
    }

    // TODO
    // height of current picture
    public int height() {
        return pic[0].length;
    }

    // TODO
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if ((x < 0 || x > width()-1) || (y < 0 || y > height()-1))
            throw new java.lang.IllegalArgumentException("x or y is outside its prescribed range");

        double v;
        // TODO calcuate energy
        if ((width()-1 == x || 0 == x) || (height()-1 == y) || (0 == y)) v = DEFAULT_ENERGY_VAL;
        else {
            int gradientX = calGradient(pic[x-1][y], pic[x+1][y]);
            int gradientY = calGradient(pic[x][y-1], pic[x][y+1]);
            v = Math.sqrt(gradientX+gradientY);
        }
        return v;
    }

    // TODO
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // TODO transpose the picture
        transposePicture();
        // TODO get result
        int[] result = findVerticalSeam();
        // TODO re transpose the picture
        transposePicture();
        return result;
    }

    // TODO
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        distTo = new double[width()][height()];
        edgeTo = new int[width()][height()];

        for (int r = 0; r < height(); r++) {
            for (int c = 0; c < width(); c++) {
                if (0 == r) distTo[c][r] = DEFAULT_ENERGY_VAL;
                else distTo[c][r] = Double.POSITIVE_INFINITY;
            }
        }

        // Topological sort
        // TODO since the picture are sorted, loop from top row to bottom row
        for (int r = 0; r < height()-1; r++) {
            for (int c = 0; c < width(); c++)
                relaxEdge(c, r);
        }

        int[] result = new int[height()];
        double minE = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 0; i < width(); i++) {
            if (distTo[i][height()-1] < minE) {
                minE = distTo[i][height()-1];
                minIndex = i;
            }
        }
        // get the seam
        result[height()-1] = minIndex; // init the last column
        // append the index of column values
        for (int j = height()-2; j >= 0; j--) {
            result[j] = edgeTo[result[j+1]][j+1];
        }
        // clear memory of distTo and edgeTo and the GC will automatically release memory
        distTo = null;
        edgeTo = null;
        return result;
    }

    // TODO
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (null == seam) throw new java.lang.IllegalArgumentException("null pointer");
        if (seam.length != width()) throw new java.lang.IllegalArgumentException("seam.length != width()");
        for (int v : seam) {
            if (v < 0 || v > height()-1) throw new java.lang.IllegalArgumentException("out of height range");
        }
        if (height() <= 1) throw new java.lang.IllegalArgumentException("height <= 1");
        // check seam value
        for (int i = 0; i < width(); i++) {
            if (i > 0 && Math.abs(seam[i]-seam[i-1]) > 1)
                throw new java.lang.IllegalArgumentException("two adjacent entries differ by more than 1");
        }
        // TODO remove horizontal seam
        transposePicture();
        removeSeam(seam);
        transposePicture();
    }

    // TODO
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (null == seam) throw new java.lang.IllegalArgumentException("null pointer");
        if (seam.length != height()) throw new java.lang.IllegalArgumentException("seam.length != height()");
        for (int v : seam) {
            if (v < 0 || v > width()-1) throw new java.lang.IllegalArgumentException("out of width range");
        }
        if (width() <= 1) throw new java.lang.IllegalArgumentException("width <= 1");
        // check seam value
        for (int i = 0; i < height(); i++) {
            if (i > 0 && Math.abs(seam[i]-seam[i-1]) > 1)
                throw new java.lang.IllegalArgumentException("two adjacent entries differ by more than 1");
        }
        // TODO remove seam
        removeSeam(seam);
    }

    private void relaxEdge(int c, int r) {
        // top-to-down
        if (distTo[c][r+1] > distTo[c][r] + energy[c][r+1]) {
            distTo[c][r+1] = distTo[c][r] + energy[c][r+1];
            edgeTo[c][r+1] = c;
        }
        // top-to-left
        if ((c-1) >= 0 && distTo[c-1][r+1] > distTo[c][r] + energy[c-1][r+1]) {
            distTo[c-1][r+1] = distTo[c][r] + energy[c-1][r+1];
            edgeTo[c-1][r+1] = c;
        }
        // top-to-right
        if ((c+1) < width() && distTo[c+1][r+1] > distTo[c][r] + energy[c+1][r+1]) {
            distTo[c+1][r+1] = distTo[c][r] + energy[c+1][r+1];
            edgeTo[c+1][r+1] = c;
        }
    }

    private void transposePicture() {
        int[][] newPic = new int[height()][width()];
        double[][] enArr = new double[height()][width()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                newPic[j][i] = pic[i][j];
                enArr[j][i] = energy[i][j];
            }
        }
        energy = enArr;
        pic = newPic;
    }

    private void removeSeam(int[] seam) {
        int[][] newPic = new int[width()-1][height()];
        for (int r = 0, i = 0; r < height(); r++, i++) {
            for (int c = 0, j = 0; c < width(); c++, j++) {
                if (seam[r] == c) {
                    j--;
                    continue;
                }
                newPic[j][i] = pic[c][r];
            }
        }
        pic = newPic;

        energy = new double[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) energy[i][j] = energy(i, j);
        }
    }

    private int calGradient(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >>  8) & 0xFF;
        int b1 = (rgb1 >>  0) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >>  8) & 0xFF;
        int b2 = (rgb2 >>  0) & 0xFF;
        return (r1-r2)*(r1-r2) + (g1-g2)*(g1-g2) + (b1-b2)*(b1-b2);
    }

    public static void main(String[] args) {
        Picture picture = new Picture("seam/6x5.png");
        SeamCarver sc = new SeamCarver(picture);
        int[] res = sc.findHorizontalSeam();
        sc.removeHorizontalSeam(res);
    }
}
