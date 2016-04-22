package fr.umlv.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class SeamCarving {
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

	public static int[][] interest (int[][] image) {
		int[] tmp = new int[image.length];
	
		for (int i = 0 ; i < image.length ; i++) {
			int j;
		
			if (image[i].length >= 2) {
				tmp[0] = Math.abs(image[i][0] - image[i][1]); 
	
				for (j = 1 ; j < image[i].length - 1; j++)
					tmp[j] = Math.abs(image[i][j] - ((image[i][j-1] + image[i][j+1])/2));
		
				tmp[j] = Math.abs(image[i][j-1] - image[i][j]);
			}
			else {
				tmp[0] = image[i][0];
			}
	
			for (j = 0 ; j < image[i].length ; j++)
				image[i][j] = tmp[j];
		}
	 
		return image; 
	}

	public static void print2DTable (int table[][]) {
		for (int i = 0 ; i < table.length ; i++) {
			for (int j = 0 ; j < table[i].length ; j++)
				System.out.print(table[i][j] + " ");
			System.out.println();	
		}
	}
	
	public static void main(String[] args) {
		Path path = Paths.get("test.pgm");
		int img[][];
		try {
			img = readpgm(path);
			print2DTable (interest(img));
			writepgm (img, "test2.pgm");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("OK");
	}
}
