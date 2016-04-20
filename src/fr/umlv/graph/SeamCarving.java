package fr.umlv.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class SeamCarving {
  public static int[][] readpgm(Path path) throws IOException {
    try(BufferedReader reader = Files.newBufferedReader(path)) {
      reader.readLine();  // magic
      
      String line = reader.readLine();
      while (line.startsWith("#")) {
        line = reader.readLine();
      }
      
      Scanner scanner = new Scanner(line);
      int width = scanner.nextInt();
      int height = scanner.nextInt();
      
      line = reader.readLine();
      scanner = new Scanner(line);
      scanner.nextInt();  // maxVal
      
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
		  
	  if (image[i].length > 2) {
		  tmp[0] = Math.abs(image[i][0] - image[i][1]); 
	  
		  for (j = 1 ; j < image[i].length - 1; j++) {
			  tmp[j] = Math.abs(image[i][j] - (image[i][j-1] + image[i][j+1])/2);
		  }
		  
		  tmp[j] = Math.abs(image[i][j-1] - image[i][j]);
	  }
	  
	  for (j = 0 ; j < image[i].length ; j++) {
		  image[i][j] = tmp[j];
	  }  
	}
	 
	return image; 
  }
}
