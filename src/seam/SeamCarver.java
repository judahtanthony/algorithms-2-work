import edu.princeton.cs.algs4.Picture;
// import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private int pixelsW;
    private int pixelsH;
    private int[][] pixels;
    private boolean transposed = false;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        // The data type may not mutate the Picture argument to the constructor.
        pixelsW = picture.width();
        pixelsH = picture.height();
        if (pixelsW == 0 || pixelsH == 0) {
            throw new IllegalArgumentException();
        }
        pixels = new int[pixelsH][pixelsW];
        for (int x = 0; x < pixelsW; ++x) {
            for (int y = 0; y < pixelsH; ++y) {
                pixels[y][x] = picture.getRGB(x, y);
            }
        }
    }
    // current picture
    public Picture picture() {
        if (transposed) {
            transpose();
        }
        Picture p = new Picture(pixelsW, pixelsH);
        for (int x = 0; x < pixelsW; ++x) {
            for (int y = 0; y < pixelsH; ++y) {
                p.setRGB(x, y, pixels[y][x]);
            }
        }
        return p;
    }
    // width of current picture
    public     int width() {
        return !transposed ? pixelsW : pixelsH;
    }
    // height of current picture
    public     int height() {
        return !transposed ? pixelsH : pixelsW;
    }
    // energy of pixel at column x and row y
    public  double energy(int x, int y) {
        if (transposed) {
            int oldX = x;
            x = y;
            y = oldX;
        }
        if (x < 0 || x >= pixelsW || y < 0 || y >= pixelsH) {
            throw new IllegalArgumentException();
        }
        return rowEnergy(x, y);
    }
    // sequence of indices for horizontal seam
    public   int[] findHorizontalSeam() {
        if (!transposed) {
            transpose();
        }
        return findSeam();
    }
    // sequence of indices for vertical seam
    public    int[] findVerticalSeam() {
        if (transposed) {
            transpose();
        }
        return findSeam();
    }
    // remove horizontal seam from current picture
    public    void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width() || height() <= 1) {
            throw new IllegalArgumentException();
        }
        if (!transposed) {
            transpose();
        }
        removeSeam(seam);
    }
    // remove vertical seam from current picture
    public    void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height() || width() <= 1) {
            throw new IllegalArgumentException();
        }
        if (transposed) {
            transpose();
        }
        removeSeam(seam);
    }
    
    
    private    void removeSeam(int[] seam) {
        for (int y = 0; y < pixelsH; ++y) {
            if (seam[y] < 0 || seam[y] >= pixelsW) {
                throw new IllegalArgumentException();
            }
            if (y > 0) {
                if (seam[y] - seam[y-1] < -1 || seam[y] - seam[y-1] > 1) {
                    throw new IllegalArgumentException();
                }
            }
            if (seam[y] == pixelsW-1) {
                continue;
            }
            System.arraycopy(pixels[y], seam[y] + 1, pixels[y], seam[y], pixelsW - (seam[y] + 1));
        }
        pixelsW -= 1;
    }
    private    int[] findSeam() {
        int[] path = new int[pixelsH];
        if (pixelsW <= 2 || pixelsH <= 2) {
            for (int y = 0; y < pixelsH; y++) {
                path[y] = pixelsW / 2;
            }
            return path;
        }
        int pixelsWH = pixelsW * pixelsH;
        double[] energyTo = new double[pixelsWH];
        double[] energies = new double[pixelsWH];
        int[] pathTo = new int[pixelsWH];
        int i = 0;
        int end = 0;
        double minEnergy = Double.POSITIVE_INFINITY;
        
        for (i = 0; i < pixelsWH; ++i) {
            energyTo[i] = i < pixelsW ? 0.0 : Double.POSITIVE_INFINITY;
            energies[i] = rowEnergy(toX(i), toY(i));
        }
        
        // get topo order
        for (i = 0; i < pixelsWH; ++i) {
            int x = toX(i);
            int y = toY(i);
            // Next row.
            if (y < pixelsH-1) {
                ++y;
                relax(i, toIndex(x, y), pathTo, energyTo, energies);
                if (x > 0) {
                    relax(i, toIndex(x-1, y), pathTo, energyTo, energies);
                }
                if (x < pixelsW-1) {
                    relax(i, toIndex(x+1, y), pathTo, energyTo, energies);
                }
            }
            else {
                if (minEnergy > energyTo[i]) {
                    minEnergy = energyTo[i];
                    end = i;
                }
            }
        }
        
        i = end;
        while (true) {
            int x = toX(i);
            int y = toY(i);
            path[y] = x;
            if (y <= 0) {
                break;
            }
            i = pathTo[i];
        }
        // A little normalization.
        path[0] = path[1];
        path[pixelsH-1] = path[pixelsH-2];
        
        return path;
    }
    private void relax(int from, int to, int[] pathTo, double[] energyTo, double[] energies) {
        if (energyTo[to] > energyTo[from] + energies[to]) {
            energyTo[to] = energyTo[from] + energies[to];
            pathTo[to] = from;
        }
    }
    private  double rowEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == (pixelsW-1) || y == (pixelsH-1)) {
            return 1000.0;
        }
        int xp = pixels[y][x-1];
        int xn = pixels[y][x+1];
        int yp = pixels[y-1][x];
        int yn = pixels[y+1][x];
        
        return Math.sqrt(diffs(xp, xn) + diffs(yp, yn));
    }
    private double diffs(int prev, int next) {
        int red = ((prev >> 16) & 0xFF) - ((next >> 16) & 0xFF);
        int green = ((prev >> 8) & 0xFF) - ((next >> 8) & 0xFF);
        int blue = (prev & 0xFF) - (next & 0xFF);
        
        return red*red + green*green + blue*blue;
    }
    private void transpose() {
        int[][] newPixels = new int[pixelsW][pixelsH];
        for (int x = 0; x < pixelsW; ++x) {
            for (int y = 0; y < pixelsH; ++y) {
                newPixels[x][y] = pixels[y][x];
            }
        }
        
        int oldW = pixelsW;
        pixelsW = pixelsH;
        pixelsH = oldW;
        pixels = newPixels;
        transposed = !transposed;
    }
    private int toIndex(int x, int y) {
        return x + y * pixelsW;
    }
    private int toX(int i) {
        return i % pixelsW;
    }
    private int toY(int i) {
        return i / pixelsW;
    }
}
