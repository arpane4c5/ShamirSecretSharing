import java.math.BigInteger;
import library.*;
import java.io.*;
import java.util.*;

class ShamirSecretCombine{
	public static void main(String[] args) {

		BigInteger maxPrime = null;
		int initTotalShares = 0;
		int initNeededShares = 0;
		String initOutput = "";		

		// handling the inputs given by the user
		if(args.length < 1){			
			System.out.println("Usage : java ShamirSecretCombine <directory-containing-output-files>");
			System.exit(0);
		}else{
			initOutput = args[0];
		}

		String filenames[] = null;

		// reading the output-info.txt file
        try{
		    BufferedReader fInput = new BufferedReader(new FileReader(initOutput+"output-info.txt"));
		    LineNumberReader lnRead = new LineNumberReader(new FileReader(initOutput+"output-info.txt"));
		    lnRead.skip(Long.MAX_VALUE);

		    int totalLine = lnRead.getLineNumber();
		    filenames = new String[totalLine-3];

		    String line;
		    int i = 0;
			while((line = fInput.readLine()) != null){
				if(i > 2 && !line.equals("")){
					filenames[i-3] = line;
				}else if(i == 2){
					initNeededShares = Integer.parseInt(line);
				}else if(i == 1){
					initTotalShares = Integer.parseInt(line);
				}else if(i == 0){
					maxPrime = new BigInteger(line);
				}
				i++;
			}
			fInput.close();
			lnRead.close();

			if((maxPrime == null) || (initTotalShares == 0) || (initNeededShares == 0) || (initNeededShares > initTotalShares) || (initTotalShares != (totalLine-3))){								
				System.out.println("output-info.txt File Corrupted");
				System.exit(0);
			}

		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}catch(IOException e){
			System.out.println("IOException");
		}

		// Asking the user to choose random k files for input
		System.out.println(initNeededShares+" files need to be chosen from "+initTotalShares+" total shares. \nEnter integer between 1 and "+initTotalShares+" :");

		int fileNumbers[] = new int[initNeededShares];
		Scanner sc = new Scanner(System.in);

		for (int i = 0; i < initNeededShares ; ) {
			int temp = sc.nextInt();
			if(temp > 0 && temp <= initTotalShares ){
				fileNumbers[i] = temp-1;
				i++;
			}else{
				System.out.println("Please enter integer between 1 and "+initTotalShares+" :");
			}			
		}

		SecretShare[][][] sharesMatrix = null;

		// reading the k files chosen by the user to regenerate the 3D matrix of secretshares
		try{
			int x = 0;
			for(int i : fileNumbers){
				BufferedReader fInput = new BufferedReader(new FileReader(initOutput+filenames[i]));
			    LineNumberReader lnRead = new LineNumberReader(new FileReader(initOutput+filenames[i]));
			    lnRead.skip(Long.MAX_VALUE);
			    String line;
			    int y = 0;
				while((line = fInput.readLine()) != null){
					String lineWords[] = line.split(" ");
					if(sharesMatrix == null){						
						sharesMatrix = new SecretShare[lineWords.length][lnRead.getLineNumber()][fileNumbers.length];
					}
					for (int z=0;z<lineWords.length;z++) {
						sharesMatrix[z][y][x] = new SecretShare(i,new BigInteger(lineWords[z])); 
					}
					y++;
				}
				fInput.close();
				lnRead.close();
				x++;
			}
		}catch(FileNotFoundException e){
			System.out.println("FileNotFoundException");
		}catch(IOException e){
			System.out.println("IOException");
		}

		// combining the 3D matrix
        final Shamir shamir = new Shamir(initNeededShares,initTotalShares);
        final BigInteger result[][] = shamir.combineMatrix2D(sharesMatrix,maxPrime);

        // printing the output
        System.out.println("Secret : ");
        for (int i = 0; i < result.length; i++) {
        	for (int j = 0; j<result[0].length ; j++) {        		
        		System.out.print(result[i][j]+"  ");	
        	}
        	System.out.println();
        }
	}
}