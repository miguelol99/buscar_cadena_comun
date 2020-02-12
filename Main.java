//Autores: Miguel Nuñez Saiz y Alex Richard Muñoz

package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	static final int TEST = 1;
    
    static long count;
    static int wordsAdded;
    static double tiempoInicio;
    static double tiempoFinal;
    static int indexObjetivo;
    static int indexMaestro;
    
    /**
     * Devuelve el hashcode de un string acotado en un tamaño
     */
    static int hashCode(String word, int size){       
      return Math.abs(word.hashCode()) % size;
    }
    
    /**
     * Crea una hashTable a partir de una lista y un tamaño pasados como argumentos
     */
    static public Pair[] hashTable(List<String> diccionario, int size) {
    	Pair[] hashTable = new Pair[size];
    	
    	for(int index = 0; index < diccionario.size(); index++) {
    		String word = diccionario.get(index);
    		int position = hashCode(word, size);
    		  		
    		while(hashTable[position] != null) {
    			count++;
    			position = (position + 1) % size;
    		}
    		hashTable[position] = new Pair(index, word);
    	}
    	return hashTable;
    }
    
    /**
     * Completa el hashTable con las palabras del objetivo, actualizando el diccionario.
     * Convierte laspalabras del objetivo en sus indices en el diccionario.
     */
    static public Pair[] completeHashTable(Pair[] hashTable, List<String> objetivo, List<String> diccionario) {
    	int size = hashTable.length;
    	boolean already_exist = false;
    	
    	for(int index = 0; index < objetivo.size(); index++) {
    		String word = objetivo.get(index);
    		int position = hashCode(word, size);
    		
    		while(hashTable[position] != null) {
    			count++;
    			if(hashTable[position].getValue().equals(word)) {
    				objetivo.set(index, "" + hashTable[position].getIndex() + "");
    				already_exist = true;
    				break;
    			}
    			position = (position + 1) % size;
    		}
    		if(already_exist == false) {
    			diccionario.add(word);
    			hashTable[position] = new Pair(diccionario.size()-1, word);
    			objetivo.set(index, "" + hashTable[position].getIndex() + ""); 
    			wordsAdded++;
    		}
    		already_exist = false;
    	}
    	return hashTable;
    }
    
    /**
     * Lee un archivo y convierte sus lineas en una lista de strings
     */
    static List<String> readFile(String path) throws IOException{
        Path pathFile = Paths.get(path);           
        return java.nio.file.Files.readAllLines(pathFile);
    }
    
    
    /**
     * Lee un archivo y convierte su unica linea en una lista de strings
     */
    static List<String> readObjetivo(String path) throws IOException{
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        List<String> objetivo = new ArrayList<>();  
        
        String[] words = br.readLine().split(" "); br.close();
        
        for(String word:words) {
            objetivo.add(word);               
        }       
        return objetivo;
    }
    
    /**
     * Trunca una lista a un tamaño determinado pasado como parametro
     */
    public static List<String> truncateList (List<String> list, int number){        
        return list.subList(0, number);    
    }
    
    /**
     * Busca la mayor subcadena comun entre la lista objetivo y maestro.
     * Devuelve su longitud y actualiza las variables globales de indexObjetivo e indexMaestro
     */
	public static int longerSubstring(List<String> objetivo, List<String> maestro) {
		int lenght = 0, maxLenght = 0;

		for (int i = 0; i < objetivo.size(); i++) {

			for (int j = 0; j < maestro.size(); j++) {

				for (int k = 0; (i+k) < objetivo.size() && (j+k) < maestro.size(); k++) {
					count++;
					if (objetivo.get(i+k).equals(maestro.get(j+k)))
						lenght++; 
					else 
						break;
				}
				
				if (lenght > maxLenght) {
					maxLenght = lenght;
					indexObjetivo = i;
					indexMaestro = j;
				}
				lenght = 0;
			}
		}
		return maxLenght;
	}
    
	public static void main(String[] args) throws IOException {
        
    	Scanner teclado = new Scanner (System.in);
    	     
    	//Pide al usuario los ficheros objetivo y maestro y pregunta si se desea truncar el maestro
        System.out.print("Fichero diccionario: ");       
        List<String> diccionario = readFile(teclado.nextLine());             
        System.out.print("Fichero maestro: ");      
        List<String> maestro = readFile(teclado.nextLine());       
        System.out.print("Maestro: " + maestro.size() + " palabras. ");
        System.out.println("Diccionario: " + diccionario.size() + " palabras.");   
        System.out.print("Desea reducir el fichero Maestro? [S/N]: ");      
        if(teclado.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Numero de palabras: ");           
            maestro = truncateList(maestro, teclado.nextInt()); teclado.nextLine();
        }
        
        //Pide al usuario el fichero objetivo y pregunta si se desea truncar
        System.out.print("Fichero objetivo: ");
        List<String> objetivo = readObjetivo(teclado.nextLine());       
        System.out.println(objetivo.size() + " palabras.");        
        System.out.print("Desea reducir el fichero Objetivo? [S/N]: "); 
        if(teclado.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Numero de palabras: ");
            objetivo = truncateList(objetivo, teclado.nextInt()); teclado.close();
        }
        
  
        System.out.println(); System.out.println();
        System.out.println("--- NORMALIZANDO FICHERO OBJETIVO ---");
        
        tiempoInicio = System.currentTimeMillis();
        
        int size = diccionario.size() * 2;
        Pair[] hashTable = hashTable(diccionario, size);  
	     
	    hashTable = completeHashTable(hashTable, objetivo, diccionario);

	    tiempoFinal = System.currentTimeMillis();
	    
	    System.out.println("\nSe han anadido " + wordsAdded + " palabras al diccionario");
	    System.out.println("Numero de comparaciones: " + count);
	    System.out.println("Tiempo: " + (tiempoFinal - tiempoInicio)/1000 + " seg.");
	    count = 0;
	    
        System.out.println(); System.out.println();
        System.out.println("--- BUSCANDO MAYOR SUBCADENA COMUN ---");
        
        tiempoInicio = System.currentTimeMillis();
        
        int longitud = longerSubstring(objetivo, maestro);
        
        System.out.println("La mayor subcadena tiene " + longitud + " palabras");      

        for(int i = indexObjetivo; i < indexObjetivo + longitud; i++) {
        	int position = Integer.parseInt(objetivo.get(i));        	
        	 System.out.print(diccionario.get(position) + " ");
        } 	
        System.out.println();
        
        tiempoFinal = System.currentTimeMillis();
        
        System.out.println("Maestro: posicion " + indexMaestro);
        System.out.println("Objetivo: posicion " + indexObjetivo);
        System.out.println("Numero de comparaciones: " + count);
	    System.out.println("Tiempo: " + (tiempoFinal - tiempoInicio)/1000 + " seg.");
    }
}
