package fr.umlv.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class SeamCarving {
	/**
	 * Create a pgm file with a 2D tab.
	 * @param image the 2D tab of the image
	 * @param filename the name of the new pgm file
	 */
	public static void writepgm(int [][] image, String filename){
		try (BufferedWriter writer = new BufferedWriter( new FileWriter(new File(filename)))){
			int i, j;
			writer.write("P2");
			writer.newLine();
			
			writer.write(Integer.toString(image[0].length) + ' ' + Integer.toString(image.length));
			writer.newLine();
			
			writer.write(Integer.toString(maxTab2D(image)));
			writer.newLine();
			
			for (i = 0; i < image.length; i++){
				for (j = 0; j < image[i].length; j++)
					writer.write (Integer.toString(image[i][j]) + ' ');
				writer.newLine();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
	}

	private static int maxTab2D(int img [][]){
		int max;
		max = img[0][0];
		int i,j;
		for (i = 0; i < img.length; i++)
			for (j = 0; j < img[i].length; j++)
				if (max < img[i][j])
					max = img[i][j];
		
		return max;		
	}
	
	/**
	 * Read a pgm file and return a 2D tab of the file.
	 * @param path where is the file.
	 * @return a 2D tab of the image
	 * @throws IOException throw an IOException
	 */
	public static int[][] readpgm(Path path) throws IOException {
		try(BufferedReader reader = Files.newBufferedReader(path)) {
			reader.readLine();// magic
			
			String line = reader.readLine();
			while (line.startsWith("#"))
				line = reader.readLine();
			
			Scanner scanner = new Scanner(line);
			int width = scanner.nextInt();
			int height = scanner.nextInt();
			 
			line = reader.readLine();
			scanner = new Scanner(line);
			scanner.nextInt();// maxVal
	
			int[][] im = new int[height][width];
			scanner = new Scanner(reader);
			int count = 0;
			while (count < height * width) {
				im[count / width][count % width] = scanner.nextInt();
				count++;
			}
			return im;
		}
	}

	/**
	 * Create a new 2D tab with the interest calculated from the pix of the image.
	 * @param image the 2D tab with the value of each pix
	 * @return a 2D tab of the interest of the image.
	 */
	public static int[][] interest (int[][] image) {
		int[][] tmp = new int[image.length][image[0].length];
//System.out.println(image.length + " " + image[0].length);
		for (int i = 0 ; i < image.length ; i++) {
			int j;
			if (image[i].length >= 2) {
				tmp[i][0] = Math.abs(image[i][0] - image[i][1]); 
	
				for (j = 1 ; j < tmp.length - 1; j++) {
					tmp[i][j] = Math.abs(image[i][j] - ((image[i][j-1] + image[i][j+1])/2));
				}
		
				tmp[i][j] = Math.abs(image[i][j-1] - image[i][j]);
			}
			else {
				tmp[i][0] = image[i][0];
			}
		}
	 
		return image; 
	}

	/**
	 * Print in the console the 2D tab given in argument
	 * @param table the 2D tab you want to print.
	 */
	public static void print2DTable (int table[][]) {
		for (int i = 0 ; i < table.length ; i++) {
			for (int j = 0 ; j < table[i].length ; j++)
				System.out.print(table[i][j] + " ");
			System.out.println();	
		}
	}
	
	private static boolean toRemoveAt (int i, ArrayList<Edge> a) {
		return a.stream().anyMatch(e -> e.from == i);
	}
	
	public static void main(String[] args) {
		if (args.length != 2){
			System.err.println("Usage : java ./SeamCarving [image path] [number of pixels]");
			return;
		}
		Path path = Paths.get(args[0]); // TODO Image arg
		Graph g;
		int img[][];
		int reduced[][];
		int count = Integer.parseInt(args[1]);
		ArrayList<Edge> tmp = new ArrayList<>();
//		System.out.println("Test");
		
		try {
			img = readpgm(path);
			reduced = img;
			
			for (int i = 0 ; i < count ; i++) {//TODO Limite max arg
				int test;
				
				//TODO Awful
				if (img.length - i <= 0) break;
				
				g = Graph.toGraph(interest(reduced));

				System.out.println("Vertices count: " + g.vertices());
				tmp = g.minimalCut();
				System.out.println("\nMinimal cut value:" + tmp.stream().map(e -> e.used).reduce(0, Integer::sum));
				
				reduced = new int[img.length][img[0].length - i];
				
				// Copy new image
				for (int j = 0 ; j < reduced.length ; j++) {						
					test = 0;
					
					for (int k = 0 ; k < reduced[0].length ; k++) {
						if (toRemoveAt(k*reduced.length + j + 1, tmp)) test++;
						reduced[j][k] = img[j][k + test];
					}
				}
				img = reduced;
			}

			writepgm (img, "new" + args[0]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Generation ended.");
	}
}
