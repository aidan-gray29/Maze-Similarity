//Aidan Gray

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

class mTriple implements Comparable<mTriple>{
    int maze_1, maze_2, longSubLen;
    /**
     * Class to contain maze pair subsequence comparisons with the relevant maze pairs
     * @param m1 : Implicit maze
     * @param m2 : Explicit maze
     * @param longestSequence : Length of the longest common subsequence shared by both maze paths
     */
    mTriple(int m1, int m2, int longestSequence){
        maze_1 = m1;
        maze_2 = m2;
        longSubLen = longestSequence;
    }

    @Override
    public int compareTo(mTriple o){
        return Integer.compare(this.longSubLen, o.longSubLen);
    }
}

class SPC{
    //visitation counts; 2 = done|wall, 1 = visited once, 0 = visited never
    int nV, sV, wV, eV;

    /**
     * Constructs a space given a 4 character length substring of N S W E format
     * @param str substring to be used
     */
    SPC(String str){
        nV = Character.getNumericValue(str.charAt(0))*2;
        sV = Character.getNumericValue(str.charAt(1))*2;
        wV = Character.getNumericValue(str.charAt(2))*2;
        eV = Character.getNumericValue(str.charAt(3))*2;
    }

    @Override
    public String toString(){
        return nV+""+sV+""+wV+""+eV;
    }

    //for origin total use purpose only
    public boolean isUsed(){
        return (nV+sV+wV+eV == 8);
    }
}

public class Main {

    /**Returns the length of the longest common subsequence shared between Strings a & b
     * @param a : the chosen String
     * @param b : the String to be compared
     * @return int length of longest common subsequence
     */
    public static int longSubLength(String a, String b){
        int[][] mat = new int[a.length()][b.length()];

        for(int[] row : mat){
            for( int ind : row){
                ind = 0;
            }
        }

        for(int i = 1; i < a.length(); i++){
            for(int j = 1; j < b.length(); j++){
                if(a.charAt(i) == b.charAt(j)){
                    mat[i][j] = mat[i-1][j-1]+1;
                }else{
                    mat[i][j] = Math.max(mat[i][j-1],mat[i-1][j]);
                }
            }
        }
        return mat[a.length()-1][b.length()-1];
    }

    /**
     * Starting from 0,0 go from space to space marking directions as used (++).
     * Use direction only if 0 or 1, prioritize 0, in order of N -> S -> W -> E
     * @param maze
     * @return String representing the path taken to traverse the entire maze
     */
    public static String buildPath(SPC[][] maze){
        String path = "";
        int i = 0 , j = 0;
        while(!maze[0][0].isUsed()){
            //add direction to path and increment proper variables. follow priority given by PDF directions
            if(maze[i][j].nV == 0){
                path = path.concat("n");
                maze[i][j].nV++;
                maze[--i][j].sV++;
                continue;
            }else if(maze[i][j].sV == 0){
                path = path.concat("s");
                maze[i][j].sV++;
                maze[++i][j].nV++;
                continue;
            }else if(maze[i][j].wV == 0){
                path = path.concat("w");
                maze[i][j].wV++;
                maze[i][--j].eV++;
                continue;
            }else if(maze[i][j].eV == 0){
                path = path.concat("e");
                maze[i][j].eV++;
                maze[i][++j].wV++;
                continue;
            }else if(maze[i][j].nV == 1){
                path = path.concat("n");
                maze[i][j].nV++;
                maze[--i][j].sV++;
                continue;
            }else if(maze[i][j].sV == 1){
                path = path.concat("s");
                maze[i][j].sV++;
                maze[++i][j].nV++;
                continue;
            }else if(maze[i][j].wV == 1){
                path = path.concat("w");
                maze[i][j].wV++;
                maze[i][--j].eV++;
                continue;
            }else if(maze[i][j].eV == 1){
                path = path.concat("e");
                maze[i][j].eV++;
                maze[i][++j].wV++;
                continue;
            }
        }
        return path;
    }

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();

        FileReader f = new FileReader("input.txt");
        BufferedReader r = new BufferedReader(f);

        //first line: how many mazes
        //second line: n x n size of matrices
        int M, N;
        M = Integer.parseInt(r.readLine());
        N = Integer.parseInt(r.readLine());

        //array of maze, spaces "SPCs"
        SPC[][][] mazes = new SPC[M][N][N];
        String currLine;
        for(int mazeNum = 0; mazeNum < M; mazeNum++){
            for(int i = 0; i < N; ++i){
                //read maze line by line with trim, break into SPCs
                currLine = r.readLine().trim();
                for(int j = 0; j < N*4; j+=4){
                    SPC currSpace = new SPC(currLine.substring(j,j+4));
                    mazes[mazeNum][i][j/4] = currSpace;
                }
            }
            r.readLine(); // throw away empty line between matrices of SPCs
        }
				long mazeStart = System.nanoTime();
				
        //all paths that will be compared to each other
        String[] paths = new String[M];
        for(int mazeNum = 0; mazeNum < M; mazeNum++){
            paths[mazeNum] = buildPath(mazes[mazeNum]);
        }
				long mazeEnd  = System.nanoTime();
				System.out.println("\n\nBuilding paths complete. Time taken : "+ (mazeEnd-mazeStart)/1000000.0);
        // concat 'a' to beginning of strings so that first characters are actually
        // compared.
				long lcsStart = System.nanoTime();
        ArrayList<mTriple> mazeScores = new ArrayList<>();
        for(int i = 0; i < M-1; i++){
            for(int j = i+1; j < M; j++){
                mazeScores.add( new mTriple(i, j, longSubLength("a"+paths[i],"a"+paths[j])) );
            }
        }
				long lcsEnd = System.nanoTime();
				System.out.println("Computing LCS's complete. Time taken: "+(lcsEnd-lcsStart)/1000000.0);

				long sortStart = System.nanoTime();
        Collections.sort(mazeScores);
				long sortEnd = System.nanoTime();
				System.out.println("Sorting LCS scores complete. Time taken: "+(sortEnd-sortStart)/1000000.0);
        FileWriter o = new FileWriter("output.txt");
        BufferedWriter fOut = new BufferedWriter(o);

        fOut.write(mazeScores.get(0).maze_1 +" "+mazeScores.get(0).maze_2);

        r.close();
        f.close();
        fOut.close();
        long end  = System.nanoTime();
        long duration = end - start;
        System.out.println("\nTotal time in milliseconds: "+duration/1000000.0);
    }
}
