import java.awt.Color;

public class SeamCarver {
    //private Picture ourPicture;
    private byte[][] redMatrix;
    private byte[][] greenMatrix;
    private byte[][] blueMatrix;
    //private double[][] energyMatrix;
    private int W;
    private int H;
    
    public SeamCarver(Picture picture) {
        // create a seam carver object based on the given picture
        //ourPicture = picture;
        W = picture.width();
        H = picture.height();
        //colorMatrix = new Color[H][W];
        redMatrix = new byte[H][W];
        greenMatrix = new byte[H][W];
        blueMatrix = new byte[H][W];
        //energyMatrix = new double[W][H];
        for (int j = 0; j < H; j++) {
            for (int i = 0; i < W; i++) {
                Color color = picture.get(i, j);
                redMatrix[j][i] = (byte) color.getRed();
                greenMatrix[j][i] = (byte) color.getGreen();
                blueMatrix[j][i] = (byte) color.getBlue();
            }
        }
       
    }
   
    public Picture picture() {
        // current picture
        Picture ourPicture = new Picture(W, H);
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                Color color = new Color(redMatrix[j][i] & 0xff,
                                        greenMatrix[j][i] & 0xff,
                                        blueMatrix[j][i] & 0xff);
                ourPicture.set(i, j, color);
            }
        }
        return ourPicture;
    }
    
    public int width() {
        // width of current picture
        return this.W;
    }
    
    public int height() {
        // height of current picture
        return this.H;
    }
    
    public double energy(int x, int y) {
        if (x < 0 || x >= W || y < 0 || y >= H)
            throw new java.lang.IndexOutOfBoundsException();
        
        int i = y;
        int j = x;
        // energy of pixel at column x and row y
        if (x == 0 || x == W-1 || y == 0 || y == H-1)
            return 195075;
        else
            return xGradient(i, j) + yGradient(i, j);
    }
    
    private double xGradient(int x, int y) {
        int red = Math.abs((redMatrix[x-1][y] & 0xff)
                               - (redMatrix[x+1][y] & 0xff));
        int green = Math.abs((greenMatrix[x-1][y] & 0xff)
                                 - (greenMatrix[x+1][y] & 0xff));
        int blue = Math.abs((blueMatrix[x-1][y] & 0xff)
                                - (blueMatrix[x+1][y] & 0xff));
        return red * red + green * green + blue * blue;
    }
    
    private double yGradient(int x, int y) {
        int red = Math.abs((redMatrix[x][y-1] & 0xff)
                               - (redMatrix[x][y+1] & 0xff));
        int green = Math.abs((greenMatrix[x][y-1] & 0xff)
                                 - (greenMatrix[x][y+1] & 0xff));
        int blue = Math.abs((blueMatrix[x][y-1] & 0xff)
                                - (blueMatrix[x][y+1] & 0xff));
        return red * red + green * green + blue * blue;
    }
    
    public int[] findHorizontalSeam() {
        // sequence of indices for horizontal seam       
        double[][] distTo = new double[W][H];
        int[][] edgeTo = new int[W][H];
        double[][] energyMatrix = new double[W][H];
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
        
        for (int i = 0; i < H; i++) {
            distTo[0][i] = 195075;
        }
        for (int i = 1; i < W; i++) {
            for (int j = 0; j < H; j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        
        for (int i = 0; i < W-1; i++) {
            for (int j = 0; j < H; j++) {
                if (distTo[i+1][j] > distTo[i][j] + energyMatrix[i+1][j]) {
                        distTo[i+1][j] = distTo[i][j] + energyMatrix[i+1][j];
                        edgeTo[i+1][j] = j;
                    }
                
                if (j == 0) {
                    
                    if (distTo[i+1][j+1] > distTo[i][j] + energyMatrix[i+1][j+1]) {
                        distTo[i+1][j+1] = distTo[i][j] + energyMatrix[i+1][j+1];
                        edgeTo[i+1][j+1] = j;
                    }
                }
                else if (j == H-1) {
                   
                    if (distTo[i+1][j-1] > distTo[i][j] + energyMatrix[i+1][j-1]) {
                        distTo[i+1][j-1] = distTo[i][j] + energyMatrix[i+1][j-1];
                        edgeTo[i+1][j-1] = j;
                    }
                }
                else {
                    if (distTo[i+1][j+1] > distTo[i][j] + energyMatrix[i+1][j+1]) {
                        distTo[i+1][j+1] = distTo[i][j] + energyMatrix[i+1][j+1];
                        edgeTo[i+1][j+1] = j;
                    }
                    if (distTo[i+1][j-1] > distTo[i][j] + energyMatrix[i+1][j-1]) {
                        distTo[i+1][j-1] = distTo[i][j] + energyMatrix[i+1][j-1];
                        edgeTo[i+1][j-1] = j;
                    }
                }
            }
        }
        int idx = maxHorizontalIdx(distTo);
        int[] seam = horizontalSeam(idx, edgeTo);
        return seam;
    }
    
    private int maxHorizontalIdx(double[][] distTo) {
        int idx = -1;
        double value = Double.POSITIVE_INFINITY;
        for (int i = 0; i < H; i++) {
            if (distTo[W-1][i] < value) {
                value = distTo[W-1][i];
                idx = i;
            }
        }
        return idx;
    }
    
    private int[] horizontalSeam(int idx, int[][] edgeTo) {
        int[] seam = new int[W];
        seam[W-1] = idx;
        int place = idx;
        for (int i = W-1; i > 0; i--) {
            place = edgeTo[i][place];
            seam[i-1] = place;
        }
        return seam;
    }
        
    public int[] findVerticalSeam() {
        // sequence of indices for vertical seam
        double[][] distTo = new double[W][H];
        int[][] edgeTo = new int[W][H];
        double[][] energyMatrix = new double[W][H];
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                energyMatrix[i][j] = energy(i, j);
            }
        }
        
        for (int i = 0; i < W; i++) {
            distTo[i][0] = 195075;
        }
        for (int i = 0; i < W; i++) {
            for (int j = 1; j < H; j++) {
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        
        for (int j = 0; j < H-1; j++) {
            for (int i = 0; i < W; i++) {
                if (distTo[i][j+1] > distTo[i][j] + energyMatrix[i][j+1]) {
                        distTo[i][j+1] = distTo[i][j] + energyMatrix[i][j+1];
                        edgeTo[i][j+1] = i;
                    }
                
                if (i == 0) {
                    
                    if (distTo[i+1][j+1] > distTo[i][j] + energyMatrix[i+1][j+1]) {
                        distTo[i+1][j+1] = distTo[i][j] + energyMatrix[i+1][j+1];
                        edgeTo[i+1][j+1] = i;
                    }
                }
                else if (i == W-1) {
                   
                    if (distTo[i-1][j+1] > distTo[i][j] + energyMatrix[i-1][j+1]) {
                        distTo[i-1][j+1] = distTo[i][j] + energyMatrix[i-1][j+1];
                        edgeTo[i-1][j+1] = i;
                    }
                }
                else {
                    if (distTo[i+1][j+1] > distTo[i][j] + energyMatrix[i+1][j+1]) {
                        distTo[i+1][j+1] = distTo[i][j] + energyMatrix[i+1][j+1];
                        edgeTo[i+1][j+1] = i;
                    }
                     if (distTo[i-1][j+1] > distTo[i][j] + energyMatrix[i-1][j+1]) {
                        distTo[i-1][j+1] = distTo[i][j] + energyMatrix[i-1][j+1];
                        edgeTo[i-1][j+1] = i;
                    }
                }
            }
        }
        int idx = maxVerticalIdx(distTo);
        int[] seam = verticalSeam(idx, edgeTo);
        return seam;
    }
    
    private int maxVerticalIdx(double[][] distTo) {
        int idx = -1;
        double value = Double.POSITIVE_INFINITY;
        for (int i = 0; i < W; i++) {
            if (distTo[i][H-1] < value) {
                value = distTo[i][H-1];
                idx = i;
            }
        }
        return idx;
    }
    
    private int[] verticalSeam(int idx, int[][] edgeTo) {
        int[] seam = new int[H];
        seam[H-1] = idx;
        int place = idx;
        for (int i = H-1; i > 0; i--) {
            place = edgeTo[place][i];
            seam[i-1] = place;
        }
        return seam;
    }
                    
         
    private boolean validSeam(int[] seam, int which) {
        // which == 0 is vertical, which == 1 is horizontal
        int l = -1;
        int r = -1;
        if (which == 0) {
            l = this.H;
            r = this.W;
        }
        else {
            l = this.W;
            r = this.H;
        }
        
        if (seam.length != l)
            return false;
        for (int i = 0; i < l; i++) {
            int idx = seam[i];
            int lIdx = idx;
            int rIdx = idx;
            if (idx < 0 || idx > r)
                return false;
            if (i != 0)
                lIdx = seam[i-1];
            if (i != l-1)
                rIdx = seam[i+1];
            if (Math.abs(idx-lIdx) > 1 || Math.abs(idx-rIdx) > 1)
                return false;               
        }
        return true;
    }
    
    public void removeHorizontalSeam(int[] seam) {
        // remove horizontal seam from current picture
        if (seam == null)
            throw new java.lang.NullPointerException();
        if (!validSeam(seam, 1))
            throw new java.lang.IllegalArgumentException();
        if (this.W <= 1 || this.H <= 1)
            throw new java.lang.IllegalArgumentException();
            
        //Color[][] newColorMatrix = new Color[H-1][W];
        byte[][] newRedMatrix = new byte[H-1][W];
        byte[][] newGreenMatrix = new byte[H-1][W];
        byte[][] newBlueMatrix = new byte[H-1][W];
        int k = 0;
        for (int i = 0; i < W; i++) {
            for (int j = 0; j < H; j++) {
                if (j != seam[i]) {
                    //newEnergyMatrix[i][k] = energyMatrix[i][j];
                    //newColorMatrix[k][i] = colorMatrix[j][i];
                    newRedMatrix[k][i] = redMatrix[j][i];
                    newGreenMatrix[k][i] = greenMatrix[j][i];
                    newBlueMatrix[k][i] = blueMatrix[j][i];
                    k++;
                }
            }
            k = 0;
        }
        //energyMatrix = newEnergyMatrix;
        //colorMatrix = newColorMatrix;
        redMatrix = newRedMatrix;
        greenMatrix = newGreenMatrix;
        blueMatrix = newBlueMatrix;
        this.H--;
    }
    
    
    public void removeVerticalSeam(int[] seam) {
        // remove vertical seam from current picture
        if (seam == null)
            throw new java.lang.NullPointerException();
        if (!validSeam(seam, 0))
            throw new java.lang.IllegalArgumentException();        
        if (this.W <= 1 || this.H <= 1)
            throw new java.lang.IllegalArgumentException();      
        
        //Color[][] newColorMatrix = new Color[H][W-1];
        byte[][] newRedMatrix = new byte[H][W-1];
        byte[][] newGreenMatrix = new byte[H][W-1];
        byte[][] newBlueMatrix = new byte[H][W-1];
        int k = 0;
        for (int j = 0; j < H; j++) {
            for (int i = 0; i < W; i++) {
                if (i != seam[j]) {
                    //newEnergyMatrix[k][j] = energyMatrix[i][j];
                    //newColorMatrix[j][k] = colorMatrix[j][i];
                    newRedMatrix[j][k] = redMatrix[j][i];
                    newGreenMatrix[j][k] = greenMatrix[j][i];
                    newBlueMatrix[j][k] = blueMatrix[j][i];
                    k++;
                }
            }
            k = 0;
        }
        //energyMatrix = newEnergyMatrix;
        //colorMatrix = newColorMatrix;
        redMatrix = newRedMatrix;
        greenMatrix = newGreenMatrix;
        blueMatrix = newBlueMatrix;
        
        this.W--;
    }
}